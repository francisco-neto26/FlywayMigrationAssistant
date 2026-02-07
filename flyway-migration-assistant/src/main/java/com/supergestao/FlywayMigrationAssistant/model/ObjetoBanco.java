package com.supergestao.FlywayMigrationAssistant.model;

public enum ObjetoBanco {
    TABELA("Table"),
    COLUNA("Column"),
    INDICE("Index"),
    VIEW("View"),
    VIEW_MATERIALIZADA("MatView"),
    SEQUENCIA("Sequence"),
    FUNCAO("Function"),
    PROCEDIMENTO("Procedure"),
    GATILHO("Trigger"),
    CONSTRAINT("Constraint"),
    TIPO("Type"),
    DOMINIO("Domain"),
    ESQUEMA("Schema"),
    EXTENSAO("Extension"),
    POLITICA("Policy");

    private final String label;

    ObjetoBanco(String label) {
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