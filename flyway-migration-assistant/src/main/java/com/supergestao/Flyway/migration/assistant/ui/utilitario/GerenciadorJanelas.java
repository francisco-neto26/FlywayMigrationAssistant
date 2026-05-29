package com.supergestao.Flyway.migration.assistant.ui.utilitario;

import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemErro;
import com.supergestao.Flyway.migration.assistant.ui.controller.TelaDialogoController;
import com.supergestao.Flyway.migration.assistant.ui.estado.ContextoAplicacao;

public class GerenciadorJanelas implements IGerenciadorJanelas {

    private ContextoAplicacao contextoAplicacao;

    public GerenciadorJanelas(ContextoAplicacao contextoAplicacao) {
        this.contextoAplicacao = contextoAplicacao;
    }

    @Override
    public boolean exibirDialogo(String TipoMensagem, String titulo, String mensagem, String detalhes, CoresPadrao coresPadrao) {
        try {
            TelaDialogoController confirmacao;
            if (TipoMensagem.equalsIgnoreCase("c")) {
                confirmacao = ConstrutorJanelas.abrirModal(
                        CaminhoTela.TELA_DIALOGO,
                        titulo,
                        coresPadrao,
                        //(TelaDialogoController controller) -> controller.telaConfirmacao(detalhes),
                        this.contextoAplicacao
                );

            } else {
                confirmacao = ConstrutorJanelas.abrirModal(
                        CaminhoTela.TELA_DIALOGO,
                        titulo,
                        coresPadrao,
                        //(TelaDialogoController controller) -> controller.telaMensagem(mensagem, detalhes)
                        this.contextoAplicacao
                );
            }

            return confirmacao.isConfirmado();

        } catch (Exception e) {
            throw new RuntimeException(MensagemErro.ERRO_ABERTURA_TELA.MensagemComParametro("Tela " + titulo), e);
        }
    }
}

