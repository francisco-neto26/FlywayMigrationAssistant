package com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao;

import java.util.List;

public class ValidacaoCompletaSql {

    private final List<RegraValidacaoSql> regras;

    public ValidacaoCompletaSql() {

        this.regras = List.of(
                new ValidarSqlVazio(),
                new ValidarScriptSQL());
    }

    public void validarScriptCompleto(String script) {
        for (RegraValidacaoSql regra : regras) {
            regra.validar(script);
        }
    }
}