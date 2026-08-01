package com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao;

import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemSistema;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.modelo.BlocoSql;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.util.CapturarErro;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.util.RemoverComentarios;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.validadores.RegraValidacaoSql;

import java.util.List;

public class ValidacaoCompletaSql {

    private final List<RegraValidacaoSql> regras;

    public ValidacaoCompletaSql() {

        this.regras = List.of(
                new ValidarScriptSQL());
    }

    public void validarScriptCompleto(String script) {

        CapturarErro capturarErro = new CapturarErro();

        if (script == null || script.trim().isEmpty()) {
            capturarErro.registrarErro(MensagemSistema.SCRIPT_VAZIO.getMensagem());
            return;
        }

        String scriptLimpo = RemoverComentarios.removerComentarios(script, false);
        List<BlocoSql> blocosSql = ExtrairBlocosSql.extrair(scriptLimpo);



        for (RegraValidacaoSql regra : regras) {
            regra.validar(capturarErro, script);
        }
    }
}