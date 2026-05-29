package com.supergestao.Flyway.migration.assistant.ui.controller;

import com.supergestao.Flyway.migration.assistant.exception.TelaException;
import com.supergestao.Flyway.migration.assistant.persistencia.gerenciador.modulos.arquivos.IGerenciadorModulosArquivosDisco;
import com.supergestao.Flyway.migration.assistant.ui.estado.ContextoAplicacao;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.GerenciadorEstiloBotao;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.IGerenciadorJanelas;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.CoresPadrao;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.TipoDialogo;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.nio.file.Paths;

public class TelaNovoModuloController implements ITelasModal{
    @FXML
    private TextField txtNomeModulo;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnSalvarModulo;
    @FXML
    private VBox painelRaiz;

    private ContextoAplicacao contexto;

    public void setContextoAplicacao(ContextoAplicacao contextoAplicacao) {
        this.contexto = contextoAplicacao;
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
            String caminho = Paths.get(this.contexto.getIGerenciadorConfiguracao().getDiretorioModulo(), nomeModulo).toAbsolutePath().toString();
            boolean confirmacao = this.contexto.getIGerenciadorJanelas().exibirDialogo(TipoDialogo.CONFIRMACAO,
                    "Novo Módulo",
                    null,
                    "Deseja criar o módulo '" + nomeModulo + "' no diretorio '" + caminho + "'?"
            );

            if (confirmacao) {
                try {
                    this.contexto.getIGerenciadorModulosArquivosDisco().salvarModuloFuncao(caminho);
                    Stage stage = (Stage) btnSalvarModulo.getScene().getWindow();
                    stage.close();

                } catch (TelaException e) {
                    this.contexto.getIGerenciadorJanelas().exibirDialogo(TipoDialogo.ERRO,
                            "Erro ao Salvar",
                            "Não foi possível criar o módulo",
                            e.getMessage()
                    );
                }
            }
        } else {
            this.contexto.getIGerenciadorJanelas().exibirDialogo(TipoDialogo.ALERTA,
                    "Atenção", null,
                    "O nome do módulo não pode estar vazio."
            );
        }
    }

}
