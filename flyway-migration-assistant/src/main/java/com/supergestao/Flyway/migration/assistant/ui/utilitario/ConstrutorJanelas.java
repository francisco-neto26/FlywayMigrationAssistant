package com.supergestao.Flyway.migration.assistant.ui.utilitario;

import atlantafx.base.theme.Theme;
import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemSistema;
import com.supergestao.Flyway.migration.assistant.ui.controller.ITelasModal;
import com.supergestao.Flyway.migration.assistant.ui.controller.JanelaBaseController;
import com.supergestao.Flyway.migration.assistant.ui.controller.TelaDialogoController;
import com.supergestao.Flyway.migration.assistant.ui.estado.ContextoAplicacao;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class ConstrutorJanelas {

    private static Stage stageJanelaPrincipal;

    public static void abrirJanelaPrincipal(Stage primaryStage, CaminhoTela caminhoTela, ContextoAplicacao contextoAplicacao) {
        try {
            stageJanelaPrincipal = primaryStage;
            Stage stage = ajustarMonitor(primaryStage, "principal");
            Theme temaSalvo = contextoAplicacao.getIGerenciadorConfiguracao().getTema();
            Application.setUserAgentStylesheet(temaSalvo.getUserAgentStylesheet());

            CoresPadrao corBarra = defineCor(0);
            String tituloTela = caminhoTela.getNome();
            // Monta a base visual e injeta dependências
            montarJanela(stage, caminhoTela, tituloTela, corBarra, contextoAplicacao, true);

        } catch (Exception e) {
            throw new RuntimeException(MensagemSistema.ERRO_ABERTURA_TELA.MensagemComParametro("Tela Principal") + caminhoTela.getNome(), e);
        }
    }

    public static <T> void abrirJanelaSecundaria(CaminhoTela caminhoTela, ContextoAplicacao contextoAplicacao) {
        try {
            Stage stage = ajustarMonitor(null, "auxiliar");

            CoresPadrao corBarra = defineCor(0);
            String tituloTela = caminhoTela.getNome();

            T controller = montarJanela(stage, caminhoTela, tituloTela, corBarra, contextoAplicacao, false);
            stage.showAndWait();

        } catch (Exception e) {
            throw new RuntimeException(MensagemSistema.ERRO_ABERTURA_TELA.MensagemComParametro("Tela Auxiliar") + caminhoTela.getNome(), e);
        }
    }

    public static <T> T abrirJanelaDialogo(TipoDialogo tipoDialogo, CaminhoTela caminhoTela, String tituloTela, String mensagem, String detalhes, ContextoAplicacao contextoAplicacao) {
        try {
            Stage stage = ajustarMonitor(null, "dialogo");

            CoresPadrao corBarra = defineCor(tipoDialogo.getTipoDialogo());

            T controller = montarJanela(stage, caminhoTela, tituloTela, corBarra, contextoAplicacao, false);

            if (controller instanceof TelaDialogoController) {
                ((TelaDialogoController) controller).telaMensagem(tipoDialogo, mensagem, detalhes);
            }

            stage.showAndWait();

            return controller;

        } catch (Exception e) {
            throw new RuntimeException(MensagemSistema.ERRO_ABERTURA_TELA.MensagemComParametro("Tela Diálogo ") + tipoDialogo.getNome(), e);
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

    public static CoresPadrao defineCor(int origem) {
        return switch (origem) {
            case 1 -> CoresPadrao.SUCESSO;
            case 2 -> CoresPadrao.AVISO;
            case 3 -> CoresPadrao.INFO;
            case 4 -> CoresPadrao.ERRO;
            default -> CoresPadrao.BARRA_PRINCIPAL;
        };
    }

    public static Stage ajustarMonitor(Stage stage, String tipoJanela) {

        if (stage == null) {
            stage = new Stage();
        }

        stage.initStyle(StageStyle.UNDECORATED);


        if (tipoJanela.equalsIgnoreCase("principal")) {

            javafx.geometry.Rectangle2D limites = javafx.stage.Screen.getPrimary().getVisualBounds();
            stage.setX(limites.getMinX());
            stage.setY(limites.getMinY());
            stage.setWidth(limites.getWidth());
            stage.setHeight(limites.getHeight());

        } else {

            if (stageJanelaPrincipal != null) {
                stage.initOwner(stageJanelaPrincipal);
                if (tipoJanela.equalsIgnoreCase("dialogo")) {
                    stage.initModality(Modality.APPLICATION_MODAL);
                }
                Stage finalStage = stage;
                stage.setOnShown(event -> {
                    finalStage.setX(stageJanelaPrincipal.getX() + (stageJanelaPrincipal.getWidth() - finalStage.getWidth()) / 2);
                    finalStage.setY(stageJanelaPrincipal.getY() + (stageJanelaPrincipal.getHeight() - finalStage.getHeight()) / 2);
                });
            }
        }
        return stage;
    }

}



