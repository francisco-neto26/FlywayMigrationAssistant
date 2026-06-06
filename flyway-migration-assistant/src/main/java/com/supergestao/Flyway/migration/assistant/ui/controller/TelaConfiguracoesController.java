package com.supergestao.Flyway.migration.assistant.ui.controller;


import atlantafx.base.theme.Theme;
import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemSistema;
import com.supergestao.Flyway.migration.assistant.exception.PersistenciaException;
import com.supergestao.Flyway.migration.assistant.ui.estado.ContextoAplicacao;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.File;
import java.util.List;

public class TelaConfiguracoesController implements ITelasModal {

    @FXML
    private ComboBox<String> comboFonte;
    @FXML
    private ComboBox<String> comboDirModulo;
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
    private VBox painelRaiz;

    @FXML
    private Button btnCoresPadrao;


    private ContextoAplicacao contexto;
    String txtDiretorioModuloAntigo;

    // terminar de implementar as alterações de usamodulo, fonte
    public void setContextoAplicacao(ContextoAplicacao contextoAplicacao) {
        this.contexto = contextoAplicacao;
    }

    @FXML
    public void initialize() {

        Platform.runLater(() -> {
            GerenciadorEstiloBotao.gerenciadorEstiloBotao(painelRaiz);
            iniciarCombo();
            verficaConfigUsaModulo();
        });

    }

    private void iniciarCombo() {
        comboTema.setConverter(new StringConverter<Theme>() {
            @Override
            public String toString(Theme tema) {
                return tema != null ? tema.getName() : "";
            }

            @Override
            public Theme fromString(String string) {
                return null;
            }
        });

        txtDiretorioModulos.setText(this.contexto.getIGerenciadorConfiguracao().getDiretorioModulo());
        txtDiretorioArquivos.setText(this.contexto.getIGerenciadorConfiguracao().getDiretorioArquivo());
        comboTema.getItems().addAll(this.contexto.getIGerenciadorConfiguracao().getListaTema());
        comboFonte.getItems().addAll(javafx.scene.text.Font.getFamilies());
        comboDirModulo.getItems().addAll(List.of("Sim", "Não"));

        comboTema.getSelectionModel().select(this.contexto.getIGerenciadorConfiguracao().getTema());
        comboFonte.getSelectionModel().select(this.contexto.getIGerenciadorConfiguracao().getChaveFonte());
        comboDirModulo.getSelectionModel().select(this.contexto.getIGerenciadorConfiguracao().getChaveUsaModulo() ? "Sim": "Não");
    }

    private void verficaConfigUsaModulo() {
        if (comboDirModulo.getValue().equalsIgnoreCase("Sim")) {
            txtDiretorioModulos.setEditable(true);
            btnProcurarDiretorioModulos.setDisable(false);
            if (txtDiretorioModulos.getText() == null || txtDiretorioModulos.getText().isEmpty()) {
                txtDiretorioModulos.setText(this.txtDiretorioModuloAntigo);
            }
        } else {
            txtDiretorioModulos.setEditable(false);
            txtDiretorioModuloAntigo = txtDiretorioModulos.getText();
            txtDiretorioModulos.setText("");
            btnProcurarDiretorioModulos.setDisable(true);
        }
    }

    @FXML
    private void fechar() {
        GerenciadorVisual.aplicarTemaGlobal(comboTema.getValue());
        GerenciadorVisual.aplicarFonteGlobal(comboFonte.getValue());
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void salvar() {
        if (txtDiretorioModulos.getText().isEmpty() && comboDirModulo.getValue().equalsIgnoreCase("Sim")) {
            this.contexto.getIGerenciadorJanelas().exibirDialogo(TipoDialogo.ALERTA,
                    "Alerta",
                    null,
                    "O diretório dos módulos não pode estar vazio.");
            return;
        }
        if (txtDiretorioArquivos.getText().isEmpty()) {
            this.contexto.getIGerenciadorJanelas().exibirDialogo(TipoDialogo.ALERTA,
                    "Alerta",
                    null,
                    "O diretório dos arquivos não pode estar vazio.");
            return;
        }

        boolean confirmacao = this.contexto.getIGerenciadorJanelas().exibirDialogo(TipoDialogo.CONFIRMACAO,
                "Configurações",
                null,
                "Deseja salvar as configurações?"
        );

        if (confirmacao) {
            try {
                this.contexto.getIGerenciadorConfiguracao().salvarDiretorioModulo(txtDiretorioModulos.getText());
                this.contexto.getIGerenciadorConfiguracao().salvarDiretorioArquivo(txtDiretorioArquivos.getText());
                this.contexto.getIGerenciadorConfiguracao().salvarTema(comboTema.getValue().getName());
                this.contexto.getIGerenciadorConfiguracao().salvarChaveFonte(comboFonte.getValue());
                this.contexto.getIGerenciadorConfiguracao().salvarChaveUsaModulo(comboDirModulo.getValue());

                fechar();

            } catch (PersistenciaException e) {

                String detalhesDoErro = e.getCause() != null ? e.getCause().toString() : MensagemSistema.ERRO_GENERICO.MensagemComParametro("Erro ao salvar configurações");

                this.contexto.getIGerenciadorJanelas().exibirDialogo(TipoDialogo.ERRO,
                        "Falha Crítica",
                        "Não foi possível gravar no RegEdit do Windows",
                        detalhesDoErro
                );
            }
        }
    }

    @FXML
    private void obterDiretorioModulos() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecione o arquivo .java com os módulos");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Arquivos .java", "*.java")
        );

        if (txtDiretorioModulos.getText() != null && !txtDiretorioModulos.getText().isEmpty()) {
            File arquivoAtual = new File(txtDiretorioModulos.getText());
            if (arquivoAtual.exists() && arquivoAtual.isFile()) {
                File parentDir = arquivoAtual.getParentFile();
                if (parentDir != null && parentDir.exists() && parentDir.isDirectory()) {
                    fileChooser.setInitialDirectory(parentDir);
                }
            }
        }

        Stage stage = (Stage) btnProcurarDiretorioModulos.getScene().getWindow();
        File arquivoSelecionado = fileChooser.showOpenDialog(stage);

        if (arquivoSelecionado != null) {
            txtDiretorioModulos.setText(arquivoSelecionado.getAbsolutePath());
        }
    }

    @FXML
    private void obterDiretorioArquivos() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Selecione a pasta raiz dos Módulos");

        File pastaAtual = new File(txtDiretorioArquivos.getText());
        if (pastaAtual.exists() && pastaAtual.isDirectory()) {
            directoryChooser.setInitialDirectory(pastaAtual);
        }

        Stage stage = (Stage) btnProcurarDiretorioArquivos.getScene().getWindow();
        File pastaSelecionada = directoryChooser.showDialog(stage);

        if (pastaSelecionada != null) {
            txtDiretorioArquivos.setText(pastaSelecionada.getAbsolutePath());
        }
    }

    @FXML
    private void aplicarfonte() {
        GerenciadorVisual.aplicarFonteGlobal(comboFonte.getValue());
    }

    @FXML
    private void aplicarTema() {
        GerenciadorVisual.aplicarTemaGlobal(comboTema.getValue());
    }

    @FXML
    private void usaDiretorioModulo() {
        verficaConfigUsaModulo();
    }

    @FXML
    private void coresPadrao() {

    }

}
