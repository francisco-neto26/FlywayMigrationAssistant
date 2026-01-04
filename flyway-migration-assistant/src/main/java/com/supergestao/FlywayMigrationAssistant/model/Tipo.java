package com.supergestao.FlywayMigrationAssistant.model;

public enum Tipo {
    VERSIONED("V", "Versioned Migrations", "Alterar a estrutura física do banco de dados, cria tabelas, adiciona colunas, remove colunas ou cria índices", true),
    REPEATABLE("R", "Repeatable Migrations", "Armazenar a lógica e objetos que podem ser sobrescritos, gerencia View, Functon, Procedure e Trigger", false),
    UNDO("U", "Undo Migrations", "Reverter uma migração versionada específica. Basicamente faz o oposto do que o arquivo V de mesma versão fez (ex: se o V deu um CREATE, o U dá um DROP)", false);

    private final String prefixo;
    private final String descricao;
    private final String uso;
    private final boolean requerTimestamp;

    Tipo(String prefixo, String descricao, String uso, boolean requerTimestamp) {
        this.prefixo = prefixo;
        this.descricao = descricao;
        this.uso = uso;
        this.requerTimestamp = requerTimestamp;
    }

    public String getprefixo() {
        return prefixo;
    }

    public String getdescricao() {
        return descricao;
    }

    public String getUso() {
        return uso;
    }

    public boolean getrequerTimestamp() {
        return requerTimestamp;
    }

    @Override
    public String toString() {
        return prefixo + " - " + descricao;
    }

}
