package com.supergestao.Flyway.migration.assistant.exception;

public class SqlException extends ExceptionPadrao {

    public SqlException(String mensagem) {
        super(mensagem);
    }

    public SqlException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
