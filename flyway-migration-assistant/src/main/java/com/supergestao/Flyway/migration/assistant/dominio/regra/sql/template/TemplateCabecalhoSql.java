package com.supergestao.Flyway.migration.assistant.dominio.regra.sql.template;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TemplateCabecalhoSql {

    public static String montarCabecalho(String modulo, String funcao, String nomeScript,
                                         String acao, String objeto, String subAcao,String tipoMigration) {

        String dataAtual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        return "-- ================================================================\n" +
                "-- Módulo: " + (modulo != null ? modulo : "") + "\n" +
                "-- Função: " + (funcao != null ? funcao : "") + "\n" +
                "-- Nome: " + (nomeScript != null ? nomeScript : "") + "\n" +
                "-- Ação: " + (acao != null ? acao : "") + "\n" +
                "-- Objeto: " + (objeto != null ? objeto : "") + "\n" +
                "-- Sub-Ação: " + (subAcao != null ? subAcao : "") + "\n" +
                "-- Tipo: " + (tipoMigration != null ? tipoMigration : "") + "\n" +
                "-- Data Criação: " + dataAtual + "\n" +
                "-- ================================================================\n\n";
    }
}

