package com.supergestao.Flyway.migration.assistant.dominio.regra.sql.template.padrao;

import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.template.GerarTemplate;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.AcaoBanco;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.ObjetoBanco;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.SubAcaoBanco;

public class TemplateFunction implements GerarTemplate {

    private final AcaoBanco acao;
    private final ObjetoBanco objeto;
    private final SubAcaoBanco subAcao;

    public TemplateFunction(AcaoBanco acao, ObjetoBanco objeto, SubAcaoBanco subAcao) {
        this.acao = acao;
        this.objeto = objeto;
        this.subAcao = subAcao != null ? subAcao : SubAcaoBanco.NENHUMA;
    }

    @Override
    public String gerar() {
        return switch (acao) {
            case CREATE -> gerarCreateFunction();
            case DROP   -> gerarDropFunction();
            case ALTER  -> switch (subAcao) {
                case RENAME_FUNCTION -> gerarRenameFunction();
                case OWNER_TO        -> gerarOwnerFunction();
                case SET_SCHEMA      -> gerarSetSchemaFunction();
                default -> "-- Selecione uma Sub-Ação para a Função";
            };
            default -> "\n-- Combinação não suportada para Funções.\n";
        };
    }

    private String gerarCreateFunction() {
        return """
                CREATE OR REPLACE FUNCTION fn_calcula_algo(p_parametro INT)
                RETURNS NUMERIC AS $$
                DECLARE
                    v_resultado NUMERIC;
                BEGIN
                    -- Lógica de negócio aqui
                    
                    RETURN v_resultado;
                END;
                $$ LANGUAGE plpgsql;

                COMMENT ON FUNCTION fn_calcula_algo(INT) IS 'Descrição da regra de negócio';
                """;
    }

    private String gerarDropFunction() {
        return "DROP FUNCTION IF EXISTS fn_calcula_algo(INT);\n";
    }

    private String gerarRenameFunction() {
        return "ALTER FUNCTION fn_nome_antigo(INT) RENAME TO fn_nome_novo;\n";
    }

    private String gerarOwnerFunction() {
        return "ALTER FUNCTION fn_calcula_algo(INT) OWNER TO nova_role;\n";
    }

    private String gerarSetSchemaFunction() {
        return "ALTER FUNCTION schema_antigo.fn_calcula_algo(INT) SET SCHEMA schema_novo;\n";
    }
}
