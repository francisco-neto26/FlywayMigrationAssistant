package com.supergestao.FlywayMigrationAssistant.util;

import com.supergestao.FlywayMigrationAssistant.model.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GeradorTemplateSql {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static String gerar(String descricao, Tipo tipo, Acao acao, ObjetoBanco objeto, String modulo) {
        String nomeSugerido = formatarNomeObjeto(descricao);
        return gerarCabecalho(descricao, tipo, acao, objeto, modulo) + gerarCorpo(acao, objeto, nomeSugerido);
    }

    private static String formatarNomeObjeto(String descricao) {
        if (descricao == null || descricao.isBlank()) return "nome_objeto";
        String s = descricao.trim().toLowerCase();
        if (s.contains("__")) s = s.substring(s.indexOf("__") + 2);
        return s.replaceAll("\\s+", "_").replaceAll("[^a-z0-9_]", "");
    }

    private static String gerarCabecalho(String descricao, Tipo tipo, Acao acao, ObjetoBanco objeto, String modulo) {
        return "-- ================================================================\n" +
                "-- MÓDULO:    " + modulo.toUpperCase() + "\n" +
                "-- MIGRATION: " + descricao + "\n" +
                "-- AÇÃO:      " + acao + "\n" +
                "-- OBJETO: " + objeto + "\n" +
                "-- TIPO:      " + tipo.name() + "\n" +
                "-- DATA: " + LocalDateTime.now().format(DATE_FORMATTER) + "\n" +
                "-- ================================================================\n\n";
    }

    private static String gerarCorpo(Acao acao, ObjetoBanco objeto, String nome) {
        if (acao == null || objeto == null) return "-- Selecione Ação e Objeto para gerar o template.\n";
        return switch (acao) {
            case CRIAR -> templateCriar(objeto, nome);
            case ALTERAR -> templateAlterar(objeto, nome);
            case REMOVER -> templateRemover(objeto, nome);
            case RENOMEAR -> "ALTER " + traduzirObjeto(objeto) + " " + nome + " RENAME TO " + nome + "_novo;\n";
            default -> "-- Comando não implementado para esta combinação.\n";
        };
    }

    private static String templateCriar(ObjetoBanco objeto, String nome) {
        return switch (objeto) {
            case TABELA -> "CREATE TABLE IF NOT EXISTS " + nome + " (\n    id BIGSERIAL PRIMARY KEY,\n    uuid UUID DEFAULT gen_random_uuid(),\n    criado_em TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP\n);\n";
            case INDICE -> "CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_" + nome + " ON nome_tabela (coluna);\n";
            case VIEW -> "CREATE OR REPLACE VIEW vw_" + nome + " AS \nSELECT * FROM nome_tabela;\n";
            case FUNCAO -> "CREATE OR REPLACE FUNCTION fn_" + nome + "()\nRETURNS void AS $$\nBEGIN\nEND;\n$$ LANGUAGE plpgsql;\n";
            default -> "CREATE " + traduzirObjeto(objeto) + " " + nome + ";\n";
        };
    }

    private static String templateAlterar(ObjetoBanco objeto, String nome) {
        return switch (objeto) {
            case TABELA -> "ALTER TABLE " + nome + " ADD COLUMN IF NOT EXISTS nova_coluna TIPO;\n";
            case COLUNA -> "ALTER TABLE nome_tabela ALTER COLUMN " + nome + " SET NOT NULL;\n";
            default -> "ALTER " + traduzirObjeto(objeto) + " " + nome + " ...;\n";
        };
    }

    private static String templateRemover(ObjetoBanco objeto, String nome) {
        return "DROP " + traduzirObjeto(objeto) + " IF EXISTS " + nome + " CASCADE;\n";
    }

    private static String traduzirObjeto(ObjetoBanco objeto) {
        return switch (objeto) {
            case VIEW_MATERIALIZADA -> "MATERIALIZED VIEW";
            case TABELA -> "TABLE";
            case FUNCAO -> "FUNCTION";
            case PROCEDIMENTO -> "PROCEDURE";
            default -> objeto.name();
        };
    }
}