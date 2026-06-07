package com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao;

import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemSistema;
import com.supergestao.Flyway.migration.assistant.exception.SqlException;

public class ValidarSqlVazio implements RegraValidacaoSql {

    @Override
    public void validar(String script) {
        if (script == null || script.trim().isEmpty()) {
            throw new SqlException(MensagemSistema.SCRIPT_VAZIO.getMensagem());
        }
    }
}
