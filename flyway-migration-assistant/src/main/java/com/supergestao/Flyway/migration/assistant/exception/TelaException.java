package com.supergestao.Flyway.migration.assistant.exception;

public class TelaException extends ExceptionPadrao {

    public TelaException(String mensagem) {
        super(mensagem);
    }

    public TelaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}