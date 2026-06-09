package com.supergestao.Flyway.migration.assistant.ui.controller;

import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemSistema;
import com.supergestao.Flyway.migration.assistant.ui.estado.ContextoAplicacao;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.*;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;


public class TelaPrincipalController implements ITelasModal {

    @FXML
    private TextField txtBuscarArquivo;
    @FXML
    private Button btnBuscar;
    @FXML
    private TreeView<String> treeArquivos;
    @FXML
    private Button btnIndentar;
    @FXML
    private Button btnValidarSql;
    @FXML
    private Button btnCancelarEdicao;
    @FXML
    private Button btnSalvarSql;
    @FXML
    private TextArea txtAreaSql;
    @FXML
    private TextArea txtAreaMensagens;
    @FXML
    private Button btnAtualizar;
    @FXML
    private Button btnNovoModulo;
    @FXML
    private Button btnNovaFuncao;
    @FXML
    private Button btnNovoMigration;
    @FXML
    private Button btnConfiguracoes;
    @FXML
    private BorderPane painelRaiz;

    private ContextoAplicacao contexto;

    public void setContextoAplicacao(ContextoAplicacao contextoAplicacao) {
        this.contexto = contextoAplicacao;
    }

    @FXML
    public void initialize() {
        buscaTempoReal();
        Platform.runLater(() -> {
            verifcaEcarregarModuloFuncao();
            GerenciadorEstiloBotao.gerenciadorEstiloBotao(painelRaiz);
            AtivaDesativaBotoesPrincipais();
        });
    }

    @FXML
    private void telaConfiguracao() {
        ConstrutorJanelas.abrirJanelaSecundaria(
                CaminhoTela.TELA_CONFIGURACOES,
                this.contexto
        );
        GerenciadorVisual.aplicarTemaGlobal(contexto.getTema());
        GerenciadorVisual.aplicarFonteGlobal(contexto.getChaveFonte());
        verifcaEcarregarModuloFuncao();
    }

    @FXML
    private void telaNovoModulo() {
        ConstrutorJanelas.abrirJanelaSecundaria(
                CaminhoTela.TELA_NOVO_MODULO,
                this.contexto
        );
        verifcaEcarregarModuloFuncao();
    }

    @FXML
    private void telaNovaFuncao() {
        ConstrutorJanelas.abrirJanelaSecundaria(
                CaminhoTela.TELA_NOVA_FUNCAO,
                this.contexto
        );
        verifcaEcarregarModuloFuncao();
    }

    @FXML
    private void telaNovoMigration() {
        ConstrutorJanelas.abrirJanelaSecundaria(
                CaminhoTela.TELA_NOVO_MIGRATION,
                this.contexto
        );
        verifcaEcarregarModuloFuncao();
    }

    @FXML
    private void indentar() {

    }

    @FXML
    private void validarSql() {

    }

    @FXML
    private void cancelarEdicao() {

    }

    @FXML
    private void salvarSql() {

    }

    private void buscaTempoReal() {
        PauseTransition atrasoBusca = new PauseTransition(Duration.millis(500));
        atrasoBusca.setOnFinished(event -> {
            buscarArquivo(txtBuscarArquivo.getText());
        });
        txtBuscarArquivo.textProperty().addListener((observable, valorAntigo, valorNovo) -> {
            atrasoBusca.playFromStart();
        });

        btnBuscar.setOnAction(event -> buscarArquivo(txtBuscarArquivo.getText()));
    }

    private void buscarArquivo(String nomeArquivo) {

    }

    private void AtivaDesativaBotoesPrincipais() {
        boolean existeDiretorioConfigurado = !this.contexto.getDiretoriosConfigurados();
        btnAtualizar.setDisable(existeDiretorioConfigurado);
        btnNovoModulo.setDisable(existeDiretorioConfigurado);
        btnNovaFuncao.setDisable(existeDiretorioConfigurado);
        btnNovoMigration.setDisable(existeDiretorioConfigurado);
        btnBuscar.setDisable(existeDiretorioConfigurado);
        txtBuscarArquivo.setDisable(existeDiretorioConfigurado);
    }

    @FXML
    private void verifcaEcarregarModuloFuncao() {
        if (!this.contexto.getDiretoriosConfigurados()) {
            boolean querConfigurar = this.contexto.exibirDialogo(TipoDialogo.CONFIRMACAO,
                    MensagemSistema.CADASTRAR_CONFIGURACAO.getMensagem(),
                    null,
                    MensagemSistema.IR_CONFIGURACAO.getMensagem());

            if (querConfigurar) {
                telaConfiguracao();
            }
        } else {
            GerenciadorArvoreModulos.buscarModulosFuncoes(this.contexto, treeArquivos);
        }
    }
}
