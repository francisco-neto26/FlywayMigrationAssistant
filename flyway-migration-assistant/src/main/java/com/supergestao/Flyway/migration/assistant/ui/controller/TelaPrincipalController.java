package com.supergestao.Flyway.migration.assistant.ui.controller;

import com.supergestao.Flyway.migration.assistant.dominio.configuracao.GerenciadorConfiguracao;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Arquivo;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Funcao;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Modulo;
import com.supergestao.Flyway.migration.assistant.persistencia.gerenciador.modulos.arquivos.GerenciadorModulosArquivosDisco;
import com.supergestao.Flyway.migration.assistant.ui.estado.ContextoAplicacao;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.*;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;


import java.util.Map;

public class TelaPrincipalController {

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

    private ContextoAplicacao contexto;

    @FXML
    public void initialize() {

        atualizarContexto();
        //melhorar a chamada dos botoes, ta ficando extensa aqui, criar metodo
        GerenciadorEstiloBotao.BotaoPadrao(btnAtualizar);
        btnAtualizar.setOnAction(event -> verifcaEcarregarModuloFuncao());

        GerenciadorEstiloBotao.BotaoPadrao(btnNovoModulo);
        btnNovoModulo.setOnAction(event -> telaNovoModulo());

        GerenciadorEstiloBotao.BotaoPadrao(btnNovaFuncao);
        btnNovaFuncao.setOnAction(event -> telaNovaFuncao());

        GerenciadorEstiloBotao.BotaoPadrao(btnNovoMigration);
        btnNovoMigration.setOnAction(event -> telaNovoMigration());

        GerenciadorEstiloBotao.BotaoPadrao(btnConfiguracoes);
        btnConfiguracoes.setOnAction(event -> telaConfiguracao());
        buscaTempoReal();
        AtivaDesativaBotoesPrincipais();
        Platform.runLater(() -> {
            verifcaEcarregarModuloFuncao();
        });
    }

    private void atualizarContexto() {
        this.contexto = new ContextoAplicacao(
                GerenciadorConfiguracao.getDiretorioModulo(),
                GerenciadorConfiguracao.getDiretorioArquivo(),
                GerenciadorConfiguracao.getTema().getName(),
                GerenciadorConfiguracao.getListaTema(),
                new GerenciadorModulosArquivosDisco(),
                new GerenciadorJanelas());
    }

    private void telaConfiguracao() {
        ConstrutorJanelas.abrirModal(
                CaminhoTela.TELA_CONFIGURACOES,
                CaminhoTela.TELA_CONFIGURACOES.getNome(),
                CoresPadrao.BARRA_PRINCIPAL,
                (TelaConfiguracoesController controller) -> {
                    controller.setGerenciador(this.contexto);
                }
        );
        atualizarContexto();
        verifcaEcarregarModuloFuncao();
    }

    private void telaNovoModulo() {
        ConstrutorJanelas.abrirModal(
                CaminhoTela.TELA_NOVO_MODULO,
                CaminhoTela.TELA_NOVO_MODULO.getNome(),
                CoresPadrao.BARRA_PRINCIPAL,
                (TelaNovoModuloController controller) -> {
                    controller.setGerenciador(this.contexto);
                }
        );
        verifcaEcarregarModuloFuncao();
    }

    private void telaNovaFuncao() {
        ConstrutorJanelas.abrirModal(
                CaminhoTela.TELA_NOVA_FUNCAO,
                CaminhoTela.TELA_NOVA_FUNCAO.getNome(),
                CoresPadrao.BARRA_PRINCIPAL,
                (TelaNovaFuncaoController controller) -> {
                    controller.setGerenciador(this.contexto);
                }
        );
        verifcaEcarregarModuloFuncao();
    }

    private void telaNovoMigration() {
        ConstrutorJanelas.abrirModal(
                CaminhoTela.TELA_NOVO_MIGRATION,
                CaminhoTela.TELA_NOVO_MIGRATION.getNome(),
                CoresPadrao.BARRA_PRINCIPAL,
                (TelaNovoMigrationController controller) -> {
                    controller.setGerenciador(this.contexto);
                }
        );
        verifcaEcarregarModuloFuncao();
    }

    private void buscaTempoReal(){
        GerenciadorEstiloBotao.BotaoPadrao(btnBuscar);
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


    private boolean existeDiretorioConfigurado() {
        return this.contexto.getDiretorioModulos() != null && !this.contexto.getDiretorioModulos().isEmpty();
    }

    private void AtivaDesativaBotoesPrincipais() {
        boolean existeDiretorioConfigurado = !existeDiretorioConfigurado();
        btnAtualizar.setDisable(existeDiretorioConfigurado);
        btnNovoModulo.setDisable(existeDiretorioConfigurado);
        btnNovaFuncao.setDisable(existeDiretorioConfigurado);
        btnNovoMigration.setDisable(existeDiretorioConfigurado);
        btnBuscar.setDisable(existeDiretorioConfigurado);
        txtBuscarArquivo.setDisable(existeDiretorioConfigurado);
    }

    private void verifcaEcarregarModuloFuncao() {
        if (!existeDiretorioConfigurado()) {

            boolean querConfigurar = this.contexto.getMensageiro().exibirDialogo("c",
                    "Cadastrar configurações",
                    null,
                    "O diretório raiz dos Módulos não está configurado. Deseja configurar agora?",
                    CoresPadrao.INFO);

            if (querConfigurar) {
                telaConfiguracao();
            }

        } else {
            desenharArvore();
        }
    }

    private void desenharArvore() {
        try {
            if (!existeDiretorioConfigurado()) {
                return;
            }
            TreeItem<String> raizVisual = new TreeItem<>("Módulos (Raiz)");
            raizVisual.setExpanded(true);

            Map<String, Modulo> mapaModulos = this.contexto.getGerenciadorModulosArquivos()
                    .obterModuloOrigem(this.contexto.getDiretorioModulos());

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
            this.contexto.getMensageiro().exibirDialogo("mensagem",
                    "Erro de Leitura",
                    "Falha ao ler os módulos",
                    e.getMessage(),
                    CoresPadrao.ERRO);
        }
    }
}
