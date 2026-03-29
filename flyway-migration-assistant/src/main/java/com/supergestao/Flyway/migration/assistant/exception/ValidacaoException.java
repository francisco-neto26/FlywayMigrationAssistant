package com.supergestao.Flyway.migration.assistant.exception;

public class ValidacaoException extends MigrationException {

    public ValidacaoException(String mensagem) {
        super(mensagem);
    }

    public ValidacaoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
