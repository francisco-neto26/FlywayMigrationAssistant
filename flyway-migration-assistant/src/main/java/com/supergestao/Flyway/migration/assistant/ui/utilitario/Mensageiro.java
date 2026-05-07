package com.supergestao.Flyway.migration.assistant.ui.utilitario;

public interface Mensageiro {
    void exibirMensagem(String titulo, String mensagem, String detalhes);
    boolean pedidoConfirmacao(String titulo, String mensagem, String textoBotao);
}
