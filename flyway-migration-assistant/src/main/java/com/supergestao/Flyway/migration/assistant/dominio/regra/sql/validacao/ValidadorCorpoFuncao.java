package com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao;

import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.antlr.PostgreSQLLexer;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.antlr.PostgreSQLParser;
import com.supergestao.Flyway.migration.assistant.exception.SqlException;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Valida o conteúdo PL/pgSQL dentro de blocos $BODY$...$BODY$ ou $$...$$
 * executando análise estrutural de blocos e validação das instruções DML.
 */
public class ValidadorCorpoFuncao {

    // Detecta qualquer variação de dollar quote: $$, $BODY$, $FUNCTION$, etc.
    private static final Pattern PATTERN_DOLLAR = Pattern.compile(
            "(\\$(\\w*)\\$)(.*?)\\1",
            Pattern.DOTALL | Pattern.CASE_INSENSITIVE
    );

    // Sanitização de cabeçalhos PL/pgSQL para evitar falsos negativos (Bypass) e erros de parse
    private static final Pattern PATTERN_PL_HEADER = Pattern.compile(
            "^\\s*(?:BEGIN|DECLARE|ELSE|EXCEPTION|" +
                    "IF\\b.*?\\bTHEN|" +
                    "ELSIF\\b.*?\\bTHEN|" +
                    "WHEN\\b.*?\\bTHEN|" +
                    "FOR\\b.*?\\bLOOP|" +
                    "WHILE\\b.*?\\bLOOP|" +
                    "FOREACH\\b.*?\\bLOOP|" +
                    "END\\s+IF|END\\s+LOOP|END\\s+CASE|END" +
                    ")\\b\\s*",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    // -------------------------------------------------------------------------
    // Classes internas auxiliares
    // -------------------------------------------------------------------------

    private static class ControlBlock {
        String type;
        int line;
        int col;
        int offset;

        ControlBlock(String type, int line, int col, int offset) {
            this.type   = type;
            this.line   = line;
            this.col    = col;
            this.offset = offset;
        }
    }

    /** Exceção interna usada apenas para capturar linha/coluna do ANTLR. */
    private static class SqlInternoException extends RuntimeException {
        final int line;
        final int col;

        SqlInternoException(int line, int col, String msg) {
            super(msg);
            this.line = line;
            this.col  = col;
        }
    }

    // =========================================================================
    // PONTO DE ENTRADA
    // =========================================================================

    public void validar(String sqlCompleto) {
        if (sqlCompleto == null || sqlCompleto.isBlank()) return;

        Matcher matcher = PATTERN_DOLLAR.matcher(sqlCompleto);
        while (matcher.find()) {
            int    inicioCorpo    = matcher.start(3);
            String conteudoCorpo  = matcher.group(3);

            if (conteudoCorpo == null || conteudoCorpo.isBlank()) continue;

            // Validações de ortografia e estrutura da seção DECLARE
            validarSecaoDeclare(sqlCompleto, conteudoCorpo, inicioCorpo);
            validarVariaveisTrigger(sqlCompleto, conteudoCorpo, inicioCorpo);
            validarErrosComunsFuncoes(sqlCompleto, conteudoCorpo, inicioCorpo);

            // 1. Valida estruturas de controle (IF/THEN/ELSIF/LOOP/BEGIN/END)
            validarEstruturaControle(sqlCompleto, conteudoCorpo, inicioCorpo);

            // 2. Valida e extrai cursores da seção DECLARE
            String conteudoSemCursores = validarEExtrairCursores(sqlCompleto, conteudoCorpo, inicioCorpo);

            // 3. Valida os comandos DML/SQL restantes no corpo
            validarComandosSqlInternos(sqlCompleto, conteudoSemCursores, inicioCorpo);
        }
    }

    // =========================================================================
    // VALIDAÇÃO DA SEÇÃO DECLARE (E PONTUAÇÃO DE ;)
    // =========================================================================

    private void validarSecaoDeclare(String script, String conteudoCorpo, int inicioCorpo) {
        // Encontra o bloco entre o DECLARE e o BEGIN
        Pattern patternDeclare = Pattern.compile("\\bDECLARE\\b(.*?)\\bBEGIN\\b", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = patternDeclare.matcher(conteudoCorpo);
        if (matcher.find()) {
            int startSecao = matcher.start(1);
            String secao = matcher.group(1);
            String secaoLimpa = limparComentariosEStrings(secao);

            int startPos = 0;
            int endPos;
            while ((endPos = secaoLimpa.indexOf(';', startPos)) != -1) {
                String declOriginal = secao.substring(startPos, endPos);
                String declLimpa = secaoLimpa.substring(startPos, endPos).trim();
                startPos = endPos + 1;

                if (declLimpa.isEmpty()) continue;

                // Verifica se existem múltiplos comandos de declaração na mesma instrução (sinal de ; ausente)
                String[] linhas = declOriginal.split("\\n");
                int linhasComDeclaracao = 0;
                for (String linha : linhas) {
                    String linhaLimpa = limparComentariosEStrings(linha).trim();
                    if (linhaLimpa.isEmpty()) continue;

                    // Se a linha começa com o padrão: "identificador [CONSTANT] outro_identificador_ou_tipo"
                    if (linhaLimpa.matches("^[a-zA-Z_][a-zA-Z0-9_]*\\s+(?:CONSTANT\\s+)?(?:[a-zA-Z_][a-zA-Z0-9_]*|\"[^\"]+\").*")) {
                        linhasComDeclaracao++;
                    }
                }

                if (linhasComDeclaracao > 1) {
                    int offsetAbsoluto = inicioCorpo + startSecao + startPos - declOriginal.length();
                    int[] pos = obterLinhaEColuna(script, offsetAbsoluto);
                    throw new SqlException(String.format(
                            "Erro na linha: %d coluna: %d \n Mensagem: Falta ponto e vírgula (;) ou erro de sintaxe na declaração de variáveis.",
                            pos[0], pos[1]));
                }
            }

            // Verifica se sobrou alguma declaração sem ponto e vírgula antes do BEGIN
            String restante = secaoLimpa.substring(startPos).trim();
            if (!restante.isEmpty()) {
                int offsetAbsoluto = inicioCorpo + startSecao + startPos;
                int[] pos = obterLinhaEColuna(script, offsetAbsoluto);
                throw new SqlException(String.format(
                        "Erro na linha: %d coluna: %d \n Mensagem: Falta ponto e vírgula (;) na última declaração antes do 'BEGIN'.",
                        pos[0], pos[1]));
            }
        }
    }

    // =========================================================================
    // VALIDAÇÃO DE ORTOGRAFIA DE FUNÇÕES E VARIÁVEIS ESPECIAIS
    // =========================================================================

    private void validarVariaveisTrigger(String script, String conteudoCorpo, int inicioCorpo) {
        Set<String> validTgs = Set.of(
                "TG_OP", "TG_TABLE_NAME", "TG_TABLE_SCHEMA", "TG_NAME",
                "TG_WHEN", "TG_LEVEL", "TG_ARGV", "TG_NARGS"
        );
        Pattern patternTg = Pattern.compile("\\b(TG_\\w+)\\b", Pattern.CASE_INSENSITIVE);
        Matcher matcher = patternTg.matcher(conteudoCorpo);
        while (matcher.find()) {
            String tgVar = matcher.group(1).toUpperCase();
            if (!validTgs.contains(tgVar)) {
                int offsetAbsoluto = inicioCorpo + matcher.start(1);
                int[] pos = obterLinhaEColuna(script, offsetAbsoluto);
                throw new SqlException(String.format(
                        "Erro na linha: %d coluna: %d \n Mensagem: Variável especial de trigger inválida ou incorreta: '%s'. Você quis dizer TG_OP ou TG_TABLE_NAME?",
                        pos[0], pos[1], matcher.group(1)));
            }
        }
    }

    private void validarErrosComunsFuncoes(String script, String conteudoCorpo, int inicioCorpo) {
        Map<String, String> typos = Map.ofEntries(
                Map.entry("SUBSTRING", "SUBSTRING"),
                Map.entry("SUBSTRNG", "SUBSTRING"),
                Map.entry("SUBSTRI", "SUBSTRING"),
                Map.entry("COALESCE", "COALESCE"),
                Map.entry("COLESCE", "COALESCE"),
                Map.entry("COALESE", "COALESCE"),
                Map.entry("COALESC", "COALESCE"),
                Map.entry("UPPER", "UPPER"),
                Map.entry("UPER", "UPPER"),
                Map.entry("UPPR", "UPPER"),
                Map.entry("LOWER", "LOWER"),
                Map.entry("LOWR", "LOWER"),
                Map.entry("LOWRE", "LOWER")
        );

        Pattern patternFuncao = Pattern.compile("\\b(\\w+)\\s*\\(", Pattern.CASE_INSENSITIVE);
        Matcher matcher = patternFuncao.matcher(conteudoCorpo);
        while (matcher.find()) {
            String func = matcher.group(1).toUpperCase();
            if (typos.containsKey(func) && !func.equals(typos.get(func))) {
                int offsetAbsoluto = inicioCorpo + matcher.start(1);
                int[] pos = obterLinhaEColuna(script, offsetAbsoluto);
                throw new SqlException(String.format(
                        "Erro na linha: %d coluna: %d \n Mensagem: Função do sistema incorreta: '%s'. Você quis dizer %s?",
                        pos[0], pos[1], matcher.group(1), typos.get(func)));
            }
        }
    }

    // =========================================================================
    // 1. VALIDAÇÃO DE ESTRUTURAS DE CONTROLE
    // =========================================================================

    private void validarEstruturaControle(String script, String conteudoCorpo, int inicioCorpo) {
        Set<String> labels = new HashSet<>();
        Matcher matcherLabels = Pattern.compile("<<(\\w+)>>", Pattern.CASE_INSENSITIVE)
                .matcher(conteudoCorpo);
        while (matcherLabels.find()) {
            labels.add(matcherLabels.group(1).toUpperCase());
        }

        String conteudoLimpo = limparComentariosEStrings(conteudoCorpo);

        Pattern patternEstruturas = Pattern.compile(
                "\\b(END\\s+IF|END\\s+LOOP|END\\s+CASE|END|BEGIN|IF|ELSIF|THEN|LOOP|CASE)\\b",
                Pattern.CASE_INSENSITIVE
        );
        Matcher matcher = patternEstruturas.matcher(conteudoLimpo);

        Stack<ControlBlock> stack = new Stack<>();
        int skipUntilOffset = -1;

        while (matcher.find()) {
            int offsetNoCorpo = matcher.start();
            if (offsetNoCorpo < skipUntilOffset) continue;

            String token         = matcher.group(1).toUpperCase().replaceAll("\\s+", " ");
            int    offsetAbsoluto = inicioCorpo + offsetNoCorpo;
            int[]  pos            = obterLinhaEColuna(script, offsetAbsoluto);

            if (!token.equals("THEN")) {
                verificarThenPendente(stack, pos[0], pos[1]);
            }

            switch (token) {
                case "BEGIN":
                case "LOOP":
                case "CASE":
                    stack.push(new ControlBlock(token, pos[0], pos[1], offsetAbsoluto));
                    break;

                case "IF":
                    stack.push(new ControlBlock("IF_WITHOUT_THEN", pos[0], pos[1], offsetAbsoluto));
                    break;

                case "ELSIF":
                    if (stack.isEmpty() || !stack.peek().type.equals("IF")) {
                        throw new SqlException(String.format(
                                "Erro na linha: %d coluna: %d \n Mensagem: Instrução 'ELSIF' fora de um bloco 'IF' aberto.",
                                pos[0], pos[1]));
                    }
                    stack.push(new ControlBlock("ELSIF_WITHOUT_THEN", pos[0], pos[1], offsetAbsoluto));
                    break;

                case "THEN": {
                    if (stack.isEmpty()) {
                        throw new SqlException(String.format(
                                "Erro na linha: %d coluna: %d \n Mensagem: Instrução 'THEN' sem correspondente 'IF' ou 'ELSIF'.",
                                pos[0], pos[1]));
                    }
                    ControlBlock topo = stack.peek();
                    if (topo.type.equals("CASE")) {
                        // THEN pertence a WHEN ... THEN dentro de CASE — mantém CASE na pilha
                    } else if (topo.type.equals("IF_WITHOUT_THEN") || topo.type.equals("ELSIF_WITHOUT_THEN")) {
                        // Valida a condição entre IF/ELSIF e THEN
                        int    startCond         = topo.offset + topo.type.replace("_WITHOUT_THEN", "").length();
                        int    endCond           = offsetAbsoluto;
                        String condicaoOriginal  = script.substring(startCond, endCond);
                        String condicao          = condicaoOriginal.trim();
                        int    leadingSpaces     = condicaoOriginal.length() - condicaoOriginal.stripLeading().length();

                        if (!condicao.isEmpty()) {
                            String sqlParaValidar = "SELECT " + condicao;
                            try {
                                validarInstrucaoSqlInterna(sqlParaValidar);
                            } catch (SqlInternoException e) {
                                int offsetErro = calcularOffsetNoScript(
                                        sqlParaValidar, e.line, e.col,
                                        startCond + leadingSpaces, 7);
                                int[] posErro = obterLinhaEColuna(script, offsetErro);
                                throw new SqlException(String.format(
                                        "Erro na linha: %d coluna: %d \n Mensagem: Erro de sintaxe na condição do %s: %s",
                                        posErro[0], posErro[1],
                                        topo.type.replace("_WITHOUT_THEN", ""), e.getMessage()));
                            } catch (Exception e) {
                                throw new SqlException(String.format(
                                        "Erro na linha: %d coluna: %d \n Mensagem: Erro na condição do %s: %s",
                                        topo.line, topo.col, topo.type.replace("_WITHOUT_THEN", ""), e.getMessage()));
                            }
                        }

                        if (topo.type.equals("IF_WITHOUT_THEN")) {
                            topo.type = "IF";
                        } else {
                            stack.pop(); // remove ELSIF_WITHOUT_THEN; IF pai permanece
                        }
                    } else {
                        throw new SqlException(String.format(
                                "Erro na linha: %d coluna: %d \n Mensagem: Instrução 'THEN' inesperada nesta posição.",
                                pos[0], pos[1]));
                    }
                    break;
                }

                case "END IF":
                    validarEPop(stack, "IF", pos[0], pos[1]);
                    skipUntilOffset = matcher.end();
                    break;

                case "END LOOP":
                    validarEPop(stack, "LOOP", pos[0], pos[1]);
                    skipUntilOffset = matcher.end();
                    break;

                case "END CASE":
                    validarEPop(stack, "CASE", pos[0], pos[1]);
                    skipUntilOffset = matcher.end();
                    break;

                case "END": {
                    if (!stack.isEmpty() && stack.peek().type.equals("CASE")) {
                        stack.pop();
                        break;
                    }
                    // Lê o próximo token para decidir se é END; ou END label
                    int    k       = matcher.end();
                    while (k < conteudoLimpo.length() && Character.isWhitespace(conteudoLimpo.charAt(k))) k++;

                    String nextWord = "";
                    if (k < conteudoLimpo.length()) {
                        if (Character.isLetterOrDigit(conteudoLimpo.charAt(k)) || conteudoLimpo.charAt(k) == '_') {
                            int startW = k;
                            while (k < conteudoLimpo.length() &&
                                    (Character.isLetterOrDigit(conteudoLimpo.charAt(k)) || conteudoLimpo.charAt(k) == '_')) k++;
                            nextWord = conteudoLimpo.substring(startW, k).toUpperCase();
                        } else {
                            nextWord = String.valueOf(conteudoLimpo.charAt(k));
                        }
                    }

                    if (nextWord.equals(";") || nextWord.isEmpty() || labels.contains(nextWord)) {
                        validarEPop(stack, "BEGIN", pos[0], pos[1]);
                    } else {
                        throw new SqlException(String.format(
                                "Erro na linha: %d coluna: %d \n Mensagem: 'END' seguido por token inesperado: '%s'. "
                                        + "Você quis dizer 'END IF', 'END LOOP' ou 'END CASE'?",
                                pos[0], pos[1], nextWord));
                    }
                    break;
                }
            }
        }

        if (!stack.isEmpty()) {
            ControlBlock topo = stack.pop();
            String tipo = topo.type.replace("_WITHOUT_THEN", "");
            throw new SqlException(String.format(
                    "Erro na linha: %d coluna: %d \n Mensagem: A estrutura de controle '%s' iniciada nesta linha não foi fechada corretamente.",
                    topo.line, topo.col, tipo));
        }
    }

    private void verificarThenPendente(Stack<ControlBlock> stack, int line, int col) {
        if (!stack.isEmpty()) {
            ControlBlock topo = stack.peek();
            if (topo.type.equals("IF_WITHOUT_THEN")) {
                throw new SqlException(String.format(
                        "Erro na linha: %d coluna: %d \n Mensagem: Instrução 'IF' iniciada na linha %d, coluna %d não possui o 'THEN' correspondente.",
                        line, col, topo.line, topo.col));
            }
            if (topo.type.equals("ELSIF_WITHOUT_THEN")) {
                throw new SqlException(String.format(
                        "Erro na linha: %d coluna: %d \n Mensagem: Instrução 'ELSIF' iniciada na linha %d, coluna %d não possui o 'THEN' correspondente.",
                        line, col, topo.line, topo.col));
            }
        }
    }

    private void validarEPop(Stack<ControlBlock> stack, String expectedType, int line, int col) {
        if (stack.isEmpty()) {
            throw new SqlException(String.format(
                    "Erro na linha: %d coluna: %d \n Mensagem: Instrução 'END %s' sem correspondente '%s' aberto.",
                    line, col, expectedType, expectedType));
        }
        ControlBlock topo = stack.peek();
        if (!topo.type.equals(expectedType)) {
            throw new SqlException(String.format(
                    "Erro na linha: %d coluna: %d \n Mensagem: Esperava fechar '%s' (iniciado na linha %d, coluna %d), mas tentou fechar '%s'.",
                    line, col, topo.type, topo.line, topo.col, expectedType));
        }
        stack.pop();
    }

    // =========================================================================
    // 2. VALIDAÇÃO DE CURSORES
    // =========================================================================

    private String validarEExtrairCursores(String script, String conteudoCorpo, int inicioCorpo) {
        // Suporta parâmetros opcionais tanto antes de CURSOR (padrão Oracle) quanto depois (padrão Postgres).
        Pattern patternCursor = Pattern.compile(
                "\\b(\\w+)\\s*(?:\\([^)]*\\))?\\s*CURSOR\\s*(?:\\([^)]*\\))?\\s*(?:FOR|IS)\\s+(.*?);",
                Pattern.DOTALL | Pattern.CASE_INSENSITIVE
        );

        Matcher matcher    = patternCursor.matcher(conteudoCorpo);
        StringBuilder sb   = new StringBuilder(conteudoCorpo);

        while (matcher.find()) {
            int    startQueryNoCorpo = matcher.start(2);
            String queryOriginal     = matcher.group(2);
            String query             = queryOriginal.trim();
            int    leadingSpaces     = queryOriginal.length() - queryOriginal.stripLeading().length();

            if (!query.isEmpty()) {
                try {
                    validarInstrucaoSqlInterna(query);
                } catch (SqlInternoException e) {
                    int offsetErro = calcularOffsetNoScript(
                            query, e.line, e.col,
                            inicioCorpo + startQueryNoCorpo + leadingSpaces, 0);
                    int[] posicao = obterLinhaEColuna(script, offsetErro);
                    throw new SqlException(String.format(
                            "Erro na linha: %d coluna: %d \n Mensagem: Erro de sintaxe no cursor '%s': %s",
                            posicao[0], posicao[1], matcher.group(1), e.getMessage()));
                } catch (SqlException e) {
                    int offsetErro = inicioCorpo + startQueryNoCorpo + leadingSpaces;
                    int[] posicao  = obterLinhaEColuna(script, offsetErro);
                    throw new SqlException(String.format(
                            "Erro na linha: %d coluna: %d \n Mensagem: %s",
                            posicao[0], posicao[1], e.getMessage()));
                }
            }

            for (int i = matcher.start(); i < matcher.end(); i++) {
                if (sb.charAt(i) != '\n') sb.setCharAt(i, ' ');
            }
        }
        return sb.toString();
    }

    // =========================================================================
    // 3. VALIDAÇÃO DE COMANDOS SQL INTERNOS
    // =========================================================================

    private void validarComandosSqlInternos(String script, String conteudoCorpo, int inicioCorpo) {
        String conteudoLimpo = limparComentariosEStrings(conteudoCorpo);

        Set<String> validStarters = Set.of(
                "SELECT", "INSERT", "UPDATE", "DELETE", "IF", "ELSE", "ELSIF", "LOOP", "DECLARE",
                "BEGIN", "END", "FETCH", "OPEN", "CLOSE", "RETURN", "EXIT", "RAISE", "EXECUTE",
                "PERFORM", "FOREACH", "CONTINUE", "WHILE", "FOR", "CALL", "EXCEPTION", "WHEN", "CASE", "GET"
        );

        Map<String, String> keywordsTypos = Map.ofEntries(
                Map.entry("EXCUTE", "EXECUTE"),
                Map.entry("EXECUET", "EXECUTE"),
                Map.entry("PRFORM", "PERFORM"),
                Map.entry("PERFOR", "PERFORM"),
                Map.entry("RETUR", "RETURN"),
                Map.entry("RETUN", "RETURN"),
                Map.entry("OPN", "OPEN"),
                Map.entry("CLSE", "CLOSE"),
                Map.entry("DECLAR", "DECLARE"),
                Map.entry("FORACH", "FOREACH"),
                Map.entry("CONTINE", "CONTINUE"),
                Map.entry("ELIF", "ELSIF"),
                Map.entry("WHIL", "WHILE")
        );

        Map<String, String> bodyTypos = Map.of(
                "USNG", "USING",
                "USIN", "USING",
                "STRCT", "STRICT",
                "STRIT", "STRICT",
                "FOUUND", "FOUND",
                "FOND", "FOUND"
        );

        int startPos = 0;
        int endPos;

        while ((endPos = conteudoLimpo.indexOf(';', startPos)) != -1) {
            String stripLinha = conteudoLimpo.substring(startPos, endPos);
            String stripOriginal = conteudoCorpo.substring(startPos, endPos);
            String instrucaoLimpa    = stripLinha.trim();
            String instrucaoOriginal = stripOriginal.trim();

            // Offset do início desta instrução dentro do conteudoCorpo considerando espaços iniciais
            int offsetInicioInstrucao = startPos
                    + (stripLinha.length() - stripLinha.stripLeading().length());

            startPos = endPos + 1;

            if (instrucaoLimpa.isEmpty()) continue;

            // Sanitização: Substitui estruturas de controle PL/pgSQL no início do fragmento por espaços equivalentes
            String instrucaoParaValidar = instrucaoOriginal;
            Matcher plMatcher = PATTERN_PL_HEADER.matcher(instrucaoParaValidar);
            while (plMatcher.find()) {
                int start = plMatcher.start();
                int end = plMatcher.end();
                StringBuilder spaces = new StringBuilder();
                for (int i = start; i < end; i++) {
                    char c = instrucaoParaValidar.charAt(i);
                    spaces.append(c == '\n' ? '\n' : ' ');
                }
                instrucaoParaValidar = instrucaoParaValidar.substring(0, start) + spaces + instrucaoParaValidar.substring(end);
                plMatcher = PATTERN_PL_HEADER.matcher(instrucaoParaValidar);
            }

            String instrucaoLimpaAux = instrucaoParaValidar.trim();
            if (instrucaoLimpaAux.isEmpty()) {
                continue;
            }

            String[] tokens = instrucaoLimpaAux.split("\\s+");
            if (tokens.length == 0) continue;
            String firstWord = tokens[0].toUpperCase();

            // --- Validação ortográfica de erros no corpo do comando (ex: USNG, STRCT, FOUUND) ---
            for (String token : tokens) {
                String cleanToken = token.replaceAll("[^a-zA-Z_]", "").toUpperCase();
                if (bodyTypos.containsKey(cleanToken)) {
                    int offsetAbsoluto = inicioCorpo + offsetInicioInstrucao + instrucaoOriginal.toUpperCase().indexOf(cleanToken);
                    int[] pos = obterLinhaEColuna(script, offsetAbsoluto);
                    throw new SqlException(String.format(
                            "Erro na linha: %d coluna: %d \n Mensagem: Palavra-chave incorreta: '%s'. Você quis dizer %s?",
                            pos[0], pos[1], token, bodyTypos.get(cleanToken)));
                }
            }

            // --- Atribuição := ---
            if (instrucaoLimpaAux.contains(":=")) {
                int idx = instrucaoOriginal.indexOf(":=");
                if (idx != -1) {
                    String rhsOriginal = instrucaoOriginal.substring(idx + 2);
                    String rhs         = rhsOriginal.trim();
                    int    leadingRhs  = rhsOriginal.length() - rhsOriginal.stripLeading().length();

                    int offsetBaseRhs = inicioCorpo + offsetInicioInstrucao + idx + 2 + leadingRhs;

                    if (!rhs.isEmpty()) {
                        if (rhs.endsWith(";")) {
                            rhs = rhs.substring(0, rhs.length() - 1).trim();
                        }
                        String sqlValidar = "SELECT " + rhs;
                        try {
                            validarInstrucaoSqlInterna(sqlValidar);
                        } catch (SqlInternoException e) {
                            int offsetErro = calcularOffsetNoScript(
                                    sqlValidar, e.line, e.col, offsetBaseRhs, 7);
                            int[] posicao = obterLinhaEColuna(script, offsetErro);
                            throw new SqlException(String.format(
                                    "Erro na linha: %d coluna: %d \n Mensagem: Erro de sintaxe na expressão da atribuição: %s",
                                    posicao[0], posicao[1], e.getMessage()));
                        } catch (SqlException e) {
                            int[] posicao = obterLinhaEColuna(script, offsetBaseRhs);
                            throw new SqlException(String.format(
                                    "Erro na linha: %d coluna: %d \n Mensagem: %s",
                                    posicao[0], posicao[1], e.getMessage()));
                        }
                    }
                }
                continue;
            }

            // --- Validação ortográfica de erros na primeira palavra do comando ---
            if (keywordsTypos.containsKey(firstWord)) {
                int offsetAbsoluto = inicioCorpo + offsetInicioInstrucao;
                int[] pos = obterLinhaEColuna(script, offsetAbsoluto);
                throw new SqlException(String.format(
                        "Erro na linha: %d coluna: %d \n Mensagem: Palavra-chave PL/pgSQL incorreta: '%s'. Você quis dizer %s?",
                        pos[0], pos[1], tokens[0], keywordsTypos.get(firstWord)));
            }

            // Pula validações adicionais de palavras-chave estruturais conhecidas do PL/pgSQL
            if (validStarters.contains(firstWord)) continue;

            // Se não começou com palavra válida do PL/pgSQL e não é atribuição, lança comando inválido
            if (!instrucaoLimpaAux.contains("=") && !instrucaoLimpaAux.contains(":=")) {
                int offsetAbsoluto = inicioCorpo + offsetInicioInstrucao;
                int[] pos = obterLinhaEColuna(script, offsetAbsoluto);
                throw new SqlException(String.format(
                        "Erro na linha: %d coluna: %d \n Mensagem: Comando ou palavra-chave inválida/desconhecida: '%s'.",
                        pos[0], pos[1], tokens[0]));
            }

            // --- SQL puro (SELECT, INSERT, UPDATE, DELETE, etc.) ---
            boolean pareceSql = instrucaoLimpaAux.toUpperCase()
                    .matches(".*\\b(FROM|WHERE|VALUES|SET|JOIN)\\b.*")
                    || firstWord.startsWith("SELE")
                    || firstWord.startsWith("UPDA")
                    || firstWord.startsWith("DELE")
                    || firstWord.startsWith("INSE");

            if (pareceSql) {
                int offsetBaseInstrucao = inicioCorpo + offsetInicioInstrucao;
                try {
                    validarInstrucaoSqlInterna(instrucaoParaValidar);
                } catch (SqlInternoException e) {
                    int offsetErro = calcularOffsetNoScript(
                            instrucaoParaValidar, e.line, e.col,
                            offsetBaseInstrucao, 0);
                    int[] posicao = obterLinhaEColuna(script, offsetErro);
                    throw new SqlException(String.format(
                            "Erro na linha: %d coluna: %d \n Mensagem: Erro de sintaxe: %s",
                            posicao[0], posicao[1], e.getMessage()));
                } catch (SqlException e) {
                    int[] posicao = obterLinhaEColuna(script, offsetBaseInstrucao);
                    throw new SqlException(String.format(
                            "Erro na linha: %d coluna: %d \n Mensagem: %s",
                            posicao[0], posicao[1], e.getMessage()));
                }
            }
        }
    }

    // =========================================================================
    // VALIDAÇÃO VIA ANTLR4
    // =========================================================================

    private void validarInstrucaoSqlInterna(String instrucao) {
        CharStream       input  = CharStreams.fromString(instrucao);
        PostgreSQLLexer  lexer  = new PostgreSQLLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        PostgreSQLParser parser = new PostgreSQLParser(tokens);

        lexer.removeErrorListeners();
        parser.removeErrorListeners();

        BaseErrorListener errorListener = new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                                    int line, int charPositionInLine,
                                    String msg, RecognitionException e) {
                throw new SqlInternoException(line, charPositionInLine, msg);
            }
        };
        lexer.addErrorListener(errorListener);
        parser.addErrorListener(errorListener);

        PostgreSQLParser.RootContext tree = parser.root();
        ParseTreeWalker.DEFAULT.walk(new ValidadorAntlrSql(true), tree);
    }

    // =========================================================================
    // UTILITÁRIOS
    // =========================================================================

    private int calcularOffsetNoScript(String fragmento, int relativeLine, int relativeCol,
                                       int offsetBase, int prefixLength) {
        int offsetInicioLinha = 0;
        int currentLine       = 1;

        for (int i = 0; i < fragmento.length(); i++) {
            if (currentLine == relativeLine) {
                offsetInicioLinha = i;
                break;
            }
            if (fragmento.charAt(i) == '\n') {
                currentLine++;
            }
        }

        int offsetNoFragmento = offsetInicioLinha + relativeCol;

        // O prefixo artificial (ex: "SELECT ") adicionado no início do fragmento desloca
        // o offset absoluto de todos os caracteres subsequentes. Portanto, devemos sempre
        // descontar o prefixLength do offset absoluto final, independente da linha do erro.
        offsetNoFragmento = Math.max(0, offsetNoFragmento - prefixLength);

        return offsetBase + offsetNoFragmento;
    }

    private String limparComentariosEStrings(String sql) {
        char[]  chars          = sql.toCharArray();
        int     n              = chars.length;
        boolean inLineComment  = false;
        boolean inBlockComment = false;
        boolean inString       = false;
        boolean escaped        = false;

        for (int i = 0; i < n; i++) {
            char c = chars[i];

            if (inLineComment) {
                if (c == '\n') inLineComment = false;
                else chars[i] = ' ';

            } else if (inBlockComment) {
                if (c == '*' && i + 1 < n && chars[i + 1] == '/') {
                    chars[i]     = ' ';
                    chars[i + 1] = ' ';
                    i++;
                    inBlockComment = false;
                } else if (c != '\n') {
                    chars[i] = ' ';
                }

            } else if (inString) {
                if (escaped) {
                    escaped = false;
                    if (c != '\n') chars[i] = ' ';
                } else if (c == '\\') {
                    escaped  = true;
                    chars[i] = ' ';
                } else if (c == '\'') {
                    chars[i] = ' ';
                    inString = false;
                } else if (c != '\n') {
                    chars[i] = ' ';
                }

            } else {
                // Detecção de Dollar Quotes ($$ ou $tag$)
                if (c == '$') {
                    int j = i + 1;
                    while (j < n && (Character.isLetterOrDigit(chars[j]) || chars[j] == '_')) {
                        j++;
                    }
                    if (j < n && chars[j] == '$') {
                        String tag = new String(chars, i, j - i + 1);
                        int closingIdx = sql.indexOf(tag, j + 1);
                        if (closingIdx != -1) {
                            for (int k = j + 1; k < closingIdx; k++) {
                                if (chars[k] != '\n') {
                                    chars[k] = ' ';
                                }
                            }
                            i = closingIdx + tag.length() - 1;
                            continue;
                        }
                    }
                }

                if (c == '-' && i + 1 < n && chars[i + 1] == '-') {
                    inLineComment = true;
                    chars[i]     = ' ';
                    chars[i + 1]   = ' ';
                    i++;
                } else if (c == '/' && i + 1 < n && chars[i + 1] == '*') {
                    inBlockComment = true;
                    chars[i]       = ' ';
                    chars[i + 1]   = ' ';
                    i++;
                } else if (c == '\'') {
                    inString = true;
                    chars[i] = ' ';
                }
            }
        }
        return new String(chars);
    }

    private int[] obterLinhaEColuna(String text, int offset) {
        int line = 1;
        int col  = 1;
        for (int i = 0; i < offset && i < text.length(); i++) {
            if (text.charAt(i) == '\n') {
                line++;
                col = 1;
            } else {
                col++;
            }
        }
        return new int[]{line, col};
    }
}
