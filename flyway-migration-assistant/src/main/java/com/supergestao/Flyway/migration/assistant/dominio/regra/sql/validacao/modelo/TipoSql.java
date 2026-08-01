package com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.modelo;

public enum TipoSql {
    FUNCTION("Função / Procedure"),
    TRIGGER("Trigger"),
    VIEW("View"),
    BLOCO_ANONIMO("Bloco Anônimo (DO)"),
    CREATE_TABLE("Criação de Tabela"),
    CREATE_INDEX("Criação de Índice"),
    CREATE_SEQUENCE("Criação de Sequence"),
    CREATE_SCHEMA("Criação de Schema"),
    CREATE_TYPE("Criação de Tipo / Enum"),
    CREATE_EXTENSION("Criação de Extensão"),
    ALTER_TABLE("Alteração de Tabela"),
    COMANDO_SQL("Comando SQL sem bloco"),
    SCRIPT_VAZIO("Script vazio");

    private final String descricao;

    TipoSql(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}

