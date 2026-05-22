package com.supergestao.Flyway.migration.assistant.ui.controller;

import com.supergestao.Flyway.migration.assistant.exception.TelaException;
import com.supergestao.Flyway.migration.assistant.persistencia.gerenciador.modulos.arquivos.GerenciadorModulosArquivos;
import com.supergestao.Flyway.migration.assistant.ui.estado.ContextoAplicacao;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.GerenciadorEstiloBotao;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.Mensageiro;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.CoresPadrao;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.nio.file.Paths;

public class TelaNovoModuloController {
    @FXML
    private TextField txtNomeModulo;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnSalvarModulo;
    @FXML
    private VBox painelRaiz;

    private GerenciadorModulosArquivos gerenciadorModulosArquivos;
    private Mensageiro mensageiro;
    private String diretorioArquivos;


    public void setGerenciador(ContextoAplicacao contextoAplicacao) {
        this.gerenciadorModulosArquivos = contextoAplicacao.getGerenciadorModulosArquivos();
        this.mensageiro = contextoAplicacao.getMensageiro();
        this.diretorioArquivos = contextoAplicacao.getDiretorioArquivos();
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
    private void salvarModulo(){
        String nomeModulo = txtNomeModulo.getText().trim();
        if (!nomeModulo.isEmpty()) {
            String caminho = Paths.get(this.diretorioArquivos, nomeModulo).toAbsolutePath().toString();
            boolean confirmacao = this.mensageiro.exibirDialogo("c",
                    "Novo Módulo",
                    null,
                    "Deseja criar o módulo '" + nomeModulo + "' no diretorio '" + caminho + "'?",
                    CoresPadrao.INFO
            );

            if (confirmacao) {
                try {
                    this.gerenciadorModulosArquivos.salvarModuloFuncao(caminho);
                    Stage stage = (Stage) btnSalvarModulo.getScene().getWindow();
                    stage.close();

                } catch (TelaException e) {
                    this.mensageiro.exibirDialogo("m",
                            "Erro ao Salvar",
                            "Não foi possível criar o módulo",
                            e.getMessage(),
                            CoresPadrao.ERRO
                    );
                }
            }
        } else {
            this.mensageiro.exibirDialogo("m",
                    "Atenção", null,
                    "O nome do módulo não pode estar vazio.",
                    CoresPadrao.AVISO
            );
        }
    }

}
