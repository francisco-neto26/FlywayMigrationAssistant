package com.supergestao.Flyway.migration.assistant.dominio.tipo;

public enum TipoMigration {

    VERSIONED("V", "Versioned Migrations", true),
    REPEATABLE("R", "Repeatable Migrations", false),
    UNDO("U", "Undo Migrations", false);

    private final String prefixo;
    private final String descricao;
    private final boolean requerTimestamp;

    TipoMigration(String prefixo, String descricao, boolean requerTimestamp) {
        this.prefixo = prefixo;
        this.descricao = descricao;
        this.requerTimestamp = requerTimestamp;
    }

    public String getPrefixo() {
        return prefixo;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isRequerTimestamp() {
        return requerTimestamp;
    }

    public boolean isVersioned() {
        return this == VERSIONED;
    }

    public boolean isRepeatable() {
        return this == REPEATABLE;
    }

    public boolean isUndo() {
        return this == UNDO;
    }

    @Override
    public String toString() {
        return prefixo + " - " + descricao;
    }
}