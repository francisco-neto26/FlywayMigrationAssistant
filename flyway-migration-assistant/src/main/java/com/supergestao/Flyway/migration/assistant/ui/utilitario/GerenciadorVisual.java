package com.supergestao.Flyway.migration.assistant.ui.utilitario;

import atlantafx.base.theme.Theme;

public class GerenciadorVisual {

    public static void aplicarTemaGlobal(Theme tema) {
        javafx.application.Application.setUserAgentStylesheet(tema.getUserAgentStylesheet());
    }

    public static void aplicarFonteGlobal(String nomeFonte) {
        javafx.stage.Window.getWindows().forEach(window -> {
            if (window instanceof javafx.stage.Stage stage) {
                if (stage.getScene() != null && stage.getScene().getRoot() != null) {
                    stage.getScene().getRoot().setStyle("-fx-font-family: '" + nomeFonte + "';");
                }
            }
        });
    }
}
