package com.supergestao.Flyway.migration.assistant.dominio.regra.sql.template.padrao;

import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.template.GerarTemplate;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.AcaoBanco;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.ObjetoBanco;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.SubAcaoBanco;

public class TemplateSequence implements GerarTemplate {

    private final AcaoBanco acao;
    private final ObjetoBanco objeto;
    private final SubAcaoBanco subAcao;

    public TemplateSequence(AcaoBanco acao, ObjetoBanco objeto, SubAcaoBanco subAcao) {
        this.acao = acao;
        this.objeto = objeto;
        this.subAcao = subAcao != null ? subAcao : SubAcaoBanco.NENHUMA;
    }

    @Override
    public String gerar() {
        return switch (acao) {
            case CREATE -> gerarCreateSequence();
            case DROP   -> gerarDropSequence();
            case ALTER  -> gerarAlterSequence();
            default -> "\n-- Combinação não suportada para Sequences.\n";
        };
    }

    private String gerarCreateSequence() {
        return """
                CREATE SEQUENCE sq_nome_tabela_id
                    START WITH 1
                    INCREMENT BY 1
                    NO MINVALUE
                    NO MAXVALUE
                    CACHE 1;

                COMMENT ON SEQUENCE sq_nome_tabela_id IS 'Sequência para a coluna X da tabela Y';
                """;
    }

    private String gerarDropSequence() {
        return "DROP SEQUENCE IF EXISTS sq_nome_tabela_id;\n";
    }

    private String gerarAlterSequence() {
        return """
                ALTER SEQUENCE sq_nome_tabela_id
                    RESTART WITH 1000;
                """;
    }
}

