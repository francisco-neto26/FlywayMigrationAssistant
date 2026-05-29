package com.supergestao.Flyway.migration.assistant.ui.controller;

import com.supergestao.Flyway.migration.assistant.dominio.modelo.Arquivo;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Funcao;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Modulo;
import com.supergestao.Flyway.migration.assistant.ui.estado.ContextoAplicacao;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.*;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;


import java.util.Map;

public class TelaPrincipalController implements ITelasModal{

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
/*tem que implementar o itelasmodel pelas demais classes do controller, melhora a chamada das telas do modal, sem ter que passar o titulo, ja que temos elas
    caminho das telas, criar botão de cores para salvar as cores do sistema como um todo, vale lembrar que com as mudancas tem validar tudo do zero, ou seja, estamos
    no inicio novamente. Ideal antes de evoluir na tela principal é fazer com que tudo o resto funcione bem
 */
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
        ConstrutorJanelas.abrirModal(
                CaminhoTela.TELA_CONFIGURACOES,
                CaminhoTela.TELA_CONFIGURACOES.getNome(),
                CoresPadrao.BARRA_PRINCIPAL,
                this.contexto
        );
        GerenciadorVisual.aplicarTemaGlobal(contexto.getIGerenciadorConfiguracao().getTema());
        GerenciadorVisual.aplicarFonteGlobal(contexto.getIGerenciadorConfiguracao().getChaveFonte());
        verifcaEcarregarModuloFuncao();
    }

    @FXML
    private void telaNovoModulo() {
        ConstrutorJanelas.abrirModal(
                CaminhoTela.TELA_NOVO_MODULO,
                CaminhoTela.TELA_NOVO_MODULO.getNome(),
                CoresPadrao.BARRA_PRINCIPAL,
                this.contexto
        );
        verifcaEcarregarModuloFuncao();
    }

    @FXML
    private void telaNovaFuncao() {
        ConstrutorJanelas.abrirModal(
                CaminhoTela.TELA_NOVA_FUNCAO,
                CaminhoTela.TELA_NOVA_FUNCAO.getNome(),
                CoresPadrao.BARRA_PRINCIPAL,
                this.contexto
        );
        verifcaEcarregarModuloFuncao();
    }

    @FXML
    private void telaNovoMigration() {
        ConstrutorJanelas.abrirModal(
                CaminhoTela.TELA_NOVO_MIGRATION,
                CaminhoTela.TELA_NOVO_MIGRATION.getNome(),
                CoresPadrao.BARRA_PRINCIPAL,
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
        boolean existeDiretorioConfigurado = !this.contexto.getIGerenciadorConfiguracao().diretoriosConfigurados();
        btnAtualizar.setDisable(existeDiretorioConfigurado);
        btnNovoModulo.setDisable(existeDiretorioConfigurado);
        btnNovaFuncao.setDisable(existeDiretorioConfigurado);
        btnNovoMigration.setDisable(existeDiretorioConfigurado);
        btnBuscar.setDisable(existeDiretorioConfigurado);
        txtBuscarArquivo.setDisable(existeDiretorioConfigurado);
    }

    @FXML
    private void verifcaEcarregarModuloFuncao() {
        if (!this.contexto.getIGerenciadorConfiguracao().diretoriosConfigurados()) {

            boolean querConfigurar = this.contexto.getIGerenciadorJanelas().exibirDialogo(TipoDialogo.CONFIRMACAO,
                    "Cadastrar configurações",
                    null,
                    "O diretório raiz dos Módulos não está configurado. Deseja configurar agora?");

            if (querConfigurar) {
                telaConfiguracao();
            }
        } else {
            desenharArvore();
        }
    }


    private void desenharArvore() {
        try {

            TreeItem<String> raizVisual = new TreeItem<>("Módulos (Raiz)");
            raizVisual.setExpanded(true);

            Map<String, Modulo> mapaModulos = this.contexto.getIGerenciadorModulosArquivos()
                    .obterModuloOrigem(this.contexto.getIGerenciadorConfiguracao().getDiretorioModulo());

            for (Modulo modulo : mapaModulos.values()) {
                TreeItem<String> itemModulo = new TreeItem<>(modulo.getNome());

                // NOTA: Troque getFuncoes() pelo nome exato do método da sua classe Modulo
                for (Funcao funcao : modulo.getFuncoes()) {
                    TreeItem<String> itemFuncao = new TreeItem<>("📂 " + funcao.getNome());

                    // NOTA: Troque getArquivos() pelo nome exato do método da sua classe Funcao
                    for (Arquivo arquivo : funcao.getArquivos()) {
                        TreeItem<String> itemArquivo = new TreeItem<>("📄 " + arquivo.getNome());
                        itemFuncao.getChildren().add(itemArquivo);
                    }
                    itemModulo.getChildren().add(itemFuncao);
                }
                raizVisual.getChildren().add(itemModulo);
            }

            treeArquivos.setRoot(raizVisual);

        } catch (Exception e) {
            this.contexto.getIGerenciadorJanelas().exibirDialogo(TipoDialogo.ERRO,
                    "Erro de Leitura",
                    "Falha ao ler os módulos",
                    e.getMessage());
        }
    }
}
