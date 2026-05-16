package com.supergestao.Flyway.migration.assistant.ui.controller;

import com.supergestao.Flyway.migration.assistant.ui.utilitario.CoresPadrao;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.GerenciadorEstiloBotao;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class TelaDialogoController {

    @FXML
    private Label lblMensagem;
    @FXML
    private TextArea txtAreaDetalhes;
    @FXML
    private Button btnConfirmar;
    @FXML
    private Button btnCancelar;

    private boolean confirmado = false;

    @FXML
    public void initialize() {

        GerenciadorEstiloBotao.BotaoConfirmar(btnConfirmar);
        GerenciadorEstiloBotao.BotaoCancelar(btnCancelar);

        btnConfirmar.setOnAction(event -> fechar(true));
        btnCancelar.setOnAction(event -> fechar(false));
    }

    public void telaMensagem(String mensagem, String detalhes) {

        lblMensagem.setText(mensagem);
        btnCancelar.setVisible(false);
        btnCancelar.setManaged(false);
        btnConfirmar.setText("Sair");

        if (detalhes != null && !detalhes.isBlank()) {
            txtAreaDetalhes.setText(detalhes);

            if(txtAreaDetalhes.getText().length() < 80){
                txtAreaDetalhes.setMaxHeight(60);
                VBox.setMargin(txtAreaDetalhes, new Insets(20, 0, 0, 0));
            } else if (txtAreaDetalhes.getText().length() < 180) {
                txtAreaDetalhes.setMaxHeight(100);
                VBox.setMargin(txtAreaDetalhes, new Insets(10, 0, 0, 0));
            }else {
                VBox.setMargin(txtAreaDetalhes, new Insets(0, 0, 0, 0));
            }

            txtAreaDetalhes.setVisible(true);
            txtAreaDetalhes.setManaged(true);
        } else {
            txtAreaDetalhes.setVisible(false);
            txtAreaDetalhes.setManaged(false);
        }
    }

    public void telaConfirmacao(String detalhes) {

        lblMensagem.setVisible(false);
        lblMensagem.setManaged(false);

        txtAreaDetalhes.setText(detalhes);
        txtAreaDetalhes.setVisible(true);
        txtAreaDetalhes.setManaged(true);

        if(txtAreaDetalhes.getText().length() < 180){
            txtAreaDetalhes.setMaxHeight(80);
            VBox vboxSuperior = (VBox) txtAreaDetalhes.getParent();
            vboxSuperior.setAlignment(Pos.CENTER);
        }

        btnCancelar.setVisible(true);
        btnCancelar.setManaged(true);
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    private void fechar(boolean escolha) {
        this.confirmado = escolha;
        Stage stage = (Stage) btnConfirmar.getScene().getWindow();
        stage.close();
    }
}

