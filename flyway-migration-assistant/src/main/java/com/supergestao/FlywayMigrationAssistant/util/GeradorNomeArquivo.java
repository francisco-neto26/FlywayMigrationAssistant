package com.supergestao.FlywayMigrationAssistant.util;

import com.supergestao.FlywayMigrationAssistant.model.*;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GeradorNomeArquivo {
    private static final DateTimeFormatter TIMESTAMP_FORMATO = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public static String gerarNomeCompleto(String descRaw, Acao acao, ObjetoBanco objeto, String modulo, Tipo tipo, Arquivo origem) {
        if (tipo == Tipo.UNDO && origem != null) {
            return "U" + origem.getnome().substring(1).toLowerCase();
        }

        if (tipo == Tipo.REPEATABLE && origem != null) {
            return origem.getnome().toLowerCase();
        }

        if (modulo == null || modulo.isEmpty()) return "";

        String prefixoModulo = extrairPrefixo(modulo);
        StringBuilder sb = new StringBuilder();

        if (tipo == Tipo.VERSIONED) {
            if (acao != null) sb.append(acao.getValorArquivo()).append("_");
            if (objeto != null) sb.append(objeto.getValorArquivo()).append("_");
        } else if (tipo == Tipo.REPEATABLE && objeto != null) {
            sb.append(objeto.getValorArquivo()).append("_");
        }

        sb.append(descRaw);
        String descLimpa = formatarTexto(sb.toString());

        if (tipo.getrequerTimestamp()) {
            String ts = LocalDateTime.now().format(TIMESTAMP_FORMATO);
            return String.format("V%s__%s_%s.sql", ts, prefixoModulo, descLimpa);
        } else {
            return String.format("R__%s_%s.sql", prefixoModulo, descLimpa);
        }
    }

    private static String extrairPrefixo(String nome) {
        String limpo = formatarTexto(nome);
        return limpo.length() <= 3 ? limpo : limpo.substring(0, 3);
    }

    private static String formatarTexto(String t) {
        if (t == null) return "";
        String r = Normalizer.normalize(t.toLowerCase(), Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        r = r.replace(" ", "_").replaceAll("[^a-z0-9_]", "");
        return r.replaceAll("_+", "_").replaceAll("^_+|_+$", "");
    }
}