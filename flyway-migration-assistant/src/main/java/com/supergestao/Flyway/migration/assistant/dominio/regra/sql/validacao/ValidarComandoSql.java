package com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao;

import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemErro;
import com.supergestao.Flyway.migration.assistant.exception.SqlException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidarComandoSql implements RegraValidacaoSql {

    @Override
    public void validar(String script) {
        if (script == null) {
            return;
        }

        for (ComandosProibidos proibido : ComandosProibidos.getRegrasSemantica()) {
            Matcher matcher = Pattern.compile(proibido.getComando()).matcher(script);
            while (matcher.find()) {
                String comandoMaliciosoEncontrado = matcher.group();

                if (!comandoMaliciosoEncontrado.toUpperCase().contains("WHERE")) {
                    throw new SqlException(MensagemErro.SCRIPT_NAO_SEM_CONDICIONAL.MensagemComParametro(proibido.name()));
                }
            }
        }
    }
}