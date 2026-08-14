package com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.validadores;

import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.modelo.BlocoSql;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.util.CapturarErro;

import java.util.List;

public interface RegraValidacaoSql {
    void validar(CapturarErro capturarErro, BlocoSql blocoSql);
}