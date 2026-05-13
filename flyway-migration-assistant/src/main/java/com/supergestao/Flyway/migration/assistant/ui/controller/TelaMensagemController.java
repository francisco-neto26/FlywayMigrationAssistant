package com.supergestao.Flyway.migration.assistant.ui.controller;

import com.supergestao.Flyway.migration.assistant.ui.utilitario.TipoMensagem;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class TelaMensagemController {

    @FXML private Label lblTitulo;
    @FXML private TextArea txtAreaDetalhes;
    @FXML private Button btnFechar;
    @FXML private Label lblMensagem;
    @FXML private HBox hboxTopo;

    @FXML
    public void initialize() {
        txtAreaDetalhes.setVisible(false);
        txtAreaDetalhes.setManaged(false);
        btnFechar.setOnAction(event -> {
            Stage stage = (Stage) btnFechar.getScene().getWindow();
            stage.close();
        });
    }

    public void exibirMensagem(String titulo, String mensagem, String detalhes, TipoMensagem tipoMensagem) {
        lblTitulo.setText(titulo);
        hboxTopo.setStyle("-fx-background-color: " + tipoMensagem.getCorTipoMensagem() + ";");
        if (mensagem == null || mensagem.isEmpty()) {
            lblMensagem.setVisible(false);
            lblMensagem.setManaged(false);
        } else {
            lblMensagem.setText(mensagem);
            lblMensagem.setVisible(true);
            lblMensagem.setManaged(true);
        }

        if (detalhes == null || detalhes.isEmpty()) {
            txtAreaDetalhes.setVisible(false);
            txtAreaDetalhes.setManaged(false);
        } else {
            txtAreaDetalhes.setText(detalhes);
            txtAreaDetalhes.setVisible(true);
            txtAreaDetalhes.setManaged(true);
        }
    }
}
