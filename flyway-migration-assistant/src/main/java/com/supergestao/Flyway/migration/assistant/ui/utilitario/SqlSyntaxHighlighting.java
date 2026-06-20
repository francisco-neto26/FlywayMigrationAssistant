package com.supergestao.Flyway.migration.assistant.ui.utilitario;

import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlSyntaxHighlighting {

    private static final String[] PALAVRAS_CHAVE = new String[] {
            "SELECT", "FROM", "WHERE", "INSERT", "UPDATE", "DELETE", "CREATE", "DROP", "ALTER",
            "TABLE", "VIEW", "FUNCTION", "TRIGGER", "INDEX", "RETURNS", "LANGUAGE", "COST",
            "VOLATILE", "LEAKPROOF", "DECLARE", "BEGIN", "END", "LOOP", "IF", "THEN", "ELSE",
            "ELSIF", "CASE", "WHEN", "OR", "AND", "NOT", "IN", "INTO", "USING", "EXECUTE",
            "PERFORM", "RETURN", "COALESCE", "SUBSTRING", "UPPER", "LOWER", "REPLACE", "JOIN",
            "LEFT", "RIGHT", "INNER", "OUTER", "ON", "AS", "UNION", "ALL", "EXISTS", "LIKE",
            "ILIKE", "IS", "NULL", "TRUE", "FALSE", "TG_OP", "TG_TABLE_NAME", "TG_NAME",
            "OLD", "NEW", "FOUND", "EXIT", "CLOSE", "OPEN", "FETCH", "EXCLUDING", "INCLUDING",
            "ROW", "EACH", "EXECUTE", "PROCEDURE"
    };

    private static final String PALAVRAS_CHAVE_PATTERN = "\\b(" + String.join("|", PALAVRAS_CHAVE) + ")\\b";
    private static final String COMENTARIO_LINHA_PATTERN = "--[^\n]*";
    private static final String COMENTARIO_BLOCO_PATTERN = "/\\*(.*?)\\*/";
    private static final String STRING_PATTERN = "'([^'\\\\]|\\\\.)*'";
    private static final String NUMERO_PATTERN = "\\b\\d+\\b";

    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + PALAVRAS_CHAVE_PATTERN + ")"
                    + "|(?<COMENTARIOLINHA>" + COMENTARIO_LINHA_PATTERN + ")"
                    + "|(?<COMENTARIOBLOCO>" + COMENTARIO_BLOCO_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<NUMERO>" + NUMERO_PATTERN + ")"
                    + "|(?<OPERADOR>::|\\|\\||[\\+\\-\\*/=<>!]+)",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    /**
     * Calcula as regiões de estilo (spans) a partir do texto SQL atual,
     * incluindo a marcação de erro se as posições forem válidas.
     */
    public static StyleSpans<Collection<String>> calcularStyles(String texto, int erroInicio, int erroFim) {
        if (texto == null) {
            return new StyleSpansBuilder<Collection<String>>().add(Collections.emptyList(), 0).create();
        }

        Matcher matcher = PATTERN.matcher(texto);
        int ultimoFimMatch = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

        while (matcher.find()) {
            String classeEstilo =
                    matcher.group("KEYWORD") != null ? "keyword" :
                            matcher.group("COMENTARIOLINHA") != null ? "comment" :
                            matcher.group("COMENTARIOBLOCO") != null ? "comment" :
                            matcher.group("STRING") != null ? "string" :
                            matcher.group("NUMERO") != null ? "number" :
                            matcher.group("OPERADOR") != null ? "operator" :
                            null;

            if (classeEstilo != null) {
                int start = matcher.start();
                int end = matcher.end();

                // Trata o texto normal antes do match
                adicionarSpansComErro(spansBuilder, start - ultimoFimMatch, ultimoFimMatch, "normal", erroInicio, erroFim);

                // Trata o match atual
                adicionarSpansComErro(spansBuilder, end - start, start, classeEstilo, erroInicio, erroFim);

                ultimoFimMatch = end;
            }
        }

        // Trata o restante do texto final
        adicionarSpansComErro(spansBuilder, texto.length() - ultimoFimMatch, ultimoFimMatch, "normal", erroInicio, erroFim);

        return spansBuilder.create();
    }

    private static void adicionarSpansComErro(StyleSpansBuilder<Collection<String>> spansBuilder,
                                              int comprimento, int offsetBase, String classeEstiloBase,
                                              int erroInicio, int erroFim) {
        if (comprimento <= 0) return;

        int matchStart = offsetBase;
        int matchEnd = offsetBase + comprimento;

        // Verifica se o trecho atual intercepta a área com erro
        if (erroInicio >= 0 && matchStart < erroFim && matchEnd > erroInicio) {
            int antesErro = Math.max(0, erroInicio - matchStart);
            int noErro = Math.min(matchEnd, erroFim) - Math.max(matchStart, erroInicio);
            int depoisErro = Math.max(0, matchEnd - erroFim);

            if (antesErro > 0) {
                spansBuilder.add(criarEstilos(classeEstiloBase, false), antesErro);
            }
            if (noErro > 0) {
                spansBuilder.add(criarEstilos(classeEstiloBase, true), noErro);
            }
            if (depoisErro > 0) {
                spansBuilder.add(criarEstilos(classeEstiloBase, false), depoisErro);
            }
        } else {
            spansBuilder.add(criarEstilos(classeEstiloBase, false), comprimento);
        }
    }

    private static Collection<String> criarEstilos(String classeEstiloBase, boolean comErro) {
        if ("normal".equals(classeEstiloBase)) {
            return comErro ? Collections.singleton("error-underline") : Collections.emptyList();
        }
        return comErro ? Arrays.asList(classeEstiloBase, "error-underline") : Collections.singleton(classeEstiloBase);
    }
}
