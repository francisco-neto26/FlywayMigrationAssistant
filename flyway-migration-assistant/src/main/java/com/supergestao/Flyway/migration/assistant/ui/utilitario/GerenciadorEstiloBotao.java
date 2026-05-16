package com.supergestao.Flyway.migration.assistant.ui.utilitario;

import javafx.scene.control.Button;

public class GerenciadorEstiloBotao {

    public static void BotaoConfirmar(Button btnConfirmar) {

        String confirmarNormal = "-fx-background-color:" + CoresPadrao.BOTAO_CONFIRMAR.getCorTipoMensagem() + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;";
        String confirmarHover = "-fx-background-color: " + CoresPadrao.BOTAO_CONFIRMAR_HOVER.getCorTipoMensagem() + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;";

        btnConfirmar.setStyle(confirmarNormal);
        btnConfirmar.setOnMouseEntered(e -> btnConfirmar.setStyle(confirmarHover));
        btnConfirmar.setOnMouseExited(e -> btnConfirmar.setStyle(confirmarNormal));
    }

    public static void BotaoCancelar(Button btnCancelar) {

        String fecharNormal = "-fx-background-color:" + CoresPadrao.BOTAO_CANCELAR.getCorTipoMensagem() + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;";
        String fecharHover = "-fx-background-color: " + CoresPadrao.BOTAO_CANCELAR_HOVER.getCorTipoMensagem() + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;";

        btnCancelar.setStyle(fecharNormal);
        btnCancelar.setOnMouseEntered(e -> btnCancelar.setStyle(fecharHover));
        btnCancelar.setOnMouseExited(e -> btnCancelar.setStyle(fecharNormal));
    }

    public static void BotaoPadrao(Button btnPadrao) {

        String PadraoNormal = "-fx-background-color:" + CoresPadrao.BOTAO_PADRAO.getCorTipoMensagem() + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;";
        String PadraoHover = "-fx-background-color: " + CoresPadrao.BOTAO_PADRAO_HOVER.getCorTipoMensagem() + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;";

        if (btnPadrao.getText().trim().length() >= 14) {
            btnPadrao.setText(verificaReduzNomeBotao(btnPadrao.getText()));
        }
        if(btnPadrao.getText().trim().length() > 4){
            btnPadrao.setPrefWidth(125);
        }
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
            }else{
                nomeReduzido.append(nomeBotao[i]);
            }
        }
        return nomeReduzido.toString();
    }
}
