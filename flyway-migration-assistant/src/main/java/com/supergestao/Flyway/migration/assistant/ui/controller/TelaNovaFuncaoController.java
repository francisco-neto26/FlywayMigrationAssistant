package com.supergestao.Flyway.migration.assistant.ui.controller;

import com.supergestao.Flyway.migration.assistant.aplicacao.sincronizar.SincronizarModulos;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Modulo;
import com.supergestao.Flyway.migration.assistant.exception.TelaException;
import com.supergestao.Flyway.migration.assistant.persistencia.gerenciador.modulos.arquivos.GerenciadorModulosArquivos;
import com.supergestao.Flyway.migration.assistant.ui.estado.ContextoAplicacao;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.GerenciadorEstiloBotao;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.Mensageiro;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.CoresPadrao;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.nio.file.Paths;

public class TelaNovaFuncaoController {

    @FXML
    private ComboBox<Modulo> comboModulo;
    @FXML
    private TextField txtNomeFuncao;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnSalvarFuncao;
    @FXML
    private VBox painelRaiz;

    private String diretorioModulos;
    private GerenciadorModulosArquivos gerenciadorModulosArquivos;
    private Mensageiro mensageiro;
    private SincronizarModulos sincronizarModulos;

    public void setGerenciador(ContextoAplicacao contextoAplicacao) {
        this.diretorioModulos = contextoAplicacao.getDiretorioModulos();
        this.gerenciadorModulosArquivos = contextoAplicacao.getGerenciadorModulosArquivos();
        this.mensageiro = contextoAplicacao.getMensageiro();
        this.sincronizarModulos = contextoAplicacao.getSincronizarModulos();
        comboModulo.getItems().addAll(this.gerenciadorModulosArquivos.obterModuloOrigem(this.diretorioModulos).values());
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
    private void salvarFuncao() {
        String nomeFuncao = txtNomeFuncao.getText().trim();
        String nomeModulo = comboModulo.getValue().getNome();
        if (!nomeFuncao.isEmpty()) {
            String caminho = Paths.get(this.diretorioModulos, nomeModulo, nomeFuncao).toAbsolutePath().toString();
            boolean confirmacao = this.mensageiro.exibirDialogo("c",
                    "Nova Função",
                    null,
                    "Deseja criar a função '" + nomeFuncao + "' no diretorio '" + caminho + "'?",
                    CoresPadrao.AVISO
            );

            if (confirmacao) {
                try {
                    //this.gerenciadorModulosArquivos.salvarModuloFuncao(caminho);
                    this.sincronizarModulos.criarNovaFuncao(nomeModulo, nomeFuncao, this.diretorioModulos);
                    Stage stage = (Stage) btnSalvarFuncao.getScene().getWindow();
                    stage.close();

                } catch (TelaException e) {
                    this.mensageiro.exibirDialogo("m",
                            "Erro ao Salvar",
                            "Não foi possível criar a Função",
                            e.getMessage(),
                            CoresPadrao.ERRO);
                }
            }
        } else {
            this.mensageiro.exibirDialogo("m",
                    "Atenção",
                    null,
                    "O nome do módulo não pode estar vazio.",
                    CoresPadrao.AVISO
            );
        }
    }

}
