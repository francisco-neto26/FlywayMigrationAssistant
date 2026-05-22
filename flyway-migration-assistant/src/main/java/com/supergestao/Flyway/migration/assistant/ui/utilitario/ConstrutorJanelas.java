package com.supergestao.Flyway.migration.assistant.ui.utilitario;

import atlantafx.base.theme.Theme;
import com.supergestao.Flyway.migration.assistant.dominio.configuracao.GerenciadorConfiguracao;
import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemErro;
import com.supergestao.Flyway.migration.assistant.ui.controller.JanelaBaseController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.util.function.Consumer;

public class ConstrutorJanelas {

    public static <T> T abrirModal(CaminhoTela caminhoTela, String tituloTela, CoresPadrao corBarra, Consumer<T> configuradorController) {
        try {
            // Carrega as telas
            FXMLLoader baseLoader = carregarFxml(CaminhoTela.JANELA_BASE);
            FXMLLoader conteudoLoader = carregarFxml(caminhoTela);

            //Extrai os controllers
            JanelaBaseController baseController = baseLoader.getController();
            T controller = conteudoLoader.getController();

            //Monta as telas
            baseController.setConteudo(conteudoLoader.getRoot(), tituloTela);
            baseController.defineVisibilidadeMaxMin();

            if (corBarra != null) {
                baseController.defineCorBarra(corBarra);
            }

            //Injeta os dados de contextoAplicação no controller do formulário
            if (configuradorController != null) {
                configuradorController.accept(controller);
            }

            //Exibe
            Stage stage = new Stage();
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle(tituloTela);
            stage.setScene(new Scene(baseLoader.getRoot()));
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.showAndWait();

            // Retorna o controller (útil para pegar se o usuário clicou em 'Sim' ou 'Não' na confirmação)
            return controller;

        } catch (Exception e) {
            throw new RuntimeException(MensagemErro.ERRO_ABERTURA_TELA.MensagemComParametro("Tela modal") + tituloTela, e);
        }
    }

    public static void abrirJanelaPrincipal(Stage primaryStage, CaminhoTela caminhoTela, String tituloTela) {
        try {
            //Carrega tema salvo
            Theme temaSalvo = GerenciadorConfiguracao.getTema();
            Application.setUserAgentStylesheet(temaSalvo.getUserAgentStylesheet());

            // Carrega as telas
            FXMLLoader baseLoader = carregarFxml(CaminhoTela.JANELA_BASE);
            FXMLLoader conteudoLoader = carregarFxml(caminhoTela);

            //Extrai os controllers
            JanelaBaseController baseController = baseLoader.getController();

            //Monta as telas
            baseController.setConteudo(conteudoLoader.getRoot(), tituloTela);

            //define cor janela
            baseController.defineCorBarra(CoresPadrao.BARRA_PRINCIPAL);

            primaryStage.initStyle(StageStyle.UNDECORATED);
            primaryStage.setTitle(tituloTela);
            primaryStage.setScene(new Scene(baseLoader.getRoot(), 1000, 700));

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

    private static FXMLLoader carregarFxml(CaminhoTela tela) throws Exception {
        FXMLLoader loader = new FXMLLoader(ConstrutorJanelas.class.getResource(tela.getCaminho()));
        loader.load();
        return loader;
    }

}

