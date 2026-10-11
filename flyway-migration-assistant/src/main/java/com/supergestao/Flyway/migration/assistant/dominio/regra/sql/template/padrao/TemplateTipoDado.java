package com.supergestao.Flyway.migration.assistant.dominio.regra.sql.template.padrao;

import com.supergestao.Flyway.migration.assistant.dominio.modelo.ArquivoNovo;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Funcao;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Modulo;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.template.GerarTemplate;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.AcaoBanco;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.ObjetoBanco;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.SubAcaoBanco;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.TipoMigration;

public class TemplateTipoDado implements GerarTemplate {

    private final Modulo modulo;
    private final Funcao funcao;
    private final String nomeArquivo;
    private final AcaoBanco acao;
    private final ObjetoBanco objeto;
    private final SubAcaoBanco subAcao;
    private final TipoMigration tipoMigration;

    public TemplateTipoDado(ArquivoNovo arquivoNovo) {
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
