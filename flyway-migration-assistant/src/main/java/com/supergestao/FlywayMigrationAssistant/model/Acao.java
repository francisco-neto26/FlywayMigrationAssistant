package com.supergestao.FlywayMigrationAssistant.model;

public enum Acao {
    CRIAR("Create"),
    ALTERAR("Alter"),
    REMOVER("Drop"),
    RENOMEAR("Rename"),
    COMENTAR("Comment"),
    CONCEDER("Grant"),
    REVOGAR("Revoke");

    private final String label;

    Acao(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }

    public String getValorArquivo() {
        return label.toLowerCase();
    }
}