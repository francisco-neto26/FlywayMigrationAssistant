package com.supergestao.Flyway.migration.assistant.ui.utilitario;

import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlSyntaxHighlighting {

    // Lista das palavras-chave mais comuns do SQL e PL/pgSQL
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

    // Compila todas as regras em um único padrão Regex agrupado
    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + PALAVRAS_CHAVE_PATTERN + ")"
                    + "|(?<COMENTARIOLINHA>" + COMENTARIO_LINHA_PATTERN + ")"
                    + "|(?<COMENTARIOBLOCO>" + COMENTARIO_BLOCO_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<NUMERO>" + NUMERO_PATTERN + ")",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    /**
     * Calcula as regiões de estilo (spans) a partir do texto SQL atual.
     */
    public static StyleSpans<Collection<String>> calcularStyles(String texto) {
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
                            null;

            if (classeEstilo != null) {
                // Adiciona espaço de texto sem formatação antes do match atual
                spansBuilder.add(Collections.emptyList(), matcher.start() - ultimoFimMatch);
                // Adiciona o estilo correspondente para o trecho do match
                spansBuilder.add(Collections.singleton(classeEstilo), matcher.end() - matcher.start());
                ultimoFimMatch = matcher.end();
            }
        }
        // Adiciona o restante do texto final sem formatação
        spansBuilder.add(Collections.emptyList(), texto.length() - ultimoFimMatch);
        return spansBuilder.create();
    }
}
