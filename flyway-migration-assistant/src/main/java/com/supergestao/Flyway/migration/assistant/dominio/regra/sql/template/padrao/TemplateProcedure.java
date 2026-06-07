package com.supergestao.Flyway.migration.assistant.dominio.regra.sql.template.padrao;

import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.template.GerarTemplate;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.AcaoBanco;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.ObjetoBanco;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.SubAcaoBanco;

public class TemplateProcedure implements GerarTemplate {

    private final AcaoBanco acao;
    private final ObjetoBanco objeto;
    private final SubAcaoBanco subAcao;

    public TemplateProcedure(AcaoBanco acao, ObjetoBanco objeto, SubAcaoBanco subAcao) {
        this.acao = acao;
        this.objeto = objeto;
        this.subAcao = subAcao != null ? subAcao : SubAcaoBanco.NENHUMA;
    }

    @Override
    public String gerar() {
        return switch (acao) {
            case CREATE -> gerarCreateProcedure();
            case DROP   -> gerarDropProcedure();
            case ALTER  -> switch (subAcao) {
                case RENAME_FUNCTION -> gerarRenameProcedure();
                case OWNER_TO        -> gerarOwnerProcedure();
                case SET_SCHEMA      -> gerarSetSchemaProcedure();
                default -> "-- Selecione uma Sub-Ação para a Procedure";
            };
            default -> "\n-- Combinação não suportada para Procedures.\n";
        };
    }

    private String gerarCreateProcedure() {
        return """
                CREATE OR REPLACE PROCEDURE sp_processa_dados(p_parametro INT)
                LANGUAGE plpgsql AS $$
                BEGIN
                    -- Lógica de processamento em lote
                    -- COMMIT; -- Procedures permitem controle transacional
                    
                END;
                $$;

                COMMENT ON PROCEDURE sp_processa_dados(INT) IS 'Descrição do processamento em lote';
                """;
    }

    private String gerarDropProcedure() {
        return "DROP PROCEDURE IF EXISTS sp_processa_dados(INT);\n";
    }

    private String gerarRenameProcedure() {
        return "ALTER PROCEDURE sp_nome_antigo(INT) RENAME TO sp_nome_novo;\n";
    }

    private String gerarOwnerProcedure() {
        return "ALTER PROCEDURE sp_processa_dados(INT) OWNER TO nova_role;\n";
    }

    private String gerarSetSchemaProcedure() {
        return "ALTER PROCEDURE schema_antigo.sp_processa_dados(INT) SET SCHEMA schema_novo;\n";
    }
}
