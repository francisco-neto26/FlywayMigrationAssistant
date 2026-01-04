package com.supergestao.FlywayMigrationAssistant.model;

import java.awt.*;

public enum Cores {

    AZUL_ESCURO(0, 0, 139),
    VERDE(0, 128, 0),
    VERMELHO_STRING(163, 21, 21),
    LARANJA(255, 102, 0),
    ROXO(153, 0, 153),
    PRETO(0, 0, 0),
    VERDE_SUCESSO(0, 100, 0),
    VERMELHO_ALERTA(255, 0, 0),
    AZUL_PADRAO(0, 0, 150);

    private final Color cor;

    Cores(int r, int g, int b) {
        this.cor = new Color(r, g, b);
    }

    public Color getCor() {
        return cor;
    }
}
