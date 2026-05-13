package com.supergestao.Flyway.migration.assistant.ui.controller;

import com.supergestao.Flyway.migration.assistant.dominio.configuracao.GerenciadorConfiguracao;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Arquivo;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Funcao;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Modulo;
import com.supergestao.Flyway.migration.assistant.exception.TelaException;
import com.supergestao.Flyway.migration.assistant.persistencia.gerenciador.modulos.arquivos.GerenciadorModulosArquivosDisco;
import com.supergestao.Flyway.migration.assistant.ui.estado.ContextoAplicacao;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.GerenciadorJanelas;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.TipoMensagem;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

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

        //Tratar a visibilidade dos botoes, ao desabilitar quando não existe configuração passou a dar erro na classe GerenciadorModulosArquivosDisco.obterModulosFuncoes
        btnAtualizar.setOnAction(event -> verifcaEcarregarModuloFuncao());
        btnNovoModulo.setOnAction(event -> telaNovoModulo());
        btnNovaFuncao.setOnAction(event -> telaNovaFuncao());
        btnNovoMigration.setOnAction(event -> telaNovoMigration());
        btnConfiguracoes.setOnAction(event -> telaConfiguracao());
        AtivaDesativaBotoesPrincipais();
        verifcaEcarregarModuloFuncao();
    }

    private void atualizarContexto() {
        this.contexto = new ContextoAplicacao(
                GerenciadorConfiguracao.getDiretorioModulo(),
                GerenciadorConfiguracao.getDiretorioArquivo(),
                GerenciadorConfiguracao.getTema().getName(),
                GerenciadorConfiguracao.getListaTema(),
                new GerenciadorModulosArquivosDisco(),
                new GerenciadorJanelas()
        );
    }

    private void telaConfiguracao() {
        abrirTelaModal("TelaConfiguracoes.fxml", "Configurações");
        atualizarContexto();
        verifcaEcarregarModuloFuncao();
    }

    private void telaNovoModulo() {
        abrirTelaModal("TelaNovoModulo.fxml", "Novo Módulo");
        verifcaEcarregarModuloFuncao();
    }

    private void telaNovaFuncao() {
        abrirTelaModal("TelaNovaFuncao.fxml", "Nova Função");
        verifcaEcarregarModuloFuncao();
    }

    private void telaNovoMigration() {
        abrirTelaModal("TelaNovoMigration.fxml", "Novo Migration");
        verifcaEcarregarModuloFuncao();
    }


    private void abrirTelaModal(String fxmlPath, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            Object controller = loader.getController();

            switch (controller) {
                case TelaConfiguracoesController c -> c.setGerenciador(this.contexto);
                case TelaNovoModuloController c -> c.setGerenciador(this.contexto);
                case TelaNovaFuncaoController c -> c.setGerenciador(this.contexto);
                case TelaNovoMigrationController c -> c.setGerenciador(this.contexto);
                default -> { }
            }

            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (TelaException e) {
            this.contexto.getMensageiro().exibirMensagem("Erro Crítico", "Falha ao abrir a tela " + titulo, e.getMessage());
        } catch (Exception e) {
            this.contexto.getMensageiro().exibirMensagem("Erro Crítico", "Falha ao abrir a tela1 " + titulo, e.getMessage(), TipoMensagem.ERRO);
        }
    }

    private boolean existeDiretorioConfigurado(){
        return this.contexto.getDiretorioModulos() != null && !this.contexto.getDiretorioModulos().isEmpty();
    }

    private void AtivaDesativaBotoesPrincipais(){
        if (!existeDiretorioConfigurado()){
            btnAtualizar.setDisable(true);
            btnNovoModulo.setDisable(true);
            btnNovaFuncao.setDisable(true);
            btnNovoMigration.setDisable(true);
        }else{
            btnAtualizar.setDisable(false);
            btnNovoModulo.setDisable(false);
            btnNovaFuncao.setDisable(false);
            btnNovoMigration.setDisable(false);
        }
    }

    private void verifcaEcarregarModuloFuncao() {
        if (existeDiretorioConfigurado()) {

            boolean querConfigurar = this.contexto.getMensageiro().pedidoConfirmacao(
                    "Atenção",
                    "O diretório raiz dos Módulos não está configurado. Deseja configurar agora?",
                    "Sim, Configurar"
            );

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
            raizVisual.setExpanded(true); // Nasce aberta

            // Chama o seu serviço de disco para buscar tudo
            Map<String, Modulo> mapaModulos = this.contexto.getGerenciadorModulosArquivos().obterModulosFuncoes(this.contexto.getDiretorioModulos());

            for (Modulo modulo : mapaModulos.values()) {
                TreeItem<String> itemModulo = new TreeItem<>("📁 " + modulo.getNome());

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
            this.contexto.getMensageiro().exibirMensagem("Erro de Leitura", "Falha ao ler os módulos", e.getMessage());
        }
    }
}
