package com.supergestao.Flyway.migration.assistant.ui.utilitario;

import javafx.scene.Node;
import javafx.scene.control.Button;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GerenciadorEstiloBotao {

    public static void gerenciadorEstiloBotao(Node painel) {
        carregaPadraoBotoes(buscaListaBotes(painel));
    }

    private static List<Button> buscaListaBotes(Node painel) {
        List<Button> listaDeBotoes = new ArrayList<>();
        Set<Node> todosBotoes = painel.lookupAll(".button");
        for (Node node : todosBotoes) {
            if (node instanceof Button) {
                listaDeBotoes.add((Button) node);
            }
        }
        return listaDeBotoes;
    }

    private static void carregaPadraoBotoes(List<Button> listaDeBotoes) {
        for (Button botao : listaDeBotoes) {

            if (botao.getId().toLowerCase().contains("salvar") || botao.getId().toLowerCase().contains("confirmar")) {

                botaoConfirmar(botao);
                continue;

            }
            if (botao.getId().toLowerCase().contains("cancelar") || botao.getId().toLowerCase().contains("sair")) {

                botaoCancelar(botao);
                continue;

            }
            if (botao.getId().equalsIgnoreCase("btnMinimizar")
                    || botao.getId().equalsIgnoreCase("btnMaximizar")
                    || botao.getId().equalsIgnoreCase("btnFechar")) {

                botaoControleTela(botao);
                continue;

            }
            GerenciadorEstiloBotao.botaoPadrao(botao);
        }
    }

    public static void botaoConfirmar(Button btnConfirmar) {

        String confirmarNormal = "-fx-background-color:" + CoresPadrao.BOTAO_CONFIRMAR.getCorTipoMensagem() + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;";
        String confirmarHover = "-fx-background-color: " + CoresPadrao.BOTAO_CONFIRMAR_HOVER.getCorTipoMensagem() + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;";

        aplicaPadraoTamanhoBotao(btnConfirmar);

        btnConfirmar.setStyle(confirmarNormal);
        btnConfirmar.setOnMouseEntered(e -> btnConfirmar.setStyle(confirmarHover));
        btnConfirmar.setOnMouseExited(e -> btnConfirmar.setStyle(confirmarNormal));
    }

    public static void botaoCancelar(Button btnCancelar) {

        String fecharNormal = "-fx-background-color:" + CoresPadrao.BOTAO_CANCELAR.getCorTipoMensagem() + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;";
        String fecharHover = "-fx-background-color: " + CoresPadrao.BOTAO_CANCELAR_HOVER.getCorTipoMensagem() + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;";

        aplicaPadraoTamanhoBotao(btnCancelar);

        btnCancelar.setStyle(fecharNormal);
        btnCancelar.setOnMouseEntered(e -> btnCancelar.setStyle(fecharHover));
        btnCancelar.setOnMouseExited(e -> btnCancelar.setStyle(fecharNormal));
    }

    public static void botaoPadrao(Button btnPadrao) {

        String PadraoNormal = "-fx-background-color:" + CoresPadrao.BOTAO_PADRAO.getCorTipoMensagem() + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;";
        String PadraoHover = "-fx-background-color: " + CoresPadrao.BOTAO_PADRAO_HOVER.getCorTipoMensagem() + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;";

        aplicaPadraoTamanhoBotao(btnPadrao);

        btnPadrao.setStyle(PadraoNormal);
        btnPadrao.setOnMouseEntered(e -> btnPadrao.setStyle(PadraoHover));
        btnPadrao.setOnMouseExited(e -> btnPadrao.setStyle(PadraoNormal));
    }

    public static void botaoControleTela(Button botaoControleTela) {

        String estiloNormal = "-fx-background-color: transparent; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 0; -fx-cursor: hand;";
        String estiloHoverPadrao = "-fx-background-color: rgba(255, 255, 255, 0.2); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 0; -fx-cursor: hand;";
        String estiloHoverFechar = "-fx-background-color: #e81123; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 0; -fx-cursor: hand;";

        botaoControleTela.setStyle(estiloNormal);
        botaoControleTela.setOnMouseEntered(e -> botaoControleTela.setStyle(estiloHoverPadrao));
        botaoControleTela.setOnMouseExited(e -> botaoControleTela.setStyle(estiloNormal));

        if (botaoControleTela.getText().equalsIgnoreCase("✕")) {
            botaoControleTela.setStyle(estiloNormal);
            botaoControleTela.setOnMouseEntered(e -> botaoControleTela.setStyle(estiloHoverFechar));
            botaoControleTela.setOnMouseExited(e -> botaoControleTela.setStyle(estiloNormal));
        }
    }

    private static void aplicaPadraoTamanhoBotao(Button btn) {
        if (btn.getText().trim().length() >= 14) {
            btn.setText(verificaReduzNomeBotao(btn.getText()));
        }
        if (btn.getText().trim().length() > 3) {
            btn.setPrefWidth(125);
        }
    }

    private static String verificaReduzNomeBotao(String nomeBotaoAtual) {

        StringBuilder nomeReduzido = new StringBuilder();
        String[] nomeBotao = nomeBotaoAtual.split("\\s+");
        for (int i = 0; i < nomeBotao.length; i++) {
            if (i < (nomeBotao.length - 1)) {
                String palavraAtual = nomeBotao[i];
                if (palavraAtual.length() >= 3) {
                    nomeReduzido.append(palavraAtual, 0, 3).append(". ");
                } else {
                    nomeReduzido.append(palavraAtual).append(" ");
                }
            } else {
                nomeReduzido.append(nomeBotao[i]);
            }
        }
        return nomeReduzido.toString();
    }
}
