package com.supergestao.Flyway.migration.assistant.dominio.tipo;

public enum ObjetoBanco {
    TABLE("TABLE"),
    COLUMN("COLUMN"),
    INDEX("INDEX"),
    VIEW("VIEW"),
    MATERIALIZED_VIEW("MATERIALIZED_VIEW"),
    SEQUENCE("SEQUENCE"),
    FUNCTION("FUNCTION"),
    PROCEDURE("PROCEDURE"),
    TRIGGER("TRIGGER"),
    CONSTRAINT("CONSTRAINT"),
    FOREIGN_KEY("FOREIGN_KEY"),
    PRIMARY_KEY("PRIMARY_KEY"),
    UNIQUE("UNIQUE"),
    CHECK("CHECK"),
    TYPE("TYPE"),
    DOMAIN("DOMAIN"),
    SCHEMA("SCHEMA"),
    EXTENSION("EXTENSION"),
    POLICY("POLICY"),
    ROLE("ROLE"),
    TABLESPACE("TABLESPACE"),
    COLLATION("COLLATION"),
    GRANT("GRANT");

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
