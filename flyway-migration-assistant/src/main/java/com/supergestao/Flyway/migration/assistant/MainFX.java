package com.supergestao.Flyway.migration.assistant;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainFX extends Application {

    @Override
    public void start(Stage palcoPrincipal) {
        // Criamos um texto simples
        Label texto = new Label("Bem-vindo ao Flyway Assistant com JavaFX!");

        // Colocamos o texto no meio da tela
        StackPane layout = new StackPane(texto);

        // Criamos uma Cena com 600 de largura por 400 de altura
        Scene cena = new Scene(layout, 600, 400);

        // Configuramos a Janela
        palcoPrincipal.setTitle("Flyway Migration Assistant");
        palcoPrincipal.setScene(cena);
        palcoPrincipal.show();
    }

    public static void main(String[] args) {
        // Dispara o JavaFX
        launch(args);
    }
}
