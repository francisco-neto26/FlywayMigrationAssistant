package com.supergestao.Flyway.migration.assistant.ui.controller;

import com.supergestao.Flyway.migration.assistant.dominio.configuracao.GerenciadorConfiguracao;
import com.supergestao.Flyway.migration.assistant.exception.TelaException;
import com.supergestao.Flyway.migration.assistant.persistencia.gerenciador.modulos.arquivos.GerenciadorModulosArquivos;
import com.supergestao.Flyway.migration.assistant.persistencia.gerenciador.modulos.arquivos.GerenciadorModulosArquivosDisco;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.GerenciadorJanelas;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.Mensageiro;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Paths;

public class NovoModuloController {
    @FXML
    private TextField txtNomeModulo;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnCriarModulo;
    private GerenciadorModulosArquivos gerenciadorModulosArquivos;
    private Mensageiro mensageiro;


    public void setGerenciador(GerenciadorModulosArquivos gerenciadorModulosArquivos, Mensageiro mensageiro) {
        this.gerenciadorModulosArquivos = gerenciadorModulosArquivos;
        this.mensageiro = mensageiro;
    }

    @FXML
    public void initialize() {
        btnCancelar.setOnAction(event -> {
            Stage stage = (Stage) btnCancelar.getScene().getWindow();
            stage.close();
        });

        btnCriarModulo.setOnAction(event -> {
            String nomeModulo = txtNomeModulo.getText().trim();
            String caminho = Paths.get(GerenciadorConfiguracao.getDiretorioModulo(), nomeModulo).toAbsolutePath().toString();
            if (!nomeModulo.isEmpty()) {

                boolean temCerteza = this.mensageiro.pedidoConfirmacao(
                        "Novo Módulo",
                        "Deseja criar o módulo '" + nomeModulo + "' no diretorio '" + caminho + "'?",
                        null
                );

                if (temCerteza) {
                    try {
                        this.gerenciadorModulosArquivos.salvarModulo(caminho);
                        Stage stage = (Stage) btnCriarModulo.getScene().getWindow();
                        stage.close();

                    } catch (TelaException e) {
                        this.mensageiro.exibirMensagem("Erro ao Salvar", "Não foi possível criar o módulo", e.getMessage());
                    }
                }
            } else {
                this.mensageiro.exibirMensagem("Atenção", "O nome do módulo não pode estar vazio.", null);
            }
        });
    }
}
