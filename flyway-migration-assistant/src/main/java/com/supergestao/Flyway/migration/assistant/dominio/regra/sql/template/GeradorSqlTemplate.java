package com.supergestao.Flyway.migration.assistant.dominio.regra.sql.template;

import com.supergestao.Flyway.migration.assistant.dominio.tipo.AcaoBanco;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.ObjetoBanco;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.SubAcaoBanco;

import java.util.EnumMap;
import java.util.Map;

public class GeradorSqlTemplate {

    @FunctionalInterface
    private interface TemplateProvider {
        GerarTemplate criar(AcaoBanco acao, ObjetoBanco objeto, SubAcaoBanco subAcao);
    }

    private static final Map<ObjetoBanco, TemplateProvider> REGISTRO = new EnumMap<>(ObjetoBanco.class);

    static {
        // 1. Tabela e seus Agregados
        TemplateProvider providerTabela = TemplateTabela::new;
        REGISTRO.put(ObjetoBanco.TABLE, providerTabela);
        REGISTRO.put(ObjetoBanco.COLUMN, providerTabela);
        REGISTRO.put(ObjetoBanco.CONSTRAINT, providerTabela);
        REGISTRO.put(ObjetoBanco.FOREIGN_KEY, providerTabela);
        REGISTRO.put(ObjetoBanco.PRIMARY_KEY, providerTabela);
        REGISTRO.put(ObjetoBanco.UNIQUE, providerTabela);
        REGISTRO.put(ObjetoBanco.CHECK, providerTabela);

        // 2. Objetos Complexos Independentes
        REGISTRO.put(ObjetoBanco.TRIGGER, TemplateTrigger::new);
        REGISTRO.put(ObjetoBanco.VIEW, TemplateView::new);
        REGISTRO.put(ObjetoBanco.MATERIALIZED_VIEW, TemplateMaterializedView::new);
        REGISTRO.put(ObjetoBanco.SEQUENCE, TemplateSequence::new);
        REGISTRO.put(ObjetoBanco.FUNCTION, TemplateFunction::new);
        REGISTRO.put(ObjetoBanco.PROCEDURE, TemplateProcedure::new);
        REGISTRO.put(ObjetoBanco.INDEX, TemplateIndex::new);

        // 3. Segurança e Acesso
        TemplateProvider providerSeguranca = TemplateSeguranca::new;
        REGISTRO.put(ObjetoBanco.ROLE, providerSeguranca);
        REGISTRO.put(ObjetoBanco.GRANT, providerSeguranca);
        REGISTRO.put(ObjetoBanco.POLICY, providerSeguranca);

        // 4. Tipos Customizados
        TemplateProvider providerTipo = TemplateTipoDado::new;
        REGISTRO.put(ObjetoBanco.TYPE, providerTipo);
        REGISTRO.put(ObjetoBanco.DOMAIN, providerTipo);

        // 5. Infraestrutura e Setup
        TemplateProvider providerInfra = TemplateInfraestrutura::new;
        REGISTRO.put(ObjetoBanco.SCHEMA, providerInfra);
        REGISTRO.put(ObjetoBanco.EXTENSION, providerInfra);
        REGISTRO.put(ObjetoBanco.TABLESPACE, providerInfra);
        REGISTRO.put(ObjetoBanco.COLLATION, providerInfra);
    }

    public static String gerarTemplate(String modulo, String funcao, String nomeScript,
                                       AcaoBanco acao, ObjetoBanco objeto,
                                       SubAcaoBanco subAcao, String tipoMigration) {

        String cabecalho = TemplateCabecalhoSql.montarCabecalho(modulo, funcao, nomeScript,
                acao.name(), objeto.name(), subAcao.name(), tipoMigration);

        TemplateProvider provider = REGISTRO.get(objeto);

        GerarTemplate estrategia = (provider != null)
                ? provider.criar(acao, objeto, subAcao)
                : () -> "\n-- Escreva seu script SQL aqui...\n";

        return cabecalho + estrategia.gerar();
    }
}
