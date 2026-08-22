package com.supergestao.Flyway.migration.assistant.dominio.tipo;

import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemSistema;
import com.supergestao.Flyway.migration.assistant.exception.ValidacaoException;

public enum AcaoBanco {
    CREATE("CREATE"),
    ALTER("ALTER"),
    DROP("DROP"),
    ADD("ADD"),
    REMOVE("REMOVE"),
    UPDATE("UPDATE");

    private final String acao;

    AcaoBanco(String acao) {
        this.acao = acao;
    }

    public String getAcao() {
        return acao;
    }

    public AcaoBanco getOposto() {
        return switch (this) {
            case CREATE -> DROP;
            case DROP   -> CREATE;
            case ADD    -> REMOVE;
            case REMOVE -> ADD;
            case ALTER  -> ALTER;
            case UPDATE -> UPDATE;
        };
    }

    public static AcaoBanco obterAcaoBanco(String nome){
        return switch (nome.toLowerCase()) {
            case "create" -> AcaoBanco.CREATE;
            case "drop" -> AcaoBanco.DROP;
            case  "add" -> AcaoBanco.ADD;
            case "remove" -> AcaoBanco.REMOVE;
            case "alter"  -> AcaoBanco.ALTER;
            case "update" -> AcaoBanco.UPDATE;
            default ->
                    throw new ValidacaoException(MensagemSistema.ERRO_OBTER_ACAO_BANCO.MensagemComParametro(nome));
        };
    }


    @Override
    public String toString() {
        return acao;
    }
}
