package com.supergestao.Flyway.migration.assistant;

import com.supergestao.Flyway.migration.assistant.ui.utilitario.CaminhoTela;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.ConstrutorJanelas;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.application.Platform;

public class MainFX extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
        ConstrutorJanelas.abrirJanelaPrincipal(primaryStage, CaminhoTela.TELA_PRINCIPAL, CaminhoTela.TELA_PRINCIPAL.getNome());
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
