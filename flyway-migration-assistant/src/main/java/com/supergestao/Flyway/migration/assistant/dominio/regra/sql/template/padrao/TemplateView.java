package com.supergestao.Flyway.migration.assistant.dominio.regra.sql.template.padrao;

import com.supergestao.Flyway.migration.assistant.dominio.modelo.ArquivoNovo;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Funcao;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Modulo;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.template.GerarTemplate;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.AcaoBanco;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.ObjetoBanco;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.SubAcaoBanco;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.TipoMigration;

public class TemplateView implements GerarTemplate {

    private final Modulo modulo;
    private final Funcao funcao;
    private final String nomeArquivo;
    private final AcaoBanco acao;
    private final ObjetoBanco objeto;
    private final SubAcaoBanco subAcao;
    private final TipoMigration tipoMigration;

    public TemplateView(ArquivoNovo arquivoNovo) {
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
            case CREATE -> gerarCreateView();
            case DROP   -> gerarDropView();
            case ALTER  -> switch (subAcao) {
                case RENAME_TABLE -> gerarRenameView();
                case SET_SCHEMA   -> gerarSetSchemaView();
                case OWNER_TO     -> gerarOwnerView();
                default -> "-- Selecione uma Sub-Ação para a View";
            };
            default -> "\n-- Combinação não suportada para Views.\n";
        };
    }

    private String gerarCreateView() {
        return """
                CREATE OR REPLACE VIEW vw_nome_view AS
                    SELECT id, descricao
                    FROM nome_tabela
                    WHERE ativo = true;

                COMMENT ON VIEW vw_nome_view IS 'Descrição do propósito desta view';
                """;
    }

    private String gerarDropView() {
        return "DROP VIEW IF EXISTS vw_nome_view;\n";
    }

    private String gerarRenameView() {
        return "ALTER VIEW vw_nome_antigo RENAME TO vw_nome_novo;\n";
    }

    private String gerarSetSchemaView() {
        return "ALTER VIEW schema_antigo.vw_nome_view SET SCHEMA schema_novo;\n";
    }

    private String gerarOwnerView() {
        return "ALTER VIEW vw_nome_view OWNER TO nova_role;\n";
    }
}

