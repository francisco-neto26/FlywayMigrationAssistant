package com.supergestao.Flyway.migration.assistant.exception;

public class ValidacaoException extends ExceptionPadrao {

    public ValidacaoException(String mensagem) {
        super(mensagem);
    }

    public ValidacaoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
