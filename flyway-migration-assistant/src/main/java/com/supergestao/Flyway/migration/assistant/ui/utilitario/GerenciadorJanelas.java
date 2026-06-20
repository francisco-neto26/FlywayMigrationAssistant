package com.supergestao.Flyway.migration.assistant.ui.utilitario;

import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemSistema;
import com.supergestao.Flyway.migration.assistant.ui.controller.TelaDialogoController;
import com.supergestao.Flyway.migration.assistant.ui.estado.ContextoAplicacao;

public class GerenciadorJanelas implements IGerenciadorJanelas {

    private ContextoAplicacao contextoAplicacao;

    public GerenciadorJanelas(ContextoAplicacao contextoAplicacao) {
        this.contextoAplicacao = contextoAplicacao;
    }

    @Override
    public boolean exibirDialogo(TipoDialogo tipoDialogo, String titulo, String mensagem, String detalhes) {

        try {
            TelaDialogoController confirmacao;
            confirmacao = ConstrutorJanelas.abrirJanelaDialogo(tipoDialogo,
                    CaminhoTela.TELA_DIALOGO,
                    titulo,
                    mensagem,
                    detalhes,
                    this.contextoAplicacao
            );

            return confirmacao.isConfirmado();

        } catch (Exception e) {
            throw new RuntimeException(MensagemSistema.ERRO_ABERTURA_TELA.MensagemComParametro("Tela " + tipoDialogo.getNome()), e);
        }
    }
}

