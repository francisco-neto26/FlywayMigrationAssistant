package com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.util;

import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.modelo.TipoSql;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class PadroesTiposSql {

    public static final Map<TipoSql, Pattern> PadroesTiposSql(){

        Map<TipoSql, Pattern> padroes = new LinkedHashMap<>();

        padroes.put(TipoSql.BLOCO_ANONIMO, PadroesValidacaoSql.BLOCO_ANONIMO);
        padroes.put(TipoSql.FUNCTION, PadroesValidacaoSql.FUNCTION);
        padroes.put(TipoSql.TRIGGER, PadroesValidacaoSql.TRIGGER);
        padroes.put(TipoSql.VIEW, PadroesValidacaoSql.VIEW);
        padroes.put(TipoSql.CREATE_TABLE, PadroesValidacaoSql.TABLE);
        padroes.put(TipoSql.CREATE_INDEX, PadroesValidacaoSql.INDEX);
        padroes.put(TipoSql.CREATE_SEQUENCE, PadroesValidacaoSql.SEQUENCE);
        padroes.put(TipoSql.CREATE_SCHEMA, PadroesValidacaoSql.SCHEMA);
        padroes.put(TipoSql.CREATE_TYPE, PadroesValidacaoSql.TYPE);
        padroes.put(TipoSql.CREATE_EXTENSION, PadroesValidacaoSql.EXTENSION);
        padroes.put(TipoSql.ALTER_TABLE, PadroesValidacaoSql.ALTER_TABLE);

        return padroes;
    }

}
