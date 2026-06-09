package com.supergestao.Flyway.migration.assistant.ui.controller;

import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemSistema;
import com.supergestao.Flyway.migration.assistant.exception.TelaException;
import com.supergestao.Flyway.migration.assistant.ui.estado.ContextoAplicacao;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.GerenciadorEstiloBotao;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.TipoDialogo;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
    @FXML
    private Label lblNomeModulo;

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
            String caminho = this.contexto.getDiretorioArquivo();
            boolean confirmacao = this.contexto.exibirDialogo(TipoDialogo.CONFIRMACAO,
                    MensagemSistema.NOVO_MODULO.getMensagem(),
                    null,
                    MensagemSistema.CRIAR_MODULO.MensagemComParametro(nomeModulo, caminho)
            );

            if (confirmacao) {
                try {
                    this.contexto.criarModuloFuncao(nomeModulo, null, caminho);
                    Stage stage = (Stage) btnSalvarModulo.getScene().getWindow();
                    stage.close();

                } catch (TelaException e) {
                    this.contexto.exibirDialogo(TipoDialogo.ERRO,
                            MensagemSistema.ERRO_SALVAR_REGISTRO.getMensagem(),
                            MensagemSistema.ERRO_SALVAR_MODULO.MensagemComParametro(nomeModulo),
                            e.getMessage()
                    );
                }
            }
        } else {
            this.contexto.exibirDialogo(TipoDialogo.ALERTA,
                    MensagemSistema.ALERTA.getMensagem(),
                    null,
                    MensagemSistema.CAMPO_OBRIGATORIO.MensagemComParametro(lblNomeModulo.getText())
            );
        }
    }

}
