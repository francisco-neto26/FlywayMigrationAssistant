package com.supergestao.Flyway.migration.assistant.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class TelaConfirmacaoController {

    @FXML private Label lblTitulo;
    @FXML private Label lblMensagem;
    @FXML private Button btnCancelar;
    @FXML private Button btnConfirmar;

    private boolean confirmado = false;

    @FXML
    public void initialize() {
        btnCancelar.setOnAction(event -> fecharTela(false));
        btnConfirmar.setOnAction(event -> fecharTela(true));
    }

    public void carregarDados(String titulo, String mensagem, String textoBotaoConfirmar) {
        lblTitulo.setText(titulo);
        lblMensagem.setText(mensagem);
        if (textoBotaoConfirmar != null && !textoBotaoConfirmar.isEmpty()) {
            btnConfirmar.setText(textoBotaoConfirmar);
        }
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    private void fecharTela(boolean escolha) {
        this.confirmado = escolha;
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}
