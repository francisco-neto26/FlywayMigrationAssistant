package com.supergestao.Flyway.migration.assistant.ui.controller;


import atlantafx.base.theme.Theme;
import com.supergestao.Flyway.migration.assistant.dominio.configuracao.GerenciadorConfiguracao;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.File;

public class ConfiguracoesController {

    @FXML
    private TextField txtDiretorioModulos;
    @FXML
    private Button btnProcurarDiretorioModulos;
    @FXML
    private TextField txtDiretorioArquivos;
    @FXML
    private Button btnProcurarDiretorioArquivos;
    @FXML
    private ComboBox<Theme> comboTema;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnSalvar;

    @FXML
    public void initialize() {
        txtDiretorioModulos.setText(GerenciadorConfiguracao.getDiretorioModulo());
        txtDiretorioArquivos.setText(GerenciadorConfiguracao.getDiretorioArquivo());
        comboTema.getItems().addAll(GerenciadorConfiguracao.CHAVE_TEMAS_DISPONIVEIS);

        btnProcurarDiretorioModulos.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Selecione a pasta raiz dos Módulos");

            File pastaAtual = new File(txtDiretorioModulos.getText());
            if (pastaAtual.exists() && pastaAtual.isDirectory()) {
                directoryChooser.setInitialDirectory(pastaAtual);
            }

            Stage stage = (Stage) btnProcurarDiretorioModulos.getScene().getWindow();
            File pastaSelecionada = directoryChooser.showDialog(stage);

            if (pastaSelecionada != null) {
                txtDiretorioModulos.setText(pastaSelecionada.getAbsolutePath());
            }
        });

        btnProcurarDiretorioArquivos.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Selecione o arquivo .java com os módulos");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Arquivos .java", "*.java")
            );

            Stage stage = (Stage) btnProcurarDiretorioArquivos.getScene().getWindow();
            File arquivoSelecionado = fileChooser.showOpenDialog(stage);

            if (arquivoSelecionado != null) {
                txtDiretorioArquivos.setText(arquivoSelecionado.getAbsolutePath());
            }
        });

        comboTema.setConverter(new StringConverter<Theme>() {
            @Override
            public String toString(Theme tema) {
                return tema != null ? tema.getName() : "";
            }
            @Override
            public Theme fromString(String string) { return null; }
        });

        for (Theme tema : comboTema.getItems()) {
            if (tema.getName().equals(GerenciadorConfiguracao.getTema())) {
                comboTema.setValue(tema);
                break;
            }
        }

        comboTema.setOnAction(event -> {
            GerenciadorConfiguracao.aplicarTema(comboTema.getValue());
        });


        btnSalvar.setOnAction(event -> {
            GerenciadorConfiguracao.salvarDiretorioModulo(txtDiretorioModulos.getText());
            GerenciadorConfiguracao.salvarDiretorioArquivo(txtDiretorioArquivos.getText());
            GerenciadorConfiguracao.salvarTema(comboTema.getValue().getName());
            fecharTela();
        });

        btnCancelar.setOnAction(event -> fecharTela());
    }

    private void fecharTela() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}
