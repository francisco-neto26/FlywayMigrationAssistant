package com.supergestao.Flyway.migration.assistant.ui.utilitario;

public enum TipoMensagem {
    ERRO("#e74c3c"),     // Vermelho
    AVISO("#f39c12"),    // Amarelo
    SUCESSO("#27ae60"),  // Verde
    INFO("#2980b9");     // Azul

    private final String corTipoMensagem;

    TipoMensagem(String corTipoMensagem) {
        this.corTipoMensagem = corTipoMensagem;
    }

    public String getCorTipoMensagem() {
        return corTipoMensagem;
    }
}
