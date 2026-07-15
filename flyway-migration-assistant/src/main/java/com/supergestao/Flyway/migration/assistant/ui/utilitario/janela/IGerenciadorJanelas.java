package com.supergestao.Flyway.migration.assistant.ui.utilitario.janela;

public interface IGerenciadorJanelas {
    boolean exibirDialogo(TipoDialogo tipoDialogo, String titulo, String mensagem, String detalhes);
}
