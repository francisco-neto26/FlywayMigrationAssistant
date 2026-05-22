package com.supergestao.Flyway.migration.assistant.ui.controller;


import atlantafx.base.theme.Theme;
import com.supergestao.Flyway.migration.assistant.dominio.configuracao.GerenciadorConfiguracao;
import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemErro;
import com.supergestao.Flyway.migration.assistant.exception.PersistenciaException;
import com.supergestao.Flyway.migration.assistant.persistencia.gerenciador.modulos.arquivos.GerenciadorModulosArquivos;
import com.supergestao.Flyway.migration.assistant.ui.estado.ContextoAplicacao;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.GerenciadorEstiloBotao;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.Mensageiro;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.CoresPadrao;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.File;
import java.util.List;

public class TelaConfiguracoesController {

    @FXML
    public ComboBox<String> comboFonte;
    @FXML
    public ComboBox<String> comboDirModulo;
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

    private Mensageiro mensageiro;
    private boolean usaModulo;
    String txtDiretorioModuloAntigo;
// terminar de implementar as alterações de usamodulo, fonte
    public void setGerenciador(ContextoAplicacao contextoAplicacao) {

        List<Theme> temasDisponiveis = contextoAplicacao.getTemasDisponiveis();
        List<String> fontesDisponiveis = javafx.scene.text.Font.getFamilies();
        this.mensageiro = contextoAplicacao.getMensageiro();
        this.usaModulo = contextoAplicacao.getUsaModulo();
        txtDiretorioModulos.setText(contextoAplicacao.getDiretorioModulos());
        txtDiretorioArquivos.setText(contextoAplicacao.getDiretorioArquivos());
        comboTema.getItems().addAll(temasDisponiveis);
        comboFonte.getItems().addAll(fontesDisponiveis);
        comboDirModulo.getItems().addAll(List.of("Sim", "Não"));

    }

    @FXML
    public void initialize() {

        Platform.runLater(() -> {
            GerenciadorEstiloBotao.gerenciadorEstiloBotao(painelRaiz);
            verficaConfigUsaModulo();
        });
        iniciarCombo();
    }

    private void iniciarCombo(){
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

        comboTema.getSelectionModel().select(GerenciadorConfiguracao.getTema());
        comboFonte.getSelectionModel().select(GerenciadorConfiguracao.getChaveFonte());
        comboDirModulo.getSelectionModel().select(GerenciadorConfiguracao.getChaveUsaModulo() ? "Sim": "Não");
    }

    private void verficaConfigUsaModulo(){
        if (comboDirModulo.getValue().equalsIgnoreCase("Sim")){
            txtDiretorioModulos.setEditable(true);
            btnProcurarDiretorioModulos.setDisable(false);
            if(txtDiretorioModulos.getText().isEmpty()){
                txtDiretorioModulos.setText(this.txtDiretorioModuloAntigo);
            }
        }else{
            txtDiretorioModulos.setEditable(false);
            txtDiretorioModuloAntigo = txtDiretorioModulos.getText();
            txtDiretorioModulos.setText("");
            btnProcurarDiretorioModulos.setDisable(true);
        }
    }

    @FXML
    private void fechar() {
        GerenciadorVisual.aplicarTemaGlobal(comboTema.getValue());
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void salvar() {
        if (txtDiretorioModulos.getText().isEmpty()) {
            this.mensageiro.exibirDialogo("m",
                    "Alerta",
                    null,
                    "O diretório dos módulos não pode estar vazio.",
                    CoresPadrao.AVISO);
            return;
        }
        if (txtDiretorioArquivos.getText().isEmpty()) {
            this.mensageiro.exibirDialogo("m",
                    "Alerta",
                    null,
                    "O diretório dos arquivos não pode estar vazio.",
                    CoresPadrao.AVISO);
            return;
        }

        boolean confirmacao = this.mensageiro.exibirDialogo("c",
                "Configurações",
                null,
                "Deseja salvar as configurações?",
                CoresPadrao.BOTAO_CONFIRMAR
        );

        if (confirmacao) {
            try {
                GerenciadorConfiguracao.salvarDiretorioModulo(txtDiretorioModulos.getText());
                GerenciadorConfiguracao.salvarDiretorioArquivo(txtDiretorioArquivos.getText());
                GerenciadorConfiguracao.salvarTema(comboTema.getValue().getName());
                GerenciadorConfiguracao.salvarChaveFonte(comboFonte.getValue());
                GerenciadorConfiguracao.salvarChaveUsaModulo(comboDirModulo.getValue());

                fechar();

            } catch (PersistenciaException e) {

                String detalhesDoErro = e.getCause() != null ? e.getCause().toString() : MensagemErro.ERRO_GENERICO.MensagemComParametro("Erro ao salvar configurações");

                this.mensageiro.exibirDialogo(
                        "c",
                        "Falha Crítica",
                        "Não foi possível gravar no RegEdit do Windows",
                        detalhesDoErro,
                        CoresPadrao.ERRO
                );
            }
        }
    }
    @FXML
    private void obterDiretorioModulos(){
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
    private void obterDiretorioArquivos(){
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
    private void aplicarfonte(){
        GerenciadorVisual.aplicarFonteGlobal(comboFonte.getValue());
    }

    @FXML
    private void aplicarTema(){
        GerenciadorVisual.aplicarTemaGlobal(comboTema.getValue());
    }

    @FXML
    private void usaDiretorioModulo(){
        verficaConfigUsaModulo();
    }



}
