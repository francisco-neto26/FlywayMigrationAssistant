package com.supergestao.Flyway.migration.assistant.ui.estilo.aplicacao;
import java.awt.Font;

public enum FonteUI {

    PADRAO("Segoe UI", Font.PLAIN, 12),
    TITULO("Segoe UI", Font.BOLD, 16),
    BOTAO("Segoe UI", Font.BOLD, 13);

    private final Font fonte;

    FonteUI(String nome, int estilo, int tamanho) {
        this.fonte = new Font(nome, estilo, tamanho);
    }

    public Font getFonte() {
        return fonte;
    }
}