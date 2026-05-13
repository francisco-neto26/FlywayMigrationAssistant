package com.supergestao.Flyway.migration.assistant.ui.controller;


import atlantafx.base.theme.Theme;
import com.supergestao.Flyway.migration.assistant.dominio.configuracao.GerenciadorConfiguracao;
import com.supergestao.Flyway.migration.assistant.exception.TelaException;
import com.supergestao.Flyway.migration.assistant.persistencia.gerenciador.modulos.arquivos.GerenciadorModulosArquivos;
import com.supergestao.Flyway.migration.assistant.ui.estado.ContextoAplicacao;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.Mensageiro;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.TipoMensagem;
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
            public Theme fromString(String string) {
                return null;
            }
        });

        for (Theme tema : comboTema.getItems()) {
            tema.getName();
            GerenciadorConfiguracao.getTema();
        }

        comboTema.setOnAction(event -> {
            GerenciadorConfiguracao.aplicarTema(comboTema.getValue());
        });


        btnSalvar.setOnAction(event -> {

            if (txtDiretorioModulos.getText().isEmpty()) {
                this.mensageiro.exibirMensagem("Atenção", "O diretório dos módulos não pode estar vazio.", null, TipoMensagem.AVISO);
                return;
            }
            if (txtDiretorioArquivos.getText().isEmpty()) {
                this.mensageiro.exibirMensagem("Atenção", "O diretório dos arquivos não pode estar vazio.", null, TipoMensagem.AVISO);
                return;
            }

            boolean confirmacao = this.mensageiro.pedidoConfirmacao(
                    "Configurações",
                    "Deseja salvar as configurações?",
                    null
            );

            if (confirmacao) {
                try {
                    GerenciadorConfiguracao.salvarDiretorioModulo(txtDiretorioModulos.getText());
                    GerenciadorConfiguracao.salvarDiretorioArquivo(txtDiretorioArquivos.getText());
                    GerenciadorConfiguracao.salvarTema(comboTema.getValue().getName());

                    fecharTela();

                } catch (TelaException e) {
                    this.mensageiro.exibirMensagem("Erro ao salvar configurações", "Não foi possível salvar as configurações", e.getMessage(), TipoMensagem.ERRO);
                }
            }

        });

        btnCancelar.setOnAction(event -> fecharTela());
    }

    private void fecharTela() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}
