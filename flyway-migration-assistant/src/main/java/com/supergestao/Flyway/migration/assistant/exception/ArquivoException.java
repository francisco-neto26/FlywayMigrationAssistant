package com.supergestao.Flyway.migration.assistant.exception;

public class ArquivoException extends MigrationException {

    public ArquivoException(String mensagem) {
        super(mensagem);
    }

    public ArquivoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
