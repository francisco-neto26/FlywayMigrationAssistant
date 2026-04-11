package com.supergestao.Flyway.migration.assistant.dominio.tipo;

import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemErro;
import com.supergestao.Flyway.migration.assistant.exception.ValidacaoException;

public enum TipoMigration {

    VERSIONED("V", "Versioned Migrations", true),
    REPEATABLE("R", "Repeatable Migrations", false),
    UNDO("U", "Undo Migrations", false);

    private final String prefixo;
    private final String descricao;
    private final boolean requerTimestamp;

    TipoMigration(String prefixo, String descricao, boolean requerTimestamp) {
        this.prefixo = prefixo;
        this.descricao = descricao;
        this.requerTimestamp = requerTimestamp;
    }

    public String getPrefixo() {
        return prefixo;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isRequerTimestamp() {
        return requerTimestamp;
    }

    public static TipoMigration obterTipoMigration(String nomeArquivo){
        return switch (nomeArquivo.toUpperCase().charAt(0)) {
            case 'V' -> TipoMigration.VERSIONED;
            case 'U' -> TipoMigration.UNDO;
            case 'R' -> TipoMigration.REPEATABLE;
            default ->
                    throw new ValidacaoException(MensagemErro.NAO_ARQUIVO_MIGRATION.getMensagem());
        };
    }

    @Override
    public String toString() {
        return prefixo + " - " + descricao;
    }
}