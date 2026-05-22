package com.supergestao.Flyway.migration.assistant.ui.controller;

import com.supergestao.Flyway.migration.assistant.dominio.configuracao.GerenciadorConfiguracao;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.CoresPadrao;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.GerenciadorEstiloBotao;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class JanelaBaseController {

    @FXML
    private BorderPane painelPrincipal;
    @FXML
    private HBox hboxTopo;
    @FXML
    private Label lblTitulo;
    @FXML
    private Button btnMinimizar;
    @FXML
    private Button btnMaximizar;
    @FXML
    private Button btnFechar;

    private double xOffset = 0;
    private double yOffset = 0;
    private boolean isMaximized = true;
    private double lastX, lastY, lastWidth, lastHeight;

    @FXML
    public void initialize() {
        setfonte();
        Platform.runLater(() -> {
            GerenciadorEstiloBotao.gerenciadorEstiloBotao(painelPrincipal);
        });

        hboxTopo.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        hboxTopo.setOnMouseDragged(event -> {
            Stage stage = (Stage) hboxTopo.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        btnFechar.setOnAction(event -> {
            Stage stage = (Stage) btnFechar.getScene().getWindow();
            stage.close();
        });

        btnMinimizar.setOnAction(event -> {
            Stage stage = (Stage) btnMinimizar.getScene().getWindow();
            stage.setIconified(true);
        });

        btnMaximizar.setOnAction(event -> {
            Stage stage = (Stage) btnMaximizar.getScene().getWindow();
            javafx.geometry.Rectangle2D bounds = javafx.stage.Screen.getPrimary().getVisualBounds();

            if (!isMaximized) {
                lastX = stage.getX();
                lastY = stage.getY();
                lastWidth = stage.getWidth();
                lastHeight = stage.getHeight();
                stage.setX(bounds.getMinX());
                stage.setY(bounds.getMinY());
                stage.setWidth(bounds.getWidth());
                stage.setHeight(bounds.getHeight());

                isMaximized = true;
            } else {
                stage.setX(lastWidth > 0 ? lastX : bounds.getMinX() + (bounds.getWidth() - 1000) / 2);
                stage.setY(lastHeight > 0 ? lastY : bounds.getMinY() + (bounds.getHeight() - 700) / 2);
                stage.setWidth(lastWidth > 0 ? lastWidth : 1000);
                stage.setHeight(lastHeight > 0 ? lastHeight : 700);
                isMaximized = false;
            }
        });
    }

    public void setConteudo(Node conteudo, String titulo) {
        lblTitulo.setText(titulo);

        BorderPane conteudoComMoldura = new BorderPane(conteudo);
        conteudoComMoldura.setStyle("-fx-border-color: rgba(128, 128, 128, 0.4); " +
                "-fx-border-style: solid; " +
                "-fx-border-width: 2; " +
                "-fx-effect: innershadow(one-pass-box, rgba(0,0,0,0.15), 10, 0, 0, 3);");

        BorderPane.setMargin(conteudoComMoldura, new Insets(5));
        painelPrincipal.setCenter(conteudoComMoldura);
    }

    public void setfonte(){
        String fonteEscolhida = GerenciadorConfiguracao.getChaveFonte();
        painelPrincipal.setStyle("-fx-font-family: '" + fonteEscolhida + "';");
    }

    public void defineVisibilidadeMaxMin() {
        btnMinimizar.setVisible(false);
        btnMinimizar.setManaged(false);
        btnMaximizar.setVisible(false);
        btnMaximizar.setManaged(false);
    }

    public void defineCorBarra(CoresPadrao coresPadrao) {
        hboxTopo.setStyle("-fx-background-color: " + coresPadrao.getCorTipoMensagem() + ";");
    }
}

