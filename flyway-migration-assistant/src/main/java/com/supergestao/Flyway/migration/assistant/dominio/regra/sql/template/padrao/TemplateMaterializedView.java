package com.supergestao.Flyway.migration.assistant.dominio.regra.sql.template.padrao;

import com.supergestao.Flyway.migration.assistant.dominio.modelo.ArquivoNovo;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Funcao;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Modulo;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.template.GerarTemplate;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.AcaoBanco;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.ObjetoBanco;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.SubAcaoBanco;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.TipoMigration;

public class TemplateMaterializedView implements GerarTemplate {

    private final Modulo modulo;
    private final Funcao funcao;
    private final String nomeArquivo;
    private final AcaoBanco acao;
    private final ObjetoBanco objeto;
    private final SubAcaoBanco subAcao;
    private final TipoMigration tipoMigration;

    public TemplateMaterializedView(ArquivoNovo arquivoNovo) {
        this.modulo = arquivoNovo.getModulo();
        this.funcao = arquivoNovo.getFuncao();
        this.nomeArquivo = arquivoNovo.getNome();
        this.acao = arquivoNovo.getAcaoBanco();
        this.objeto = arquivoNovo.getObjetoBanco();
        this.subAcao = arquivoNovo.getSubAcaoBanco();
        this.tipoMigration = arquivoNovo.getTipo();
    }

    @Override
    public String gerar() {
        return switch (acao) {
            case CREATE -> gerarCreateMView();
            case DROP   -> gerarDropMView();
            case UPDATE -> gerarRefreshMView();
            case ALTER  -> switch (subAcao) {
                case RENAME_TABLE -> gerarRenameMView();
                case OWNER_TO     -> gerarOwnerMView();
                default -> "-- Selecione uma Sub-Ação para a Materialized View";
            };
            default -> "\n-- Combinação não suportada para Materialized Views.\n";
        };
    }

    private String gerarCreateMView() {
        return """
                CREATE MATERIALIZED VIEW mvw_nome_view AS
                    SELECT id, descricao, COUNT(*) as total
                    FROM nome_tabela
                    GROUP BY id, descricao
                WITH DATA;

                COMMENT ON MATERIALIZED VIEW mvw_nome_view IS 'Descrição do cache consolidado';
                """;
    }

    private String gerarDropMView() {
        return "DROP MATERIALIZED VIEW IF EXISTS mvw_nome_view;\n";
    }

    private String gerarRefreshMView() {
        return "REFRESH MATERIALIZED VIEW CONCURRENTLY mvw_nome_view;\n";
    }

    private String gerarRenameMView() {
        return "ALTER MATERIALIZED VIEW mvw_nome_antiga RENAME TO mvw_nome_nova;\n";
    }

    private String gerarOwnerMView() {
        return "ALTER MATERIALIZED VIEW mvw_nome_view OWNER TO nova_role;\n";
    }
}

