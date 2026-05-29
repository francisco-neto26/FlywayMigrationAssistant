package com.supergestao.Flyway.migration.assistant.ui.utilitario;

public interface IGerenciadorJanelas {
    boolean exibirDialogo(String TipoMensagem , String titulo, String mensagem, String detalhes, CoresPadrao coresPadrao);
}
