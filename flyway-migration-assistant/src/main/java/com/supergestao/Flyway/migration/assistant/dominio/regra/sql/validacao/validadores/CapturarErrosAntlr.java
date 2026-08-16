package com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.validadores;

import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemSistema;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.util.CapturarErro;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class CapturarErrosAntlr extends BaseErrorListener {
    private final CapturarErro capturarErro;
    private final int linhaInicial;

    public CapturarErrosAntlr(CapturarErro capturarErro, int linhaInicial) {
        this.capturarErro = capturarErro;
        this.linhaInicial = linhaInicial;
    }
    @Override
    public void syntaxError(Recognizer<?, ?> recognizer,
                            Object offendingSymbol,
                            int linhaEntrada,
                            int posicaoColuna,
                            String msg_erro,
                            RecognitionException e) {

        //
        int linha = (linhaInicial > 0) ? (linhaInicial + linhaEntrada - 1) : linhaEntrada;
        String mensagemFormatada = MensagemSistema.SCRIPT_ERRO_SINTAXE.MensagemComParametro(msg_erro);
        capturarErro.registrarErro(linha, mensagemFormatada);
    }
}
