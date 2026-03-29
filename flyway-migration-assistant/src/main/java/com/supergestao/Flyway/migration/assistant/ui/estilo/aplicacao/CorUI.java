package com.supergestao.Flyway.migration.assistant.ui.estilo.aplicacao;

import java.awt.Color;

public enum CorUI {
    TEXTO_PADRAO(new Color(33, 33, 33)),
    TEXTO_SECUNDARIO(new Color(97, 97, 97)),
    TEXTO_DESTAQUE(new Color(0, 0, 0)),
    INFO(new Color(33, 150, 243)),       // azul padrão moderno
    SUCESSO(new Color(56, 142, 60)),     // verde agradável
    ALERTA(new Color(255, 193, 7)),      // amarelo warning
    ERRO(new Color(211, 47, 47));        // vermelho mais suave

    private final Color cor;

    CorUI(Color cor) {
        this.cor = cor;
    }

    public Color getCor() {
        return cor;
    }
}
