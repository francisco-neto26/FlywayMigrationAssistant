package com.supergestao.Flyway.migration.assistant.dominio.tipo;

public enum ObjetoBanco {
    TABLE("table"),
    COLUMN("column"),
    INDEX("index"),
    VIEW("view"),
    MATERIALIZED_VIEW("materialized view"),
    SEQUENCE("sequence"),
    FUNCTION("function"),
    PROCEDURE("procedure"),
    TRIGGER("trigger"),
    CONSTRAINT("constraint"),
    FOREIGN_KEY("foreign Key"),
    PRIMARY_KEY("primary Key"),
    UNIQUE("unique"),
    CHECK("check"),
    TYPE("type"),
    DOMAIN("domain"),
    SCHEMA("schema"),
    EXTENSION("extension"),
    POLICY("policy"),
    ROLE("role"),
    TABLESPACE("tablespace"),
    COLLATION("collation"),
    GRANT("grant");

    private final String identificador;

    ObjetoBanco(String identificador) {
        this.identificador = identificador;
    }

    public String getIdentificador() {
        return identificador;
    }

    @Override
    public String toString() {
        return identificador;
    }


}
