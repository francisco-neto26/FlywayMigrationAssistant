package com.supergestao.Flyway.migration.assistant.exception;

public class PersistenciaException extends ExceptionPadrao {

    public PersistenciaException(String mensagem) {
        super(mensagem);
    }

    public PersistenciaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
