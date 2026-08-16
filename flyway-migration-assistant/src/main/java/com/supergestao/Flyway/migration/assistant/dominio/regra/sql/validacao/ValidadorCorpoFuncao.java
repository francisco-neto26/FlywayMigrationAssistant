package com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao;

import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemSistema;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.antlr.PostgreSQLLexer;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.antlr.PostgreSQLParser;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.antlr.PostgreSQLParserBaseListener;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.util.PadroesValidacaoSql;
import com.supergestao.Flyway.migration.assistant.exception.SqlException;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidadorCorpoFuncao {

    private static final Pattern PADRAO_DECLARACAO_CURSOR = Pattern.compile(
            "(?m)^[ \\t]*(\\w+)\\s*(?:\\([^)]*\\))?\\s*(CURSOR|CURSO|CRSOR|CUSOR)\\s*(?:\\([^)]*\\))?\\s*(?:FOR|IS)\\s+([\\s\\S]*?);",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern PADRAO_ESTRUTURAS_CONTROLE = Pattern.compile(
            "\\b(END\\s+IF|END\\s+LOOP|END\\s+CASE|END|BEGIN|IF|ELSIF|THEN|LOOP|CASE)\\b",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern PADRAO_CABECALHO_PLPGSQL = Pattern.compile(
            "^\\s*(?:BEGIN|DECLARE|ELSE|EXCEPTION|" +
                    "IF\\b.*?\\bTHEN|ELSIF\\b.*?\\bTHEN|WHEN\\b.*?\\bTHEN|" +
                    "FOR\\b.*?\\bLOOP|WHILE\\b.*?\\bLOOP|FOREACH\\b.*?\\bLOOP|" +
                    "END\\s+IF|END\\s+LOOP|END\\s+CASE|END)\\b\\s*",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    private static final Pattern PADRAO_INICIO_DECLARACAO_VARIAVEL = Pattern.compile(
            "^[a-zA-Z_][a-zA-Z0-9_]*\\s+\\S"
    );

    private static final Pattern PADRAO_VARIAVEIS_TG = Pattern.compile(
            "\\b(TG_\\w+)\\b",
            Pattern.CASE_INSENSITIVE
    );

    private static final Pattern PADRAO_CHAMADA_FUNCAO = Pattern.compile(
            "\\b(\\w+)\\s*\\(",
            Pattern.CASE_INSENSITIVE
    );

    private static final Set<String> VARIAVEIS_TRIGGER_VALIDAS = Set.of(
            "TG_OP", "TG_TABLE_NAME", "TG_TABLE_SCHEMA", "TG_NAME",
            "TG_WHEN", "TG_LEVEL", "TG_ARGV", "TG_NARGS"
    );

    private static final Map<String, String> ERROS_ORTOGRAFICOS_FUNCOES_SISTEMA = Map.ofEntries(
            Map.entry("SUBSTRNG", "SUBSTRING"),
            Map.entry("SUBSTRI", "SUBSTRING"),
            Map.entry("SUBSTING", "SUBSTRING"),
            Map.entry("COLESCE", "COALESCE"),
            Map.entry("COALESE", "COALESCE"),
            Map.entry("COALESC", "COALESCE"),
            Map.entry("COALECE", "COALESCE"),
            Map.entry("COLAESCE", "COALESCE"),
            Map.entry("COALESECE", "COALESCE"),
            Map.entry("UPER", "UPPER"),
            Map.entry("UPPR", "UPPER"),
            Map.entry("UPPE", "UPPER"),
            Map.entry("LOWR", "LOWER"),
            Map.entry("LOWRE", "LOWER"),
            Map.entry("LOEWR", "LOWER"),
            Map.entry("TRM", "TRIM"),
            Map.entry("TIRM", "TRIM"),
            Map.entry("LENGHT", "LENGTH"),
            Map.entry("LENGT", "LENGTH"),
            Map.entry("CST", "CAST"),
            Map.entry("NOw", "NOW"),
            Map.entry("TOCHAR", "TO_CHAR"),
            Map.entry("TO_CHR", "TO_CHAR")
    );

    private static final Map<String, String> ERROS_ORTOGRAFICOS_PALAVRAS_CHAVE_PLPGSQL = Map.ofEntries(
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

    private static final Map<String, String> ERROS_ORTOGRAFICOS_PALAVRAS_CORPO = Map.of(
            "USNG", "USING",
            "USIN", "USING",
            "STRCT", "STRICT",
            "STRIT", "STRICT",
            "FOUUND", "FOUND",
            "FOND", "FOUND"
    );

    private static final Set<String> INICIADORES_VALIDOS_PLPGSQL = Set.of(
            "SELECT", "INSERT", "UPDATE", "DELETE", "IF", "ELSE", "ELSIF",
            "LOOP", "DECLARE", "BEGIN", "END", "FETCH", "OPEN", "CLOSE",
            "RETURN", "EXIT", "RAISE", "EXECUTE", "PERFORM", "FOREACH",
            "CONTINUE", "WHILE", "FOR", "CALL", "EXCEPTION", "WHEN", "CASE", "GET"
    );

    private static final Set<String> COMANDOS_QUE_EXIGEM_PONTO_VIRGULA = Set.of(
            "FETCH", "OPEN", "CLOSE", "RETURN", "RAISE", "PERFORM",
            "EXECUTE", "CALL", "EXIT", "CONTINUE", "GET"
    );

    private static class BlocoControle {
        String tipo;
        final int linha;
        final int coluna;
        final int offsetAbsoluto;

        BlocoControle(String tipo, int linha, int coluna, int offsetAbsoluto) {
            this.tipo = tipo;
            this.linha = linha;
            this.coluna = coluna;
            this.offsetAbsoluto = offsetAbsoluto;
        }
    }

    private static class ErroSintaxeInterno extends RuntimeException {
        final int linha;
        final int coluna;

        ErroSintaxeInterno(int linha, int coluna, String message) {
            super(message);
            this.linha = linha;
            this.coluna = coluna;
        }
    }

    @FunctionalInterface
    private interface FabricaMensagemErro {
        String criar(int linha, int coluna, String mensagem);
    }

    public void validar(String sqlCompleto) {

        if (sqlCompleto == null || sqlCompleto.isBlank()) {
            return;
        }

        String sqlCompletoSemComentario = sqlCompleto;
        Matcher correspondenciaBlocos = PadroesValidacaoSql.FUNCTION.matcher(sqlCompletoSemComentario);

        while (correspondenciaBlocos.find()) {

            int inicioBloco = correspondenciaBlocos.start(3);
            String conteudoBloco = correspondenciaBlocos.group(3);

            if (conteudoBloco == null || conteudoBloco.isBlank()) {
                continue;
            }
            validarDeclare(sqlCompletoSemComentario, conteudoBloco, inicioBloco);
            //validarSecaoDeclare(sqlCompletoSemComentario, conteudoBloco, inicioBloco);

            /*validarVariaveisTrigger(sqlCompleto, conteudoBloco, inicioBloco);
            validarErrosOrtograficosFuncoesSistema(sqlCompleto, conteudoBloco, inicioBloco);
            validarEstruturaControle(sqlCompleto, conteudoBloco, inicioBloco);

            String blocoSemCursores = extrairEValidarCursores(sqlCompleto, conteudoBloco, inicioBloco);
            validarComandosSqlNoCorpo(sqlCompleto, blocoSemCursores, inicioBloco);*/
        }
    }


    //valida o declare, separa o cursor das demais validações
    private void validarDeclare(String scriptCompleto, String conteudoBloco, int inicioBloco) {

        Matcher correspondenciaDeclare = PadroesValidacaoSql.BLOCO_DECLARE.matcher(conteudoBloco);

        if (!correspondenciaDeclare.find()) {
            return;
        }

        int inicioSecao = correspondenciaDeclare.start(1);
        String declareOriginal = correspondenciaDeclare.group(1);
        String cursoresDeclare = cursorDeclare(declareOriginal);
        String declareSemCursores = variaveisDeclare(declareOriginal, cursoresDeclare);

        System.out.println("Declare original:\n" + declareSemCursores);

        /*StringBuilder secaoSemCursores = new StringBuilder(declareOriginal);
        Matcher correspondenciaCursor = PadroesValidacaoSql.DECLARACAO_CURSOR.matcher(secaoDeclareLimpa);

        while (correspondenciaCursor.find()) {
            int offsetBaseCursor = inicioBloco + inicioSecao + correspondenciaCursor.start();
            String declaracaoOriginal = declareOriginal.substring(
                    correspondenciaCursor.start(), correspondenciaCursor.end());

            String palavraCursorDetectada = correspondenciaCursor.group(2).toUpperCase();
            if (!palavraCursorDetectada.equals("CURSOR")) {
                int offsetErroPalavra = offsetBaseCursor + correspondenciaCursor.start(2);
                int[] pos = posicaoAbsoluta(scriptCompleto, offsetErroPalavra);
                throw new SqlException(MensagemSistema.ERRO_PALAVRA_CHAVE_INCORRETA
                        .MensagemComParametro(pos[0], pos[1], correspondenciaCursor.group(2), "CURSOR"));
            }

            validarSqlDoCursor(
                    scriptCompleto,
                    correspondenciaCursor.group(1),
                    declaracaoOriginal,
                    correspondenciaCursor.group(0),
                    offsetBaseCursor,
                    correspondenciaCursor.start(3)
            );

            int fimTrecho = correspondenciaCursor.end();
            if (secaoSemCursores.charAt(fimTrecho - 1) == ';') {
                fimTrecho--;
            }

            apagarTrechoPreservandoQuebras(secaoSemCursores, correspondenciaCursor.start(), correspondenciaCursor.end());
        }

        validarDeclaracoesDeVariaveis(scriptCompleto, declareOriginal, secaoSemCursores.toString(), inicioBloco, inicioSecao);*/
    }

    private String variaveisDeclare(String declareOriginal, String cursorDeclare) {
        ArrayList<String> listaCursores = new ArrayList<>();
        String declareSemCursores = declareOriginal;

        if (cursorDeclare.contains("\n")) {
            String[] cursores = cursorDeclare.split("\n");
            for (String cursor : cursores) {
                if (!cursor.trim().isEmpty()) {
                    listaCursores.add(cursor.trim());
                }
            }
        }

        for (String cursor : listaCursores) {
            declareSemCursores = declareSemCursores.replace(cursor, "");
        }
        return declareSemCursores.lines()
                .filter(linha -> !linha.isBlank())
                .collect(java.util.stream.Collectors.joining("\n"));
    }

    private String cursorDeclare(String declareOriginal) {
        StringBuilder cursores = new StringBuilder();
        int posicaoAtual = 0;
        int posicaoFim;

        while ((posicaoFim = declareOriginal.indexOf(';', posicaoAtual)) != -1) {
            String declareOriginalAjustado = declareOriginal.substring(posicaoAtual, posicaoFim).trim();
            posicaoAtual = posicaoFim + 1;

            if (declareOriginalAjustado.isEmpty()) {
                continue;
            }
            if (declareOriginalAjustado.toUpperCase().contains("CURSOR")) {
                cursores.append(declareOriginalAjustado).append(";\n").append("\n");
            }
        }

        return cursores.toString();
    }

    private void validarDeclareSemCursor(String scriptCompleto, String conteudoBloco, int offsetInicioBloco) {

    }

    private void validarCursorDeclare(String scriptCompleto, String conteudoBloco, int offsetInicioBloco) {

    }


    /**
     * Valida scripts SQL avulsos estruturais executados fora de funções.
     */
    public void validarComandosSqlSoltos(String sqlCompleto) {
        if (sqlCompleto == null || sqlCompleto.isBlank()) return;
        if (PadroesValidacaoSql.FUNCTION.matcher(sqlCompleto).find()) return;
        validarComandosSqlNoCorpo(sqlCompleto, sqlCompleto, 0);
    }

    /**

     private void validarSecaoDeclare(String scriptCompleto, String conteudoBloco, int offsetInicioBloco) {
     Matcher correspondenciaDeclare = BLOCO_DECLARE.matcher(conteudoBloco);
     if (!correspondenciaDeclare.find()){
     return;
     }

     int offsetInicioSecao = correspondenciaDeclare.start(1);
     String secaoDeclareOriginal = correspondenciaDeclare.group(1);
     String secaoDeclareLimpa = removerComentario(secaoDeclareOriginal);

     StringBuilder secaoSemCursores = new StringBuilder(secaoDeclareLimpa);
     Matcher correspondenciaCursor = PADRAO_DECLARACAO_CURSOR.matcher(secaoDeclareLimpa);

     while (correspondenciaCursor.find()) {
     int offsetBaseCursor = offsetInicioBloco + offsetInicioSecao + correspondenciaCursor.start();
     String declaracaoOriginal = secaoDeclareOriginal.substring(
     correspondenciaCursor.start(), correspondenciaCursor.end());

     String palavraCursorDetectada = correspondenciaCursor.group(2).toUpperCase();
     if (!palavraCursorDetectada.equals("CURSOR")) {
     int offsetErroPalavra = offsetBaseCursor + correspondenciaCursor.start(2);
     int[] pos = posicaoAbsoluta(scriptCompleto, offsetErroPalavra);
     throw new SqlException(MensagemSistema.ERRO_PALAVRA_CHAVE_INCORRETA
     .MensagemComParametro(pos[0], pos[1], correspondenciaCursor.group(2), "CURSOR"));
     }

     validarSqlDoCursor(
     scriptCompleto,
     correspondenciaCursor.group(1),
     declaracaoOriginal,
     correspondenciaCursor.group(0),
     offsetBaseCursor,
     correspondenciaCursor.start(3)
     );

     int fimTrecho = correspondenciaCursor.end();
     if (secaoSemCursores.charAt(fimTrecho - 1) == ';') {
     fimTrecho--;
     }

     apagarTrechoPreservandoQuebras(secaoSemCursores, correspondenciaCursor.start(), correspondenciaCursor.end());
     }

     validarDeclaracoesDeVariaveis(scriptCompleto, secaoDeclareOriginal, secaoSemCursores.toString(), offsetInicioBloco, offsetInicioSecao);
     }*/

    /**
     * Valida sintaticamente a query SQL interna associada a um CURSOR.
     */
    private void validarSqlDoCursor(String scriptCompleto, String nomeCursor,
                                    String declaracaoOriginal, String declaracaoLimpa,
                                    int offsetBaseCursor, int offsetGrupoSelectNaLimpa) {
        Matcher correspondenciaCursor = PADRAO_DECLARACAO_CURSOR.matcher(declaracaoLimpa);
        if (!correspondenciaCursor.find()) return;

        String sqlOriginal = declaracaoOriginal.substring(correspondenciaCursor.start(3));
        sqlOriginal = sqlOriginal.endsWith(";") ? sqlOriginal.substring(0, sqlOriginal.length() - 1) : sqlOriginal;

        String sqlTrimado = sqlOriginal.trim();
        int espacosInicio = sqlOriginal.length() - sqlOriginal.stripLeading().length();
        int offsetBaseSelect = offsetBaseCursor + correspondenciaCursor.start(3) + espacosInicio;

        if (sqlTrimado.isEmpty()) return;

        executarValidacaoAntlr(scriptCompleto, sqlTrimado, offsetBaseSelect, 0,
                (linha, coluna, mensagem) -> MensagemSistema.ERRO_SINTAXE_CURSOR
                        .MensagemComParametro(linha, coluna, nomeCursor, mensagem));
    }

    /**
     * Varre as linhas do DECLARE validando a terminação de ponto e vírgula de cada variável.
     */
    private void validarDeclaracoesDeVariaveis(String scriptCompleto, String secaoDeclareOriginal, String secaoSemCursores, int offsetInicioBloco, int offsetInicioSecao) {
        int posicaoAtual = 0;
        int posicaoFim;

        while ((posicaoFim = secaoSemCursores.indexOf(';', posicaoAtual)) != -1) {
            int inicioDeclaracao = posicaoAtual;
            String declaracaoOriginal = secaoDeclareOriginal.substring(inicioDeclaracao, posicaoFim);
            String declaracaoLimpa = secaoSemCursores.substring(inicioDeclaracao, posicaoFim).trim();
            posicaoAtual = posicaoFim + 1;

            if (declaracaoLimpa.isEmpty()) continue;

            validarUmaDeclaracaoPorPontoVirgula(scriptCompleto, declaracaoOriginal, offsetInicioBloco, offsetInicioSecao, inicioDeclaracao);
            validarAtribuicaoDeclaracao(scriptCompleto, declaracaoOriginal, offsetInicioBloco, offsetInicioSecao, inicioDeclaracao);
        }

        String restoAposUltimoPontoVirgula = secaoSemCursores.substring(posicaoAtual).trim();
        if (!restoAposUltimoPontoVirgula.isEmpty()) {
            int offsetAbsoluto = offsetInicioBloco + offsetInicioSecao + posicaoAtual;
            int[] posicao = posicaoAbsoluta(scriptCompleto, offsetAbsoluto);
            throw new SqlException(MensagemSistema.ERRO_DECLARACAO_ANTES_BEGIN.MensagemComParametro(posicao[0], posicao[1]));
        }
    }

    /**
     * Impede que múltiplas variáveis sejam declaradas na mesma instrução sem ponto e vírgula.
     */
    private void validarUmaDeclaracaoPorPontoVirgula(String scriptCompleto, String declaracaoOriginal, int offsetInicioBloco, int offsetInicioSecao, int inicioDeclaracao) {
        int contadorDeclaracoes = 0;
        int offsetPrimeiraDeclaracao = inicioDeclaracao;
        int offsetAcumulado = inicioDeclaracao;

        for (String linha : declaracaoOriginal.split("\\n")) {
            String linhaLimpa = linha.trim();
            if (PADRAO_INICIO_DECLARACAO_VARIAVEL.matcher(linhaLimpa).find()) {
                contadorDeclaracoes++;
                if (contadorDeclaracoes == 1) {
                    offsetPrimeiraDeclaracao = offsetAcumulado + (linha.length() - linha.stripLeading().length());
                }
            }
            offsetAcumulado += linha.length() + 1;
        }

        if (contadorDeclaracoes > 1) {
            int offsetAbsoluto = offsetInicioBloco + offsetInicioSecao + offsetPrimeiraDeclaracao;
            int[] posicao = posicaoAbsoluta(scriptCompleto, offsetAbsoluto);
            throw new SqlException(MensagemSistema.ERRO_DECLARACAO_SEM_PONTO_VIRGULA.MensagemComParametro(posicao[0], posicao[1]));
        }
    }

    /**
     * Valida sintaticamente o valor padrão ou expressão à direita do operador ':='.
     */
    private void validarAtribuicaoDeclaracao(String scriptCompleto, String declaracaoOriginal, int offsetInicioBloco, int offsetInicioSecao, int inicioDeclaracao) {
        int indiceAtribuicao = declaracaoOriginal.indexOf(":=");
        if (indiceAtribuicao == -1) return;

        String ladoDireitoOriginal = declaracaoOriginal.substring(indiceAtribuicao + 2);
        String ladoDireito = ladoDireitoOriginal.trim();
        int espacosLadoDireito = ladoDireitoOriginal.length() - ladoDireitoOriginal.stripLeading().length();
        int offsetBaseLadoDireito = offsetInicioBloco + offsetInicioSecao + inicioDeclaracao + indiceAtribuicao + 2 + espacosLadoDireito;

        if (ladoDireito.isEmpty()) return;

        executarValidacaoAntlr(scriptCompleto, "SELECT " + ladoDireito, offsetBaseLadoDireito, 7,
                MensagemSistema.ERRO_SINTAXE_ATRIBUICAO::MensagemComParametro);
    }

    /**
     * Extrai e valida a integridade de cursores declarados de forma dinâmica.
     */
    private String extrairEValidarCursores(String scriptCompleto, String conteudoBloco, int offsetInicioBloco) {
        String conteudoLimpo = conteudoBloco;
        StringBuilder conteudoSemCursores = new StringBuilder(conteudoBloco);
        Matcher correspondenciaCursor = PADRAO_DECLARACAO_CURSOR.matcher(conteudoLimpo);

        while (correspondenciaCursor.find()) {
            String nomeCursor = correspondenciaCursor.group(1);
            int offsetInicioSelect = correspondenciaCursor.start(3);
            int offsetFimSelect = correspondenciaCursor.end(3);

            String sqlOriginal = conteudoBloco.substring(offsetInicioSelect, offsetFimSelect);
            if (sqlOriginal.endsWith(";")) sqlOriginal = sqlOriginal.substring(0, sqlOriginal.length() - 1);

            String sqlTrimado = sqlOriginal.trim();
            int espacosInicio = sqlOriginal.length() - sqlOriginal.stripLeading().length();
            int offsetBaseSelect = offsetInicioBloco + offsetInicioSelect + espacosInicio;

            if (!sqlTrimado.isEmpty()) {
                executarValidacaoAntlr(scriptCompleto, sqlTrimado, offsetBaseSelect, 0,
                        (linha, coluna, mensagem) -> MensagemSistema.ERRO_SINTAXE_CURSOR
                                .MensagemComParametro(linha, coluna, nomeCursor, mensagem));
            }

            int fimTrecho = correspondenciaCursor.end();
            if (conteudoSemCursores.charAt(fimTrecho - 1) == ';') {
                fimTrecho--;
            }

            apagarTrechoPreservandoQuebras(conteudoSemCursores, correspondenciaCursor.start(), correspondenciaCursor.end());
        }

        return conteudoSemCursores.toString();
    }

    /**
     * Valida os prefixos das variáveis especiais de trigger do Postgres.
     */
    private void validarVariaveisTrigger(String scriptCompleto, String conteudoBloco, int offsetInicioBloco) {
        Matcher correspondencia = PADRAO_VARIAVEIS_TG.matcher(conteudoBloco);
        while (correspondencia.find()) {
            String variavel = correspondencia.group(1).toUpperCase();
            if (!VARIAVEIS_TRIGGER_VALIDAS.contains(variavel)) {
                int[] posicao = posicaoAbsoluta(scriptCompleto, offsetInicioBloco + correspondencia.start(1));
                throw new SqlException(MensagemSistema.ERRO_VARIAVEL_TRIGGER_INVALIDA
                        .MensagemComParametro(posicao[0], posicao[1], correspondencia.group(1)));
            }
        }
    }

    /**
     * Protege contra erros ortográficos em rotinas internas do banco de dados.
     */
    private void validarErrosOrtograficosFuncoesSistema(String scriptCompleto, String conteudoBloco, int offsetInicioBloco) {
        Matcher correspondencia = PADRAO_CHAMADA_FUNCAO.matcher(conteudoBloco);
        while (correspondencia.find()) {
            String nomeFuncao = correspondencia.group(1).toUpperCase();
            if (ERROS_ORTOGRAFICOS_FUNCOES_SISTEMA.containsKey(nomeFuncao)) {
                int[] posicao = posicaoAbsoluta(scriptCompleto, offsetInicioBloco + correspondencia.start(1));
                throw new SqlException(MensagemSistema.ERRO_FUNCAO_SISTEMA_INCORRETA
                        .MensagemComParametro(posicao[0], posicao[1],
                                correspondencia.group(1),
                                ERROS_ORTOGRAFICOS_FUNCOES_SISTEMA.get(nomeFuncao)));
            }
        }
    }

    /**
     * Detecta erros ortográficos em modificadores isolados de comandos do corpo da função.
     */
    private void validarErrosOrtograficosPalavrasCorpo(String scriptCompleto, String instrucaoOriginal, int offsetInicioBloco, int offsetInicioInstrucao) {
        for (String palavra : instrucaoOriginal.split("\\s+")) {
            String palavraLimpa = palavra.replaceAll("[^a-zA-Z_]", "").toUpperCase();
            if (ERROS_ORTOGRAFICOS_PALAVRAS_CORPO.containsKey(palavraLimpa)) {
                int posNaPalavra = instrucaoOriginal.toUpperCase().indexOf(palavraLimpa);
                int offsetAbsoluto = offsetInicioBloco + offsetInicioInstrucao + posNaPalavra;
                int[] posicao = posicaoAbsoluta(scriptCompleto, offsetAbsoluto);
                throw new SqlException(MensagemSistema.ERRO_PALAVRA_CHAVE_INCORRETA
                        .MensagemComParametro(posicao[0], posicao[1],
                                palavra, ERROS_ORTOGRAFICOS_PALAVRAS_CORPO.get(palavraLimpa)));
            }
        }
    }

    /**
     * Gerencia a pilha de escopos e aninhamento lógico de estruturas condicionais e de repetição.
     */
    private void validarEstruturaControle(String scriptCompleto, String conteudoBloco, int offsetInicioBloco) {
        Set<String> rotulos = extrairRotulos(conteudoBloco);
        String conteudoLimpo = conteudoBloco;

        Matcher correspondencia = PADRAO_ESTRUTURAS_CONTROLE.matcher(conteudoLimpo);
        Stack<BlocoControle> pilha = new Stack<>();
        int ignorarAteOffset = -1;

        while (correspondencia.find()) {
            int posicaoNoBloco = correspondencia.start();
            if (posicaoNoBloco < ignorarAteOffset) continue;

            String palavraChave = correspondencia.group(1).toUpperCase().replaceAll("\\s+", " ");
            int offsetAbsoluto = offsetInicioBloco + posicaoNoBloco;
            int[] posicao = posicaoAbsoluta(scriptCompleto, offsetAbsoluto);

            if (!palavraChave.equals("THEN")) verificarThenPendente(pilha, posicao[0], posicao[1]);

            switch (palavraChave) {
                case "BEGIN", "LOOP", "CASE" ->
                        pilha.push(new BlocoControle(palavraChave, posicao[0], posicao[1], offsetAbsoluto));
                case "IF" -> pilha.push(new BlocoControle("IF_SEM_THEN", posicao[0], posicao[1], offsetAbsoluto));
                case "ELSIF" -> {
                    if (pilha.isEmpty() || !pilha.peek().tipo.equals("IF"))
                        throw new SqlException(MensagemSistema.ERRO_ELSIF_FORA_IF.MensagemComParametro(posicao[0], posicao[1]));
                    pilha.push(new BlocoControle("ELSIF_SEM_THEN", posicao[0], posicao[1], offsetAbsoluto));
                }
                case "THEN" -> processarThen(scriptCompleto, pilha, offsetAbsoluto, posicao);
                case "END IF" -> {
                    desempilharValidando(pilha, "IF", posicao[0], posicao[1]);
                    ignorarAteOffset = correspondencia.end();
                }
                case "END LOOP" -> {
                    desempilharValidando(pilha, "LOOP", posicao[0], posicao[1]);
                    ignorarAteOffset = correspondencia.end();
                }
                case "END CASE" -> {
                    desempilharValidando(pilha, "CASE", posicao[0], posicao[1]);
                    ignorarAteOffset = correspondencia.end();
                }
                case "END" -> processarEnd(scriptCompleto, pilha, rotulos, conteudoLimpo, correspondencia, posicao);
            }
        }

        if (!pilha.isEmpty()) {
            BlocoControle naoFechado = pilha.pop();
            throw new SqlException(MensagemSistema.ERRO_ESTRUTURA_NAO_FECHADA
                    .MensagemComParametro(naoFechado.linha, naoFechado.coluna, naoFechado.tipo.replace("_SEM_THEN", "")));
        }
    }

    /**
     * Valida a query condicional localizada entre blocos estruturais e sua respectiva palavra chave THEN.
     */
    private void processarThen(String scriptCompleto, Stack<BlocoControle> pilha, int offsetAbsolutoThen, int[] posicaoThen) {
        if (pilha.isEmpty())
            throw new SqlException(MensagemSistema.ERRO_THEN_SEM_IF.MensagemComParametro(posicaoThen[0], posicaoThen[1]));

        BlocoControle topo = pilha.peek();
        if (topo.tipo.equals("CASE")) return;

        if (!topo.tipo.equals("IF_SEM_THEN") && !topo.tipo.equals("ELSIF_SEM_THEN"))
            throw new SqlException(MensagemSistema.ERRO_THEN_INESPERADO.MensagemComParametro(posicaoThen[0], posicaoThen[1]));

        String tipoBase = topo.tipo.replace("_SEM_THEN", "");
        int offsetInicioCondicao = topo.offsetAbsoluto + tipoBase.length();
        String condicaoOriginal = scriptCompleto.substring(offsetInicioCondicao, offsetAbsolutoThen);
        String condicaoTrimada = condicaoOriginal.trim();
        int espacosIniciais = condicaoOriginal.length() - condicaoOriginal.stripLeading().length();

        if (!condicaoTrimada.isEmpty()) {
            executarValidacaoAntlr(scriptCompleto, "SELECT " + condicaoTrimada, offsetInicioCondicao + espacosIniciais, 7,
                    (linha, coluna, mensagem) -> MensagemSistema.ERRO_SINTAXE_CONDICAO_CONTROLE.MensagemComParametro(linha, coluna, tipoBase, mensagem));
        }

        if (topo.tipo.equals("IF_SEM_THEN")) {
            topo.tipo = "IF";
        } else {
            pilha.pop();
        }
    }

    /**
     * Resolve o fechamento de blocos baseando-se em analisadores de tokens de fechamento genérico (END).
     */
    private void processarEnd(String scriptCompleto, Stack<BlocoControle> pilha, Set<String> rotulos, String conteudoLimpo, Matcher correspondencia, int[] posicao) {
        if (!pilha.isEmpty() && pilha.peek().tipo.equals("CASE")) {
            pilha.pop();
            return;
        }

        int posicaoPesquisa = correspondencia.end();
        while (posicaoPesquisa < conteudoLimpo.length() && Character.isWhitespace(conteudoLimpo.charAt(posicaoPesquisa)))
            posicaoPesquisa++;

        String proximoToken = lerProximoToken(conteudoLimpo, posicaoPesquisa);

        if (proximoToken.equals(";") || proximoToken.isEmpty() || rotulos.contains(proximoToken)) {
            desempilharValidando(pilha, "BEGIN", posicao[0], posicao[1]);
        } else {
            throw new SqlException(MensagemSistema.ERRO_END_TOKEN_INESPERADO.MensagemComParametro(posicao[0], posicao[1], proximoToken));
        }
    }

    /**
     * Intercepta inconformidades quando comandos condicionais omitiram o token THEN obrigatório.
     */
    private void verificarThenPendente(Stack<BlocoControle> pilha, int linha, int coluna) {
        if (pilha.isEmpty()) return;
        BlocoControle topo = pilha.peek();
        if (topo.tipo.equals("IF_SEM_THEN"))
            throw new SqlException(MensagemSistema.ERRO_IF_SEM_THEN.MensagemComParametro(linha, coluna, topo.linha, topo.coluna));
        if (topo.tipo.equals("ELSIF_SEM_THEN"))
            throw new SqlException(MensagemSistema.ERRO_ELSIF_SEM_THEN.MensagemComParametro(linha, coluna, topo.linha, topo.coluna));
    }

    /**
     * Confirma a conformidade do bloco atual do topo da pilha em relação ao token de fechamento esperado.
     */
    private void desempilharValidando(Stack<BlocoControle> pilha, String tipoEsperado, int linha, int coluna) {
        if (pilha.isEmpty())
            throw new SqlException(MensagemSistema.ERRO_END_SEM_ABERTURA.MensagemComParametro(linha, coluna, tipoEsperado, tipoEsperado));
        BlocoControle topo = pilha.peek();
        if (!topo.tipo.equals(tipoEsperado))
            throw new SqlException(MensagemSistema.ERRO_END_FECHAMENTO_ERRADO.MensagemComParametro(linha, coluna, topo.tipo, topo.linha, topo.coluna, tipoEsperado));
        pilha.pop();
    }

    /**
     * Valida de forma encadeada as fatias de comandos e instruções SQL identificadas após a cláusula BEGIN.
     */
    private void validarComandosSqlNoCorpo(String scriptCompleto, String conteudoBloco, int offsetInicioBloco) {
        String conteudoLimpo = (conteudoBloco);
        Matcher correspondenciaBegin = Pattern.compile("\\bBEGIN\\b", Pattern.CASE_INSENSITIVE).matcher(conteudoLimpo);

        int posicaoAtual = correspondenciaBegin.find() ? correspondenciaBegin.end() : 0;
        int posicaoFim;

        while ((posicaoFim = conteudoLimpo.indexOf(';', posicaoAtual)) != -1) {
            String fragmentoLimpo = conteudoLimpo.substring(posicaoAtual, posicaoFim);
            String fragmentoOriginal = conteudoBloco.substring(posicaoAtual, posicaoFim);
            String instrucaoLimpa = fragmentoLimpo.trim();
            String instrucaoOriginal = fragmentoOriginal.trim();

            int offsetInicioInstrucao = posicaoAtual + (fragmentoLimpo.length() - fragmentoLimpo.stripLeading().length());
            posicaoAtual = posicaoFim + 1;

            if (instrucaoLimpa.isEmpty()) continue;

            String instrucaoSemCabecalho = removerCabecalhosPlpgsql(instrucaoOriginal);
            validarPontoVirgulaNosComandos(scriptCompleto, instrucaoSemCabecalho, offsetInicioBloco, offsetInicioInstrucao);

            if (instrucaoSemCabecalho.isBlank()) continue;

            validarErrosOrtograficosPalavrasCorpo(scriptCompleto, instrucaoOriginal, offsetInicioBloco, offsetInicioInstrucao);

            if (instrucaoLimpa.contains(":=")) {
                validarAtribuicaoNoCorpo(scriptCompleto, instrucaoOriginal, offsetInicioBloco, offsetInicioInstrucao);
                continue;
            }

            String primeiraPalavra = instrucaoSemCabecalho.trim().split("\\s+")[0].toUpperCase();
            if (ERROS_ORTOGRAFICOS_PALAVRAS_CHAVE_PLPGSQL.containsKey(primeiraPalavra)) {
                int[] posicao = posicaoAbsoluta(scriptCompleto, offsetInicioBloco + offsetInicioInstrucao + instrucaoOriginal.toUpperCase().indexOf(primeiraPalavra));
                throw new SqlException(MensagemSistema.ERRO_PALAVRA_CHAVE_INCORRETA.MensagemComParametro(posicao[0], posicao[1], primeiraPalavra, ERROS_ORTOGRAFICOS_PALAVRAS_CHAVE_PLPGSQL.get(primeiraPalavra)));
            }

            if (INICIADORES_VALIDOS_PLPGSQL.contains(primeiraPalavra)) {
                if (Set.of("SELECT", "INSERT", "UPDATE", "DELETE").contains(primeiraPalavra)) {
                    executarValidacaoAntlr(scriptCompleto, instrucaoSemCabecalho, offsetInicioBloco + offsetInicioInstrucao, 0,
                            (linha, coluna, mensagem) -> MensagemSistema.ERRO_SQL_LINHA_COLUNA.MensagemComParametro(linha, coluna, mensagem));
                }
            }
        }
    }

    /**
     * Isola e valida gramaticalmente expressões de atribuição de variáveis no corpo das instruções.
     */
    private void validarAtribuicaoNoCorpo(String scriptCompleto, String instrucaoOriginal, int offsetInicioBloco, int offsetInicioInstrucao) {
        int idx = instrucaoOriginal.indexOf(":=");
        String ladoDireito = instrucaoOriginal.substring(idx + 2).trim();
        if (!ladoDireito.isEmpty()) {
            int offsetLadoDireito = offsetInicioBloco + offsetInicioInstrucao + idx + 2 + (instrucaoOriginal.substring(idx + 2).length() - ladoDireito.length());
            executarValidacaoAntlr(scriptCompleto, "SELECT " + ladoDireito, offsetLadoDireito, 7,
                    MensagemSistema.ERRO_SINTAXE_ATRIBUICAO::MensagemComParametro);
        }
    }

    /**
     * Garante que múltiplos comandos que exijam terminação por ponto e vírgula não estejam agrupados irregularmente.
     */
    private void validarPontoVirgulaNosComandos(String scriptCompleto, String instrucao, int offsetInicioBloco, int offsetInicioInstrucao) {
        String[] tokens = instrucao.trim().split("\\s+");
        for (int i = 0; i < tokens.length - 1; i++) {
            String tokenLimpo = tokens[i].replaceAll("[^a-zA-Z_]", "").toUpperCase();
            if (i == 0 && COMANDOS_QUE_EXIGEM_PONTO_VIRGULA.contains(tokenLimpo)) {
                continue;
            }
            if (COMANDOS_QUE_EXIGEM_PONTO_VIRGULA.contains(tokenLimpo)) {
                int[] pos = posicaoAbsoluta(scriptCompleto, offsetInicioBloco + offsetInicioInstrucao + instrucao.indexOf(tokens[i]));
                throw new SqlException(MensagemSistema.ERRO_DECLARACAO_SEM_PONTO_VIRGULA.MensagemComParametro(pos[0], pos[1]));
            }
        }
    }

    /**
     * Remove metadados de cabeçalhos internos do bloco PL/pgSQL para isolar puramente as consultas SQL.
     */
    private String removerCabecalhosPlpgsql(String instrucao) {
        String resultado = instrucao;
        Matcher m = PADRAO_CABECALHO_PLPGSQL.matcher(resultado);
        while (m.find()) {
            resultado = resultado.substring(0, m.start()) + " ".repeat(m.end() - m.start()) + resultado.substring(m.end());
            m = PADRAO_CABECALHO_PLPGSQL.matcher(resultado);
        }
        return resultado;
    }

    /**
     * Executa a análise léxica e sintática estrutural por meio da infraestrutura de Listeners do ANTLR4.
     */
    private void executarValidacaoAntlr(String scriptCompleto, String sqlParaValidar, int offsetBaseAbsoluto, int offsetTruncamentoAntlr, FabricaMensagemErro fabricaErro) {
        try {
            CharStream input = CharStreams.fromString(sqlParaValidar);
            PostgreSQLLexer lexer = new PostgreSQLLexer(input);
            lexer.removeErrorListeners();

            CommonTokenStream tokens = new CommonTokenStream(lexer);
            PostgreSQLParser parser = new PostgreSQLParser(tokens);
            parser.removeErrorListeners();

            parser.addErrorListener(new BaseErrorListener() {
                @Override
                public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                    throw new ErroSintaxeInterno(line, charPositionInLine, msg);
                }
            });

            ParseTreeWalker.DEFAULT.walk(new PostgreSQLParserBaseListener(), parser.root());

        } catch (ErroSintaxeInterno erro) {
            int offsetErro = calcularOffsetAbsoluto(sqlParaValidar, erro.linha, erro.coluna, offsetBaseAbsoluto, offsetTruncamentoAntlr);
            int[] pos = posicaoAbsoluta(scriptCompleto, offsetErro);
            throw new SqlException(fabricaErro.criar(pos[0], pos[1], erro.getMessage()));
        }
    }

    /**
     * Calcula o deslocamento absoluto do erro sintático remapeando de volta para a string original.
     */
    private int calcularOffsetAbsoluto(String fragmentoSql, int linhaErro, int colunaErro, int offsetBaseAbsoluto, int offsetTruncamento) {
        String[] linhas = fragmentoSql.split("\\r?\\n");
        int offsetAcumulado = 0;
        for (int i = 0; i < linhaErro - 1; i++) {
            offsetAcumulado += linhas[i].length() + 1;
        }
        return offsetBaseAbsoluto + offsetAcumulado + colunaErro - offsetTruncamento;
    }

    /**
     * Mapeia um offset absoluto de strings unidimensionais para sua respectiva coordenada bidimensional (Linha e Coluna).
     */
    private int[] posicaoAbsoluta(String texto, int offsetAbsoluto) {
        int linha = 1;
        int coluna = 1;
        int limite = Math.min(offsetAbsoluto, texto.length());
        for (int i = 0; i < limite; i++) {
            if (texto.charAt(i) == '\n') {
                linha++;
                coluna = 1;
            } else {
                coluna++;
            }
        }
        return new int[]{linha, coluna};
    }

    /**
     * Extrai e mapeia rótulos nomeados (Labels) definidos dentro da rotina PL/pgSQL.
     */
    private Set<String> extrairRotulos(String conteudoBloco) {
        Set<String> rotulos = new HashSet<>();
        Matcher m = Pattern.compile("<<(\\w+)>>").matcher(conteudoBloco);
        while (m.find()) rotulos.add(m.group(1).toUpperCase());
        return rotulos;
    }

    /**
     * Lê e consome o token alfanumérico imediatamente subsequente a um determinado índice.
     */
    private String lerProximoToken(String texto, int posicao) {
        if (posicao >= texto.length()) return "";
        char c = texto.charAt(posicao);
        if (Character.isLetterOrDigit(c) || c == '_') {
            int fim = posicao;
            while (fim < texto.length() && (Character.isLetterOrDigit(texto.charAt(fim)) || texto.charAt(fim) == '_'))
                fim++;
            return texto.substring(posicao, fim).toUpperCase();
        }
        return String.valueOf(c);
    }

    /**
     * Substitui o conteúdo de um trecho de texto por espaços vazios preservando quebras de linha.
     */
    private void apagarTrechoPreservandoQuebras(StringBuilder sb, int inicio, int fim) {
        for (int i = inicio; i < fim && i < sb.length(); i++) {
            if (sb.charAt(i) != '\n' && sb.charAt(i) != '\r') {
                sb.setCharAt(i, ' ');
            }
        }
    }
}