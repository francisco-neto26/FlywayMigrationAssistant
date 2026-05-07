package com.supergestao.Flyway.migration.assistant.ui.utilitario;

import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemErro;
import com.supergestao.Flyway.migration.assistant.exception.TelaException;
import com.supergestao.Flyway.migration.assistant.exception.ValidacaoException;
import com.supergestao.Flyway.migration.assistant.ui.controller.TelaConfirmacaoController;
import com.supergestao.Flyway.migration.assistant.ui.controller.TelaMensagemController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.file.Paths;

public class GerenciadorJanelas implements Mensageiro{
    @Override
    public void exibirMensagem(String titulo, String mensagem, String detalhes) {
        try {
            FXMLLoader loader = new FXMLLoader(GerenciadorJanelas.class.getResource("/com/supergestao/Flyway/migration/assistant/ui/controller/TelaMensagem.fxml"));
            Parent root = loader.load();

            TelaMensagemController telaMensagemController = loader.getController();
            telaMensagemController.exibirMensagem(titulo, mensagem, detalhes);

            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (IOException e) {
            throw new TelaException(MensagemErro.ERRO_ABERTURA_TELA.MensagemComParametro("Mensagem do Sistema"), e);
        } catch (Exception e) {
            throw new TelaException(MensagemErro.ERRO_GENERICO.MensagemComParametro("Mensagem do Sistema"), e);
        }
    }

    @Override
    public boolean pedidoConfirmacao(String titulo, String mensagem, String textoBotao) {
        try {
            FXMLLoader loader = new FXMLLoader(GerenciadorJanelas.class.getResource("/com/supergestao/Flyway/migration/assistant/ui/controller/TelaConfirmacao.fxml"));
            Parent root = loader.load();

            TelaConfirmacaoController telaConfirmacaoController = loader.getController();
            telaConfirmacaoController.carregarDados(titulo, mensagem, textoBotao);

            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            return telaConfirmacaoController.isConfirmado();

        } catch (IOException e) {
            throw new TelaException(MensagemErro.ERRO_ABERTURA_TELA.MensagemComParametro("Tela de Confirmação"), e);
        } catch (Exception e) {
            throw new TelaException(MensagemErro.ERRO_GENERICO.MensagemComParametro("Tela de Confirmação"), e);
        }
    }
}
