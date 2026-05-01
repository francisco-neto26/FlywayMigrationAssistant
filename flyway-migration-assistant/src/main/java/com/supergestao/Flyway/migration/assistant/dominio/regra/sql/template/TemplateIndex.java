package com.supergestao.Flyway.migration.assistant.dominio.regra.sql.template;

import com.supergestao.Flyway.migration.assistant.dominio.tipo.AcaoBanco;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.ObjetoBanco;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.SubAcaoBanco;

public class TemplateIndex implements GerarTemplate {

    private final AcaoBanco acao;
    private final ObjetoBanco objeto;
    private final SubAcaoBanco subAcao;

    public TemplateIndex(AcaoBanco acao, ObjetoBanco objeto, SubAcaoBanco subAcao) {
        this.acao = acao;
        this.objeto = objeto;
        this.subAcao = subAcao != null ? subAcao : SubAcaoBanco.NENHUMA;
    }

    @Override
    public String gerar() {
        return switch (acao) {
            case CREATE -> gerarCreateIndex();
            case DROP   -> gerarDropIndex();
            case ALTER  -> switch (subAcao) {
                case RENAME_TABLE -> gerarRenameIndex(); // Reaproveitando Enum de rename genérico
                default -> "-- Selecione uma Sub-Ação para o Índice";
            };
            default -> "\n-- Combinação não suportada para Índices.\n";
        };
    }

    private String gerarCreateIndex() {
        return """
                -- Criar índice simples para performance
                CREATE INDEX idx_nome_tabela_coluna
                    ON nome_tabela (nome_coluna);

                -- Criar índice único (descomente se for o caso)
                -- CREATE UNIQUE INDEX uq_idx_nome_tabela_coluna
                --     ON nome_tabela (nome_coluna);
                """;
    }

    private String gerarDropIndex() {
        return "DROP INDEX IF EXISTS idx_nome_tabela_coluna;\n";
    }

    private String gerarRenameIndex() {
        return "ALTER INDEX idx_nome_antigo RENAME TO idx_nome_novo;\n";
    }
}

