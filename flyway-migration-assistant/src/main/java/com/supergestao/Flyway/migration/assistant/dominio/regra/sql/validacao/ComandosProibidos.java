package com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao;

public enum ComandosProibidos {
    DROP_DATABASE("DROP DATABASE"),
    ALTER_SYSTEM("ALTER SYSTEM"),
    DROP_SCHEMA("DROP SCHEMA"),
    BEGIN("BEGIN;"),
    COMMIT("COMMIT;"),
    ROLLBACK("ROLLBACK;"),
    USE("USE "),
    UPDATE_SEM_WHERE("(?i)UPDATE\\s+[^;]+(?<!WHERE[^;]{0,500});"),
    DELETE_SEM_WHERE("(?i)DELETE\\s+FROM\\s+[^;]+(?<!WHERE[^;]{0,500});");

    private final String comando;

    ComandosProibidos(String comando) {
        this.comando = comando;
    }

    public String getComando() {
        return comando;
    }

    public static ComandosProibidos[] getRegrasSeguranca() {
        return new ComandosProibidos[]{DROP_DATABASE, ALTER_SYSTEM, DROP_SCHEMA};
    }

    public static ComandosProibidos[] getRegrasFlyway() {
        return new ComandosProibidos[]{BEGIN, COMMIT, ROLLBACK, USE};
    }

    public static ComandosProibidos[] getRegrasSemantica() {
        return new ComandosProibidos[]{UPDATE_SEM_WHERE, DELETE_SEM_WHERE};
    }

}
