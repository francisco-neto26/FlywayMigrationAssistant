package com.supergestao.Flyway.migration.assistant.ui.utilitario;

public interface Mensageiro {
    boolean exibirDialogo(String TipoMensagem , String titulo, String mensagem, String detalhes, CoresPadrao coresPadrao);
}
