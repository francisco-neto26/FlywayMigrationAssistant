package com.supergestao.Flyway.migration.assistant.dominio.regra.sql.template.padrao;

import com.supergestao.Flyway.migration.assistant.dominio.modelo.ArquivoNovo;
import com.supergestao.Flyway.migration.assistant.dominio.regra.tempo.GeradorDataHora;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TemplateCabecalhoSql {

    public static String montarCabecalho(ArquivoNovo arquivoNovo) {

        return "-- ================================================================\n" +
                "-- Módulo: " + (arquivoNovo.getModuloNome()) + "\n" +
                "-- Função: " + (arquivoNovo.getFuncaoNome()) + "\n" +
                "-- Nome: " + (arquivoNovo.getNome()) + "\n" +
                "-- Ação: " + (arquivoNovo.getSubAcaoBancoNome()) + "\n" +
                "-- Objeto: " + (arquivoNovo.getObjetoBancoNome()) + "\n" +
                "-- Sub-Ação: " + (arquivoNovo.getSubAcaoBancoNome()) + "\n" +
                "-- Tipo: " + (arquivoNovo.getTipo().getDescricao()) + "\n" +
                "-- Data Criação: " + GeradorDataHora.formatarTimestampLocalidade(arquivoNovo.getDataCriacao(), "dd/MM/yyyy HH:mm:ss") + "\n" +
                "-- ================================================================\n\n";
    }
}

