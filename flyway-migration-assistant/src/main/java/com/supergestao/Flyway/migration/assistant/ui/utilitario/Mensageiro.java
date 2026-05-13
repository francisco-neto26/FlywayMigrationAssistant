package com.supergestao.Flyway.migration.assistant.ui.utilitario;

public interface Mensageiro {
    void exibirMensagem(String titulo, String mensagem, String detalhes, TipoMensagem tipoMensagem);

    default void exibirMensagem(String titulo, String mensagem, String detalhes) {
        this.exibirMensagem(titulo, mensagem, detalhes, TipoMensagem.INFO);
    }

    boolean pedidoConfirmacao(String titulo, String mensagem, String textoBotao);
}
