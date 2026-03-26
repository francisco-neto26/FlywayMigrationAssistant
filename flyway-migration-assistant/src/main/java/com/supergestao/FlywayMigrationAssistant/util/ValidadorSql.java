package com.supergestao.FlywayMigrationAssistant.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class ValidadorSql {

    private static final Pattern PATTERN_COMENTARIO_LINHA = Pattern.compile("--.*$", Pattern.MULTILINE);
    private static final Pattern PATTERN_COMENTARIO_BLOCO = Pattern.compile("/\\*.*?\\*/", Pattern.DOTALL);
    private static final Pattern PATTERN_STRING = Pattern.compile("'[^']*'");

    public static ResultadoValidacao validar(String sql) {
        ResultadoValidacao resultado = new ResultadoValidacao();

        if (sql == null || sql.isBlank()) {
            resultado.adicionarErro("SQL vazio ou nulo");
            return resultado;
        }

        String sqlLimpo = removerComentariosEStrings(sql);

        validarSintaxeBasica(sql, sqlLimpo, resultado);
        validarPalavrasChave(sqlLimpo, resultado);
        validarParenteses(sqlLimpo, resultado);
        validarPontoVirgula(sql, resultado);
        validarNomesObjetos(sqlLimpo, resultado);
        validarBestPractices(sql, sqlLimpo, resultado);

        return resultado;
    }

    private static String removerComentariosEStrings(String sql) {
        String temp = PATTERN_COMENTARIO_BLOCO.matcher(sql).replaceAll(" ");
        temp = PATTERN_COMENTARIO_LINHA.matcher(temp).replaceAll(" ");
        temp = PATTERN_STRING.matcher(temp).replaceAll("''");
        return temp;
    }

    private static void validarSintaxeBasica(String sqlOriginal, String sqlLimpo, ResultadoValidacao resultado) {
        String sql = sqlLimpo.toUpperCase().trim();

        if (!sql.matches(".*\\b(SELECT|INSERT|UPDATE|DELETE|CREATE|ALTER|DROP|GRANT|REVOKE|COMMENT|TRUNCATE|REFRESH)\\b.*")) {
            resultado.adicionarAviso("SQL não contém nenhum comando reconhecido");
        }

        if (sql.contains("CREATE TABLE") && !sql.contains("(")) {
            resultado.adicionarErro("CREATE TABLE sem definição de colunas");
        }

        if (sql.contains("INSERT INTO") && !sql.contains("VALUES") && !sql.contains("SELECT")) {
            resultado.adicionarErro("INSERT INTO sem VALUES ou SELECT");
        }

        if (sql.contains("UPDATE") && !sql.contains("SET")) {
            resultado.adicionarErro("UPDATE sem cláusula SET");
        }

        if (sql.contains("SELECT") && sql.contains("FROM")) {
            int selectPos = sql.indexOf("SELECT");
            int fromPos = sql.indexOf("FROM");
            if (selectPos > fromPos) {
                resultado.adicionarErro("SELECT deve vir antes de FROM");
            }
        }
    }

    private static void validarPalavrasChave(String sqlLimpo, ResultadoValidacao resultado) {
        String sql = sqlLimpo.toUpperCase();

        String[] comandosDDL = {"CREATE", "ALTER", "DROP", "TRUNCATE"};
        String[] comandosDML = {"SELECT", "INSERT", "UPDATE", "DELETE"};

        int countDDL = 0;
        int countDML = 0;

        for (String cmd : comandosDDL) {
            if (sql.contains(cmd)) countDDL++;
        }

        for (String cmd : comandosDML) {
            if (sql.contains(cmd)) countDML++;
        }

        if (countDDL > 0 && countDML > 0) {
            resultado.adicionarAviso("Mistura de comandos DDL e DML no mesmo arquivo (não recomendado)");
        }

        if (sql.contains("DROP") && !sql.contains("IF EXISTS")) {
            resultado.adicionarAviso("DROP sem IF EXISTS pode causar erro se objeto não existir");
        }

        if (sql.contains("CREATE") && !sql.contains("IF NOT EXISTS") &&
                !sql.contains("OR REPLACE") && !sql.contains("CREATE UNIQUE")) {
            resultado.adicionarAviso("CREATE sem IF NOT EXISTS ou OR REPLACE pode causar erro se objeto já existir");
        }
    }

    private static void validarParenteses(String sqlLimpo, ResultadoValidacao resultado) {
        int contador = 0;
        int linha = 1;

        for (int i = 0; i < sqlLimpo.length(); i++) {
            char c = sqlLimpo.charAt(i);

            if (c == '\n') linha++;

            if (c == '(') {
                contador++;
            } else if (c == ')') {
                contador--;
                if (contador < 0) {
                    resultado.adicionarErro("Parêntese de fechamento sem abertura correspondente (próximo à linha " + linha + ")");
                    return;
                }
            }
        }

        if (contador > 0) {
            resultado.adicionarErro("Parênteses não balanceados: " + contador + " parêntese(s) não fechado(s)");
        }
    }

    private static void validarPontoVirgula(String sql, ResultadoValidacao resultado) {
        String sqlSemComentarios = PATTERN_COMENTARIO_LINHA.matcher(sql).replaceAll("");
        sqlSemComentarios = PATTERN_COMENTARIO_BLOCO.matcher(sqlSemComentarios).replaceAll("");

        String sqlTrimmed = sqlSemComentarios.trim();

        if (!sqlTrimmed.isEmpty() && !sqlTrimmed.endsWith(";")) {
            resultado.adicionarAviso("SQL não termina com ponto-e-vírgula (;)");
        }

        long count = sqlTrimmed.chars().filter(ch -> ch == ';').count();
        if (count > 1) {
            resultado.adicionarInfo("Múltiplos comandos SQL detectados (" + count + " comandos)");
        }
    }

    private static void validarNomesObjetos(String sqlLimpo, ResultadoValidacao resultado) {
        Pattern patternNomeInvalido = Pattern.compile("\\b(CREATE|ALTER|DROP)\\s+(TABLE|INDEX|VIEW|FUNCTION|PROCEDURE)\\s+([A-Z][A-Z0-9_]*)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = patternNomeInvalido.matcher(sqlLimpo);

        while (matcher.find()) {
            String nomeObjeto = matcher.group(3);
            if (nomeObjeto.matches("^[A-Z].*")) {
                resultado.adicionarAviso("Nome de objeto com letra maiúscula detectado: '" + nomeObjeto + "' (recomenda-se lowercase)");
            }
        }

        Pattern patternEspacos = Pattern.compile("\\b(CREATE|ALTER|DROP)\\s+(TABLE|INDEX|VIEW|FUNCTION|PROCEDURE)\\s+['\"]([^'\"]+)['\"]", Pattern.CASE_INSENSITIVE);
        Matcher matcherEspacos = patternEspacos.matcher(sqlLimpo);

        while (matcherEspacos.find()) {
            String nomeObjeto = matcherEspacos.group(3);
            if (nomeObjeto.contains(" ")) {
                resultado.adicionarErro("Nome de objeto com espaços: '" + nomeObjeto + "' (não recomendado)");
            }
        }
    }

    private static void validarBestPractices(String sqlOriginal, String sqlLimpo, ResultadoValidacao resultado) {
        String sql = sqlLimpo.toUpperCase();

        if (sql.contains("SELECT *") && !sql.contains("COUNT(*)")) {
            resultado.adicionarAviso("SELECT * detectado (recomenda-se especificar colunas)");
        }

        if (sql.contains("DELETE") && !sql.contains("WHERE")) {
            resultado.adicionarErro("DELETE sem WHERE - Isso apagará TODOS os registros!");
        }

        if (sql.contains("UPDATE") && !sql.contains("WHERE")) {
            resultado.adicionarErro("UPDATE sem WHERE - Isso atualizará TODOS os registros!");
        }

        if (sql.contains("TRUNCATE")) {
            resultado.adicionarAviso("TRUNCATE detectado - Operação irreversível");
        }

        if (sql.contains("DROP") && sql.contains("CASCADE")) {
            resultado.adicionarAviso("DROP CASCADE detectado - Removerá objetos dependentes");
        }

        if (!sqlOriginal.contains("COMMENT ON")) {
            resultado.adicionarInfo("Nenhum COMMENT ON detectado (recomenda-se documentar objetos)");
        }

        if (sql.contains("CREATE TABLE") && !sql.contains("PRIMARY KEY")) {
            resultado.adicionarAviso("CREATE TABLE sem PRIMARY KEY (recomenda-se adicionar)");
        }

        if (sql.contains("CREATE INDEX") && !sql.contains("CONCURRENTLY")) {
            resultado.adicionarAviso("CREATE INDEX sem CONCURRENTLY (pode travar tabela em produção)");
        }

        if (sql.contains("ALTER TABLE") && sql.contains("ADD COLUMN") && !sql.contains("IF NOT EXISTS")) {
            resultado.adicionarAviso("ALTER TABLE ADD COLUMN sem IF NOT EXISTS");
        }

        if (sql.matches(".*\\b(PASSWORD|SENHA)\\b.*") && !sqlOriginal.contains("--")) {
            resultado.adicionarErro("Possível senha em texto plano detectada no SQL");
        }
    }

    public static class ResultadoValidacao {
        private final List<String> erros = new ArrayList<>();
        private final List<String> avisos = new ArrayList<>();
        private final List<String> informacoes = new ArrayList<>();

        public void adicionarErro(String erro) {
            erros.add(erro);
        }

        public void adicionarAviso(String aviso) {
            avisos.add(aviso);
        }

        public void adicionarInfo(String info) {
            informacoes.add(info);
        }

        public boolean isValido() {
            return erros.isEmpty();
        }

        public boolean temAvisos() {
            return !avisos.isEmpty();
        }

        public List<String> getErros() {
            return new ArrayList<>(erros);
        }

        public List<String> getAvisos() {
            return new ArrayList<>(avisos);
        }

        public List<String> getInformacoes() {
            return new ArrayList<>(informacoes);
        }

        public String getMensagemCompleta() {
            StringBuilder sb = new StringBuilder();

            if (!erros.isEmpty()) {
                sb.append("❌ ERROS:\n");
                for (String erro : erros) {
                    sb.append("  • ").append(erro).append("\n");
                }
                sb.append("\n");
            }

            if (!avisos.isEmpty()) {
                sb.append("⚠️ AVISOS:\n");
                for (String aviso : avisos) {
                    sb.append("  • ").append(aviso).append("\n");
                }
                sb.append("\n");
            }

            if (!informacoes.isEmpty()) {
                sb.append("ℹ️ INFORMAÇÕES:\n");
                for (String info : informacoes) {
                    sb.append("  • ").append(info).append("\n");
                }
            }

            if (erros.isEmpty() && avisos.isEmpty() && informacoes.isEmpty()) {
                sb.append("✅ SQL validado com sucesso!");
            }

            return sb.toString();
        }

        public int getTotalProblemas() {
            return erros.size() + avisos.size();
        }
    }
}
