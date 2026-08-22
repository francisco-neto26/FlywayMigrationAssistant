package com.supergestao.Flyway.migration.assistant.dominio.tipo;

public enum SubAcaoBanco {
    RENAME_COLUMN("RENOMEAR COLUNA"),
    ALTER_TYPE("ALTERAR TIPO DE DADO"),
    SET_NOT_NULL("ADICIONAR REGRA NOT NULL"),
    DROP_NOT_NULL("REMOVER REGRA NOT NULL"),
    SET_DEFAULT("ADICIONAR VALOR PADRÃO (DEFAULT)"),
    DROP_DEFAULT("REMOVER VALOR PADRÃO (DEFAULT)"),
    RENAME_TABLE("RENOMEAR TABELA"),
    SET_SCHEMA("MOVER PARA OUTRO SCHEMA"),
    OWNER_TO("ALTERAR PROPRIETÁRIO (OWNER TO)"),
    ENABLE_ROW_LEVEL_SECURITY("HABILITAR SEGURANÇA EM NÍVEL DE LINHA (ENABLE ROW LEVEL SECURITY)"),
    RENAME_CONSTRAINT("RENOMEAR CONSTRAINT"),
    VALIDATE_CONSTRAINT("VALIDAR CONSTRAINT (VALIDATE)"),
    SET_DEFERRABLE("ALTERAR CONSTRAINT PARA DEFERRABLE"),
    RENAME_FUNCTION("RENOMEAR FUNÇÃO"),
    RENAME_TRIGGER("RENOMEAR TRIGGER"),
    DISABLE_TRIGGER("DESATIVAR TRIGGER (DISABLE)"),
    ENABLE_TRIGGER("ATIVAR TRIGGER (ENABLE)"),
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
        return descricao.toLowerCase();
    }
}

