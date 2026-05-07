package com.supergestao.Flyway.migration.assistant.exception;

public class ArquivoException extends ExceptionPadrao {

    public ArquivoException(String mensagem) {
        super(mensagem);
    }

    public ArquivoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
