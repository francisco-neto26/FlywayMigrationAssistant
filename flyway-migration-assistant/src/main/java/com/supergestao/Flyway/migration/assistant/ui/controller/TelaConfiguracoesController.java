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
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.File;
import java.util.List;

public class TelaConfiguracoesController {

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
    private GerenciadorModulosArquivos gerenciadorModulosArquivos;
    private Mensageiro mensageiro;
    private String diretorioModulos;
    private String diretorioArquivos;
    private String tema;
    private List<Theme> temasDisponiveis;

    public void setGerenciador(ContextoAplicacao contextoAplicacao) {
        this.diretorioModulos = contextoAplicacao.getDiretorioModulos();
        this.diretorioArquivos = contextoAplicacao.getDiretorioArquivos();
        this.tema = contextoAplicacao.getTema();
        this.temasDisponiveis = contextoAplicacao.getTemasDisponiveis();
        this.gerenciadorModulosArquivos = contextoAplicacao.getGerenciadorModulosArquivos();
        this.mensageiro = contextoAplicacao.getMensageiro();

        txtDiretorioModulos.setText(this.diretorioModulos);
        txtDiretorioArquivos.setText(this.diretorioArquivos);
        comboTema.getItems().addAll(this.temasDisponiveis);

    }

    @FXML
    public void initialize() {

        GerenciadorEstiloBotao.BotaoConfirmar(btnSalvar);
        GerenciadorEstiloBotao.BotaoCancelar(btnCancelar);
        GerenciadorEstiloBotao.BotaoPadrao(btnProcurarDiretorioModulos);
        GerenciadorEstiloBotao.BotaoPadrao(btnProcurarDiretorioArquivos);

        btnProcurarDiretorioModulos.setOnAction(event -> {
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
        });

        btnProcurarDiretorioArquivos.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Selecione a pasta raiz dos Módulos");

            File pastaAtual = new File(txtDiretorioArquivos.getText());
            if (pastaAtual.exists() && pastaAtual.isDirectory()) {
                directoryChooser.setInitialDirectory(pastaAtual);
            }

            Stage stage = (Stage) btnProcurarDiretorioModulos.getScene().getWindow();
            File pastaSelecionada = directoryChooser.showDialog(stage);

            if (pastaSelecionada != null) {
                txtDiretorioArquivos.setText(pastaSelecionada.getAbsolutePath());
            }
        });

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

        for (Theme tema : comboTema.getItems()) {
            tema.getName();
            GerenciadorConfiguracao.getTema();
        }
        comboTema.getSelectionModel().select(GerenciadorConfiguracao.getTema());
        comboTema.setOnAction(event -> {
            GerenciadorConfiguracao.aplicarTema(comboTema.getValue());
        });


        btnSalvar.setOnAction(event -> {

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

                    fecharTela();

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

        });

        btnCancelar.setOnAction(event -> {
            GerenciadorConfiguracao.aplicarTema(GerenciadorConfiguracao.getTema());
            fecharTela();
        });
    }

    private void fecharTela() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}
