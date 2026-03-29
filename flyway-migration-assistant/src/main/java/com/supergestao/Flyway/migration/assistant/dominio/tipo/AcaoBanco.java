package com.supergestao.Flyway.migration.assistant.dominio.tipo;

public enum AcaoBanco {
    CREATE("create"),
    ALTER("alter"),
    DROP("drop"),
    ADD("add"),
    REMOVE("remove"),
    UPDATE("update");

    private final String acao;

    AcaoBanco(String acao) {
        this.acao = acao;
    }

    public String getAcao() {
        return acao;
    }

    public boolean isCreate() {
        return this == CREATE;
    }

    public boolean isDropOrRemove() {
        return this == DROP || this == REMOVE;
    }

    @Override
    public String toString() {
        return acao;
    }
}
