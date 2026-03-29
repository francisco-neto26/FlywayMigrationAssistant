package com.supergestao.Flyway.migration.assistant.exception;

public class SqlException extends MigrationException {

    public SqlException(String mensagem) {
        super(mensagem);
    }

    public SqlException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
