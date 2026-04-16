package com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao;

import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemErro;
import com.supergestao.Flyway.migration.assistant.exception.SqlException;

public class ValidarPadraoFlyway implements RegraValidacaoSql{
    @Override
    public void validar(String script) {

        if (script == null) {
            return;
        }
        String scriptUpper = script.toUpperCase();
        for (ComandosProibidos proibido : ComandosProibidos.getRegrasFlyway()) {
            String comandoBloqueado = proibido.getComando();
            if (scriptUpper.contains(comandoBloqueado)) {
                throw new SqlException(MensagemErro.SCRIPT_NAO_PERMITIDO.MensagemComParametro(comandoBloqueado));
            }
        }
    }
}
