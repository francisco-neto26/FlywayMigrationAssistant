package com.supergestao.Flyway.migration.assistant.ui.controller;

import atlantafx.base.theme.Theme;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Funcao;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Modulo;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.AcaoBanco;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.ObjetoBanco;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.SubAcaoBanco;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.TipoMigration;
import com.supergestao.Flyway.migration.assistant.ui.estado.ContextoAplicacao;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.estilo.GerenciadorEstiloBotao;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.List;

public class TelaNovoMigrationController implements ITelasModal {

    @FXML
    public Label lblModulo;
    @FXML
    public ComboBox<Modulo> comboModulo;
    @FXML
    public Label lblFuncao;
    @FXML
    public ComboBox<Funcao> comboFuncao;
    @FXML
    public Label lblTipo;
    @FXML
    public ComboBox<TipoMigration> comboTipo;
    @FXML
    public Label lblAcao;
    @FXML
    public ComboBox<AcaoBanco> comboAcao;
    @FXML
    public Label lblObjeto;
    @FXML
    public ComboBox<ObjetoBanco> comboObjeto;
    @FXML
    public Label lblSubAcao;
    @FXML
    public ComboBox<SubAcaoBanco> comboSubAcao;
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

    private ContextoAplicacao contexto;

    public void setContextoAplicacao(ContextoAplicacao contextoAplicacao) {
        this.contexto = contextoAplicacao;
    }

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            GerenciadorEstiloBotao.gerenciadorEstiloBotao(painelRaiz);
            iniciarCombo();
            listenerNome();
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


    private void iniciarCombo() {
        comboModulo.getItems().addAll(this.contexto.obterModulosExistentes(this.contexto.getDiretorioArquivo()).values());
        comboFuncao.setDisable(true);
        comboModulo.valueProperty().addListener((observable, moduloAntigo, moduloSelecionado) -> {
            carregarFuncaoModulo(moduloSelecionado);
        });
        comboTipo.getItems().addAll(List.of(TipoMigration.values()));
        comboTipo.valueProperty().addListener((observable, tipoAntigo, tipoSelecionado) -> {
            btnBuscarUndo.setDisable(tipoSelecionado != TipoMigration.UNDO);
        });
        comboAcao.getItems().addAll(List.of(AcaoBanco.values()));
        comboObjeto.getItems().addAll(List.of(ObjetoBanco.values()));
        comboSubAcao.getItems().addAll(List.of(SubAcaoBanco.values()));
    }

    private void carregarFuncaoModulo(Modulo modulo){
        comboFuncao.getItems().clear();
        if (modulo != null && !modulo.getFuncoes().isEmpty()) {
            comboFuncao.setPromptText("Selecione uma função");
            comboFuncao.getItems().addAll(modulo.getFuncoes());
            comboFuncao.setDisable(false);
        } else {
            comboFuncao.setDisable(true);
        }
    }

    private void listenerNome(){
        txtNomeScript.textProperty().addListener((observable, nomeAntigo, nomeNovo) -> {
            txtPreviewArquivo.setText("tste");
        });
    }


}
