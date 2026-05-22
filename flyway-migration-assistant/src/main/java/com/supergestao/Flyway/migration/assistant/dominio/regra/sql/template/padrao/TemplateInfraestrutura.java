package com.supergestao.Flyway.migration.assistant.dominio.regra.sql.template.padrao;

import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.template.GerarTemplate;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.AcaoBanco;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.ObjetoBanco;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.SubAcaoBanco;

public class TemplateInfraestrutura implements GerarTemplate {

    private final AcaoBanco acao;
    private final ObjetoBanco objeto;
    private final SubAcaoBanco subAcao;

    public TemplateInfraestrutura(AcaoBanco acao, ObjetoBanco objeto, SubAcaoBanco subAcao) {
        this.acao = acao;
        this.objeto = objeto;
        this.subAcao = subAcao != null ? subAcao : SubAcaoBanco.NENHUMA;
    }

    @Override
    public String gerar() {
        if (acao == AcaoBanco.CREATE) {
            return switch (objeto) {
                case SCHEMA     -> "CREATE SCHEMA IF NOT EXISTS nome_schema;\n";
                case EXTENSION  -> "CREATE EXTENSION IF NOT EXISTS unaccent;\n";
                case TABLESPACE -> "CREATE TABLESPACE tbs_rapido LOCATION '/caminho/do/disco';\n";
                case COLLATION  -> "CREATE COLLATION pt_br_ci (provider = icu, locale = 'pt-BR-u-ks-level2', deterministic = false);\n";
                default         -> "\n-- Objeto de Infraestrutura não suportado.\n";
            };
        }

        if (acao == AcaoBanco.DROP) {
            return switch (objeto) {
                case SCHEMA     -> "DROP SCHEMA IF EXISTS nome_schema CASCADE;\n";
                case EXTENSION  -> "DROP EXTENSION IF EXISTS unaccent;\n";
                case TABLESPACE -> "DROP TABLESPACE IF EXISTS tbs_rapido;\n";
                case COLLATION  -> "DROP COLLATION IF EXISTS pt_br_ci;\n";
                default         -> "\n-- Objeto de Infraestrutura não suportado.\n";
            };
        }

        return "\n-- Ação não aplicável para Infraestrutura.\n";
    }
}

