package com.supergestao.Flyway.migration.assistant.ui.utilitario.estilo;

import atlantafx.base.theme.Theme;

public class GerenciadorVisual {

    public static void aplicarTemaGlobal(Theme tema) {
        javafx.application.Application.setUserAgentStylesheet(tema.getUserAgentStylesheet());
    }

    public static void aplicarVisualGlobal(String nomeFonte, int tamanhoSistema) {
        javafx.stage.Window.getWindows().forEach(window -> {
            if (window instanceof javafx.stage.Stage stage) {
                if (stage.getScene() != null && stage.getScene().getRoot() != null) {
                    stage.getScene().getRoot().setStyle(
                            "-fx-font-family: '" + nomeFonte + "'; " +
                            "-fx-font-size: " + tamanhoSistema + "px;"
                    );
                }
            }
        });
    }
}
