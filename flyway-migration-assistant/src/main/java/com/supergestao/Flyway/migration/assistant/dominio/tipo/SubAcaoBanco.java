package com.supergestao.Flyway.migration.assistant.dominio.tipo;

public enum SubAcaoBanco {
    RENAME_COLUMN("Renomear Coluna"),
    ALTER_TYPE("Alterar Tipo de Dado"),
    SET_NOT_NULL("Adicionar Regra NOT NULL"),
    DROP_NOT_NULL("Remover Regra NOT NULL"),
    SET_DEFAULT("Adicionar Valor Padrão (DEFAULT)"),
    DROP_DEFAULT("Remover Valor Padrão (DEFAULT)"),
    RENAME_TABLE("Renomear Tabela/View"),
    SET_SCHEMA("Mover para outro Schema"),
    OWNER_TO("Alterar Proprietário (Owner)"),
    ENABLE_ROW_LEVEL_SECURITY("Habilitar RLS (Row Level Security)"),
    RENAME_CONSTRAINT("Renomear Constraint"),
    VALIDATE_CONSTRAINT("Validar Constraint (NOT VALID para VALID)"),
    SET_DEFERRABLE("Alterar para DEFERRABLE (Atraso na validação)"),
    RENAME_FUNCTION("Renomear Função"),
    RENAME_TRIGGER("Renomear Trigger"),
    DISABLE_TRIGGER("Desativar Trigger (DISABLE)"),
    ENABLE_TRIGGER("Ativar Trigger (ENABLE)"),
    NENHUMA("Ação Padrão / Nenhuma");

    private final String descricao;

    SubAcaoBanco(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static java.util.List<SubAcaoBanco> obterPorObjeto(ObjetoBanco objeto) {
        return switch (objeto) {
            case COLUMN -> java.util.List.of(
                    RENAME_COLUMN, ALTER_TYPE, SET_NOT_NULL, DROP_NOT_NULL, SET_DEFAULT, DROP_DEFAULT
            );
            case TABLE, VIEW, MATERIALIZED_VIEW -> java.util.List.of(
                    RENAME_TABLE, SET_SCHEMA, OWNER_TO, ENABLE_ROW_LEVEL_SECURITY
            );
            case CONSTRAINT -> java.util.List.of(
                    RENAME_CONSTRAINT, VALIDATE_CONSTRAINT, SET_DEFERRABLE
            );
            case FUNCTION, PROCEDURE -> java.util.List.of(
                    RENAME_FUNCTION, SET_SCHEMA, OWNER_TO
            );
            case TRIGGER -> java.util.List.of(
                    RENAME_TRIGGER, DISABLE_TRIGGER, ENABLE_TRIGGER
            );
            case INDEX -> java.util.List.of(
                    RENAME_TABLE
            );
            default -> java.util.List.of(NENHUMA);
        };
    }

    @Override
    public String toString() {
        return descricao;
    }
}

