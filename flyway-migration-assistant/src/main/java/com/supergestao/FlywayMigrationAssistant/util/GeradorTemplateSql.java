package com.supergestao.FlywayMigrationAssistant.util;

import com.supergestao.FlywayMigrationAssistant.model.Tipo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GeradorTemplateSql {
    private static final DateTimeFormatter DATE_FORMATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static String geradorTemplateSql(String descricao, Tipo tipo) {
        String cabecalho = gerarCabecalho(descricao, tipo);
        String corpo = tipo == Tipo.VERSIONED ?
                gerarTemplateVersioned() : gerarTemplateRepeatable();

        return cabecalho + corpo;
    }

    private static String gerarCabecalho(String descricao, Tipo tipo) {
        return "-- ================================================================\n" +
                "-- Migration: " + descricao + "\n" +
                "-- Tipo: " + tipo.getdescricao() + "\n" +
                "-- Data: " + LocalDateTime.now().format(DATE_FORMATA) + "\n" +
                "-- ================================================================\n\n";
    }

    private static String gerarTemplateVersioned() {
        return "-- Criar tabela\n" +
                "CREATE TABLE IF NOT EXISTS schema.tabela_exemplo (\n" +
                "    id BIGSERIAL PRIMARY KEY,\n" +
                "    descricao VARCHAR(255) NOT NULL,\n" +
                "    ativo BOOLEAN DEFAULT TRUE,\n" +
                "    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n" +
                "    data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP\n" +
                ");\n\n" +
                "-- Criar índices\n" +
                "CREATE INDEX idx_tabela_exemplo_ativo ON schema.tabela_exemplo(ativo);\n\n" +
                "-- Dicionário de Dados Vivo\n" +
                "COMMENT ON TABLE schema.tabela_exemplo IS 'Descrição da tabela';\n" +
                "COMMENT ON COLUMN schema.tabela_exemplo.id IS 'Identificador único';\n" +
                "COMMENT ON COLUMN schema.tabela_exemplo.descricao IS 'Descrição do registro';\n" +
                "COMMENT ON COLUMN schema.tabela_exemplo.ativo IS 'Indica se o registro está ativo';\n" +
                "COMMENT ON COLUMN schema.tabela_exemplo.data_criacao IS 'Data de criação do registro';\n" +
                "COMMENT ON COLUMN schema.tabela_exemplo.data_atualizacao IS 'Data da última atualização';\n";
    }

    private static String gerarTemplateRepeatable() {
        return "-- Criar ou substituir função/view\n" +
                "CREATE OR REPLACE FUNCTION schema.funcao_exemplo(\n" +
                "    p_parametro VARCHAR\n" +
                ")\n" +
                "RETURNS TABLE(\n" +
                "    id BIGINT,\n" +
                "    descricao VARCHAR\n" +
                ") AS $$\n" +
                "BEGIN\n" +
                "    RETURN QUERY\n" +
                "    SELECT \n" +
                "        t.id,\n" +
                "        t.descricao\n" +
                "    FROM schema.tabela_exemplo t\n" +
                "    WHERE t.descricao ILIKE '%' || p_parametro || '%';\n" +
                "END;\n" +
                "$$ LANGUAGE plpgsql;\n\n" +
                "-- Dicionário de Dados Vivo\n" +
                "COMMENT ON FUNCTION schema.funcao_exemplo(VARCHAR) IS 'Descrição da função: busca registros por descrição';\n";
    }
}
