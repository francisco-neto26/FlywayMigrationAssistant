package com.supergestao.Flyway.migration.assistant.exception;

public class ExceptionPadrao extends RuntimeException {

    public ExceptionPadrao(String mensagem) {
        super(mensagem);
    }

    public ExceptionPadrao(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
