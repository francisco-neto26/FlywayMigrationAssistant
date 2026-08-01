package com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.validadores;

import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.util.CapturarErro;

public interface RegraValidacaoSql {

    void validar(CapturarErro capturarErro, String script);
}