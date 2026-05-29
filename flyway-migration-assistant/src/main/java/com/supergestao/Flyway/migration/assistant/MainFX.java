package com.supergestao.Flyway.migration.assistant;

import com.supergestao.Flyway.migration.assistant.ui.estado.ContextoAplicacao;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.CaminhoTela;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.ConstrutorJanelas;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.application.Platform;

public class MainFX extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        stage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });
        ContextoAplicacao contextoGlobal = new ContextoAplicacao();
        ConstrutorJanelas.abrirJanelaPrincipal(stage, CaminhoTela.TELA_PRINCIPAL, CaminhoTela.TELA_PRINCIPAL.getNome(), contextoGlobal);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
