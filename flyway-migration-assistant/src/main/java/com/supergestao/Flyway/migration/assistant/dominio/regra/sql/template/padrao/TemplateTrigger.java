package com.supergestao.Flyway.migration.assistant.dominio.regra.sql.template.padrao;

import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.template.GerarTemplate;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.AcaoBanco;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.ObjetoBanco;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.SubAcaoBanco;

public class TemplateTrigger implements GerarTemplate {

    private final AcaoBanco acao;
    private final ObjetoBanco objeto;
    private final SubAcaoBanco subAcao;

    public TemplateTrigger(AcaoBanco acao, ObjetoBanco objeto, SubAcaoBanco subAcao) {
        this.acao = acao;
        this.objeto = objeto;
        this.subAcao = subAcao != null ? subAcao : SubAcaoBanco.NENHUMA;
    }

    @Override
    public String gerar() {
        return switch (acao) {
            case CREATE -> gerarCreateTrigger();
            case DROP   -> gerarDropTrigger();
            case ALTER  -> switch (subAcao) {
                case RENAME_TRIGGER -> gerarRenameTrigger();
                case DISABLE_TRIGGER -> gerarDisableTrigger();
                case ENABLE_TRIGGER  -> gerarEnableTrigger();
                default -> "-- Selecione uma Sub-Ação para a Trigger";
            };
            default -> "\n-- Combinação não suportada para Triggers.\n";
        };
    }

    private String gerarCreateTrigger() {
        return """
                CREATE OR REPLACE FUNCTION fn_nome_trigger()
                RETURNS TRIGGER AS $$
                BEGIN
                    -- Sua lógica aqui
                    -- IF NEW.coluna IS NULL THEN ...
                    
                    RETURN NEW;
                END;
                $$ LANGUAGE plpgsql;

                COMMENT ON FUNCTION fn_nome_trigger() IS 'Função executada pela trigger trg_nome_trigger';

                -- ========================================================

                CREATE TRIGGER trg_nome_trigger
                    AFTER INSERT OR UPDATE ON nome_tabela
                    FOR EACH ROW
                    EXECUTE FUNCTION fn_nome_trigger();

                COMMENT ON TRIGGER trg_nome_trigger ON nome_tabela IS 'Descrição do momento/evento em que a trigger atua';
                """;
    }

    private String gerarDropTrigger() {
        return """
                DROP TRIGGER IF EXISTS trg_nome_trigger ON nome_tabela;
                DROP FUNCTION IF EXISTS fn_nome_trigger();
                """;
    }

    private String gerarRenameTrigger() {
        return "ALTER TRIGGER trg_nome_antigo ON nome_tabela RENAME TO trg_nome_novo;\n";
    }

    private String gerarDisableTrigger() {
        return "ALTER TABLE nome_tabela DISABLE TRIGGER trg_nome_trigger;\n";
    }

    private String gerarEnableTrigger() {
        return "ALTER TABLE nome_tabela ENABLE TRIGGER trg_nome_trigger;\n";
    }
}

