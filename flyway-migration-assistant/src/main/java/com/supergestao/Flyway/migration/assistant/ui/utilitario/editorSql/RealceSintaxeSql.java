package com.supergestao.Flyway.migration.assistant.ui.utilitario.editorSql;

import com.supergestao.Flyway.migration.assistant.dominio.tipo.PalavraChaveSql;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RealceSintaxeSql {

    private static final String PALAVRAS_CHAVE_PATTERN = "\\b(" + String.join("|", PalavraChaveSql.getListaPalavras()) + ")\\b";
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

    public static StyleSpans<Collection<String>> atualizarEstilos(String texto) {
        if (texto == null || texto.isEmpty()) {
            return new StyleSpansBuilder<Collection<String>>().add(Collections.emptyList(), 0).create();
        }

        Matcher matcher = PATTERN.matcher(texto);
        int ultimo = 0;
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
                int inicio = matcher.start();
                int fim = matcher.end();

                if (inicio - ultimo > 0) {
                    spansBuilder.add(Collections.emptyList(), inicio - ultimo);
                }

                spansBuilder.add(Collections.singleton(classeEstilo), fim - inicio);

                ultimo = fim;
            }
        }

        if (texto.length() - ultimo > 0) {
            spansBuilder.add(Collections.emptyList(), texto.length() - ultimo);
        }

        return spansBuilder.create();
    }
}
