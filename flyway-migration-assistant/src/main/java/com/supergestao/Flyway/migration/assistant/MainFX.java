package com.supergestao.Flyway.migration.assistant;

import com.supergestao.Flyway.migration.assistant.dominio.configuracao.GerenciadorConfiguracao;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import atlantafx.base.theme.Theme;
import javafx.application.Platform;

public class MainFX extends Application {

    @Override
    public void start(Stage primaryStage ) throws Exception {
        Theme temaSalvo = GerenciadorConfiguracao.getTema();
        Application.setUserAgentStylesheet(temaSalvo.getUserAgentStylesheet());

        FXMLLoader loaderTelaPrincipal = new FXMLLoader(getClass().getResource("/com/supergestao/Flyway/migration/assistant/ui/controller/TelaPrincipal.fxml"));
        Parent root = loaderTelaPrincipal.load();

        Scene scene = new Scene(root, 1000, 700);

        primaryStage.setTitle("Flyway Migration Assistant");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);

        primaryStage.setOnCloseRequest(event -> {
            Platform.exit();
            System.exit(0);
        });

        primaryStage .show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
