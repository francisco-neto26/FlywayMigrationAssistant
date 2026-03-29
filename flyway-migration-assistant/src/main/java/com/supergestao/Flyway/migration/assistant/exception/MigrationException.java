package com.supergestao.Flyway.migration.assistant.exception;

public class MigrationException extends RuntimeException {

    public MigrationException(String mensagem) {
        super(mensagem);
    }

    public MigrationException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
