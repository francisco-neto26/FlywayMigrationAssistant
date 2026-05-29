package com.supergestao.Flyway.migration.assistant.ui.utilitario;

import atlantafx.base.theme.Theme;
import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemErro;
import com.supergestao.Flyway.migration.assistant.ui.controller.ITelasModal;
import com.supergestao.Flyway.migration.assistant.ui.controller.JanelaBaseController;
import com.supergestao.Flyway.migration.assistant.ui.estado.ContextoAplicacao;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.util.function.Consumer;

public class ConstrutorJanelas {

    public static <T> T abrirModal(CaminhoTela caminhoTela, String tituloTela, CoresPadrao corBarra, ContextoAplicacao contextoAplicacao) {
        try {
            // Carrega as telas
            FXMLLoader janelaBaseLoader = carregarFxml(CaminhoTela.JANELA_BASE);
            FXMLLoader conteudoTelaLoader = carregarFxml(caminhoTela);

            //Extrai os controllers
            JanelaBaseController janelaBaseController = janelaBaseLoader.getController();
            T controller = conteudoTelaLoader.getController();

            // INJEÇÃO 1: Injeta na Tela do Miolo (Ex: TelaConfiguracoes)
            if (controller instanceof ITelasModal) {
                ((ITelasModal) controller).setContextoAplicacao(contextoAplicacao);
            }

            // INJEÇÃO 2: Injeta na Janela Base (A casca)
            if (janelaBaseController != null) {
                ((ITelasModal) janelaBaseController).setContextoAplicacao(contextoAplicacao);
            }

            //Monta as telas
            janelaBaseController.setConteudo(conteudoTelaLoader.getRoot(), tituloTela);
            janelaBaseController.defineVisibilidadeMaxMin();

            if (corBarra != null) {
                janelaBaseController.defineCorBarra(corBarra);
            }

            //Exibe
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle(tituloTela);
            stage.setScene(new Scene(janelaBaseLoader.getRoot()));
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.showAndWait();

            // Retorna o controller
            return controller;

        } catch (Exception e) {
            throw new RuntimeException(MensagemErro.ERRO_ABERTURA_TELA.MensagemComParametro("Tela modal") + tituloTela, e);
        }
    }

    public static void abrirJanelaPrincipal(Stage primaryStage, CaminhoTela caminhoTela, String tituloTela, ContextoAplicacao contextoAplicacao) {
        try {
            //Carrega tema salvo
            Theme temaSalvo = contextoAplicacao.getIGerenciadorConfiguracao().getTema();
            Application.setUserAgentStylesheet(temaSalvo.getUserAgentStylesheet());

            // Carrega as telas
            FXMLLoader janelaBaseLoader = carregarFxml(CaminhoTela.JANELA_BASE);
            FXMLLoader conteudoTelaLoader = carregarFxml(caminhoTela);

            //Extrai os controllers (Corrigido o tipo do controller)
            JanelaBaseController janelaBaseController = janelaBaseLoader.getController();
            Object controller = conteudoTelaLoader.getController();

            // INJEÇÃO 1: Injeta na Tela do Miolo (A TelaPrincipalController)
            if (controller instanceof ITelasModal) {
                ((ITelasModal) controller).setContextoAplicacao(contextoAplicacao);
            }

            // INJEÇÃO 2: Injeta na Janela Base (A casca)
            if (janelaBaseController instanceof ITelasModal) {
                ((ITelasModal) janelaBaseController).setContextoAplicacao(contextoAplicacao);
            }

            //Monta as telas
            janelaBaseController.setConteudo(conteudoTelaLoader.getRoot(), tituloTela);

            //define cor janela
            janelaBaseController.defineCorBarra(CoresPadrao.BARRA_PRINCIPAL);

            primaryStage.initStyle(StageStyle.UNDECORATED);
            primaryStage.setTitle(tituloTela);
            primaryStage.setScene(new Scene(janelaBaseLoader.getRoot(), 1000, 700));

            // Ajuste do bounds (Aquele ajuste para o Windows não engolir a barra de tarefas)
            javafx.geometry.Rectangle2D limites = javafx.stage.Screen.getPrimary().getVisualBounds();
            primaryStage.setX(limites.getMinX());
            primaryStage.setY(limites.getMinY());
            primaryStage.setWidth(limites.getWidth());
            primaryStage.setHeight(limites.getHeight());

        } catch (Exception e) {
            throw new RuntimeException(MensagemErro.ERRO_ABERTURA_TELA.MensagemComParametro("Tela Principal") + tituloTela, e);
        }
    }

    private static <T> T montarJanela(Stage stage, CaminhoTela caminhoTela, String tituloTela, CoresPadrao corBarra, ContextoAplicacao contextoAplicacao, boolean exibirBotoesMaxMin) throws Exception {

        // 1. Carrega FXMLs
        FXMLLoader janelaBaseLoader = carregarFxml(CaminhoTela.JANELA_BASE);
        FXMLLoader conteudoTelaLoader = carregarFxml(caminhoTela);

        // 2. Extrai Controllers
        JanelaBaseController janelaBaseController = janelaBaseLoader.getController();
        T controllerMiolo = conteudoTelaLoader.getController();

        // 3. Injeção de Dependências Automática
        if (controllerMiolo instanceof ITelasModal) {
            ((ITelasModal) controllerMiolo).setContextoAplicacao(contextoAplicacao);
        }
        if (janelaBaseController != null) {
            ((ITelasModal) janelaBaseController).setContextoAplicacao(contextoAplicacao);
        }
        // 4. Monta o visual
        assert janelaBaseController != null;
        janelaBaseController.setConteudo(conteudoTelaLoader.getRoot(), tituloTela);

        if (corBarra != null) {
            janelaBaseController.defineCorBarra(corBarra);
        }
        if (!exibirBotoesMaxMin) {
            janelaBaseController.defineVisibilidadeMaxMin();
        }
        // 5. Configura o Stage
        stage.initStyle(StageStyle.UNDECORATED);
        if (stage.getScene() == null) {
            stage.setScene(new Scene(janelaBaseLoader.getRoot()));
        }
        return controllerMiolo;
    }

    private static FXMLLoader carregarFxml(CaminhoTela tela) throws Exception {
        FXMLLoader loader = new FXMLLoader(ConstrutorJanelas.class.getResource(tela.getCaminho()));
        loader.load();
        return loader;
    }

}

