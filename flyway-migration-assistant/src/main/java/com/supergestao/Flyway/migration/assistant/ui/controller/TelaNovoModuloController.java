package com.supergestao.Flyway.migration.assistant.ui.controller;

import com.supergestao.Flyway.migration.assistant.exception.TelaException;
import com.supergestao.Flyway.migration.assistant.persistencia.gerenciador.modulos.arquivos.GerenciadorModulosArquivos;
import com.supergestao.Flyway.migration.assistant.ui.estado.ContextoAplicacao;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.Mensageiro;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.CoresPadrao;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.nio.file.Paths;

public class TelaNovoModuloController {
    @FXML
    private TextField txtNomeModulo;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnCriarModulo;
    private GerenciadorModulosArquivos gerenciadorModulosArquivos;
    private Mensageiro mensageiro;
    private String diretorioModulos;


    public void setGerenciador(ContextoAplicacao contextoAplicacao) {
        this.gerenciadorModulosArquivos = contextoAplicacao.getGerenciadorModulosArquivos();
        this.mensageiro = contextoAplicacao.getMensageiro();
        this.diretorioModulos = contextoAplicacao.getDiretorioModulos();
    }

    @FXML
    public void initialize() {
        btnCancelar.setOnAction(event -> {
            Stage stage = (Stage) btnCancelar.getScene().getWindow();
            stage.close();
        });

        btnCriarModulo.setOnAction(event -> {
            String nomeModulo = txtNomeModulo.getText().trim();
            if (!nomeModulo.isEmpty()) {
                String caminho = Paths.get(this.diretorioModulos, nomeModulo).toAbsolutePath().toString();
                boolean confirmacao = this.mensageiro.exibirDialogo("c",
                        "Novo Módulo",
                        null,
                        "Deseja criar o módulo '" + nomeModulo + "' no diretorio '" + caminho + "'?",
                        CoresPadrao.INFO
                );

                if (confirmacao) {
                    try {
                        this.gerenciadorModulosArquivos.salvarModuloFuncao(caminho);
                        Stage stage = (Stage) btnCriarModulo.getScene().getWindow();
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
        });
    }
}
