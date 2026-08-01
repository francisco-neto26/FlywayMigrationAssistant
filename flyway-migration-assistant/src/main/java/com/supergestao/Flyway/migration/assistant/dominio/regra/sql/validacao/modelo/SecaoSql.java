package com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.modelo;

public class SecaoSql {
    private final String conteudo;
    private final int linhaInicial;

    public SecaoSql(String conteudo, int linhaInicial) {
        this.conteudo = conteudo != null ? conteudo : "";
        this.linhaInicial = linhaInicial;
    }
    public String getConteudo() {
        return conteudo;
    }

    public int getLinhaInicial() {
        return linhaInicial;
    }

    public boolean isVazio() {
        return conteudo.trim().isEmpty();
    }
}