package com.supergestao.Flyway.migration.assistant.ui.controller;

import com.supergestao.Flyway.migration.assistant.persistencia.gerenciador.modulos.arquivos.GerenciadorModulosArquivos;
import com.supergestao.Flyway.migration.assistant.ui.estado.ContextoAplicacao;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.GerenciadorEstiloBotao;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.Mensageiro;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TelaNovoMigrationController {

    @FXML
    public Label lblModulo;
    @FXML
    public ComboBox comboModulo;
    @FXML
    public Label lblFuncao;
    @FXML
    public ComboBox comboFuncao;
    @FXML
    public Label lblTipo;
    @FXML
    public ComboBox comboTipo;
    @FXML
    public Label lblAcao;
    @FXML
    public ComboBox comboAcao;
    @FXML
    public Label lblObjeto;
    @FXML
    public ComboBox comboObjeto;
    @FXML
    public Label lblSubAcao;
    @FXML
    public ComboBox comboSubAcao;
    @FXML
    public Label lblBuscarMigration;
    @FXML
    public TextField txtBuscaUndo;
    @FXML
    public Button btnBuscarUndo;
    @FXML
    public Label lblNomeScript;
    @FXML
    public TextField txtNomeScript;
    @FXML
    public Label lblNomeCompleto;
    @FXML
    public TextField txtPreviewArquivo;
    @FXML
    private VBox painelRaiz;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnSalvarMigration;


    private String diretorioModulos;
    private GerenciadorModulosArquivos gerenciadorModulosArquivos;
    private Mensageiro mensageiro;

    public void setGerenciador(ContextoAplicacao contextoAplicacao) {
        this.diretorioModulos = contextoAplicacao.getDiretorioModulos();
        this.gerenciadorModulosArquivos = contextoAplicacao.getGerenciadorModulosArquivos();
        this.mensageiro = contextoAplicacao.getMensageiro();
    }

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            GerenciadorEstiloBotao.gerenciadorEstiloBotao(painelRaiz);
        });
    }

    @FXML
    private void fechar() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void salvarMigration() {

    }


}
