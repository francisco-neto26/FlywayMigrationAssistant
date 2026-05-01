package com.supergestao.Flyway.migration.assistant.dominio.regra.sql.template;

import com.supergestao.Flyway.migration.assistant.dominio.tipo.AcaoBanco;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.ObjetoBanco;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.SubAcaoBanco;

public class TemplateTipoDado implements GerarTemplate {

    private final AcaoBanco acao;
    private final ObjetoBanco objeto;
    private final SubAcaoBanco subAcao;

    public TemplateTipoDado(AcaoBanco acao, ObjetoBanco objeto, SubAcaoBanco subAcao) {
        this.acao = acao;
        this.objeto = objeto;
        this.subAcao = subAcao != null ? subAcao : SubAcaoBanco.NENHUMA;
    }

    @Override
    public String gerar() {
        if (acao == AcaoBanco.CREATE) {
            return switch (objeto) {
                case TYPE   -> "CREATE TYPE status_enum AS ENUM ('ATIVO', 'INATIVO', 'PENDENTE');\n";
                case DOMAIN -> "CREATE DOMAIN cpf_valido AS VARCHAR(14) CHECK (VALUE ~ '^\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}$');\n";
                default     -> "\n-- Objeto de Tipo não suportado.\n";
            };
        }

        if (acao == AcaoBanco.DROP) {
            return switch (objeto) {
                case TYPE   -> "DROP TYPE IF EXISTS status_enum CASCADE;\n";
                case DOMAIN -> "DROP DOMAIN IF EXISTS cpf_valido CASCADE;\n";
                default     -> "\n-- Objeto de Tipo não suportado.\n";
            };
        }

        return "\n-- Ação não aplicável para Tipos/Domínios.\n";
    }
}
