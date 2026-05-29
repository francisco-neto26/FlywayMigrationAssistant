package com.supergestao.Flyway.migration.assistant.ui.controller;

import com.supergestao.Flyway.migration.assistant.aplicacao.sincronizar.SincronizarModulos;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Modulo;
import com.supergestao.Flyway.migration.assistant.exception.TelaException;
import com.supergestao.Flyway.migration.assistant.persistencia.gerenciador.modulos.arquivos.IGerenciadorModulosArquivosDisco;
import com.supergestao.Flyway.migration.assistant.ui.estado.ContextoAplicacao;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.GerenciadorEstiloBotao;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.IGerenciadorJanelas;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.CoresPadrao;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.nio.file.Paths;

public class TelaNovaFuncaoController implements ITelasModal{

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

    private ContextoAplicacao contexto;

    public void setContextoAplicacao(ContextoAplicacao contextoAplicacao) {
        this.contexto = contextoAplicacao;
    }

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            comboModulo.getItems().addAll(this.contexto.getIGerenciadorModulosArquivosDisco().obterModuloOrigem(this.contexto.getIGerenciadorConfiguracao().getDiretorioModulo()).values());
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
            String caminho = Paths.get(this.contexto.getIGerenciadorConfiguracao().getDiretorioArquivo(), nomeModulo, nomeFuncao).toAbsolutePath().toString();
            boolean confirmacao = this.contexto.getIGerenciadorJanelas().exibirDialogo("c",
                    "Nova Função",
                    null,
                    "Deseja criar a função '" + nomeFuncao + "' no diretorio '" + caminho + "'?",
                    CoresPadrao.AVISO
            );

            if (confirmacao) {
                try {
                    this.contexto.getIGerenciadorModulosArquivosDisco().salvarModuloFuncao(caminho);
                    Stage stage = (Stage) btnSalvarFuncao.getScene().getWindow();
                    stage.close();

                } catch (TelaException e) {
                    this.contexto.getIGerenciadorJanelas().exibirDialogo("m",
                            "Erro ao Salvar",
                            "Não foi possível criar a Função",
                            e.getMessage(),
                            CoresPadrao.ERRO);
                }
            }
        } else {
            this.contexto.getIGerenciadorJanelas().exibirDialogo("m",
                    "Atenção",
                    null,
                    "O nome do módulo não pode estar vazio.",
                    CoresPadrao.AVISO
            );
        }
    }

}
