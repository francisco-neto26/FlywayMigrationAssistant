package com.supergestao.FlywayMigrationAssistant.util;

import com.supergestao.FlywayMigrationAssistant.config.ModuloConfig;
import com.supergestao.FlywayMigrationAssistant.model.Tipo;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GeradorNomeArquivo {
    private static final DateTimeFormatter TIMESTAMP_FORMATO =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public static String geradorNomeArquivo(String descricao, String modulo,
                                            Tipo tipo) {
        String prefixo = ModuloConfig.getPrefix(modulo);
        String descricaoFormatada = DescricaoFormatada(descricao);

        if (tipo.getrequerTimestamp()) {
            String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATO);
            return String.format("V%s__%s_Tabelas__%s.sql",
                    timestamp, prefixo, descricaoFormatada);
        } else {
            return String.format("R__%s_%s.sql", prefixo, descricaoFormatada);
        }
    }

    public static String DescricaoFormatada(String descricao) {
        if (descricao == null || descricao.isEmpty()) {
            return "";
        }

        // Remove acentos
        String descricaoFormatada = Normalizer.normalize(descricao, Normalizer.Form.NFD);
        descricaoFormatada = descricaoFormatada.replaceAll("\\p{M}", "");

        // Substituir espaços por underscores e remover caracteres especiais
        descricaoFormatada = descricaoFormatada.replaceAll("[^a-zA-Z0-9\\s]", "");
        descricaoFormatada = descricaoFormatada.trim().replaceAll("\\s+", "_");

        return descricaoFormatada;
    }
}