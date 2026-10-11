package com.supergestao.Flyway.migration.assistant.dominio.regra.sql.template.padrao;

import com.supergestao.Flyway.migration.assistant.dominio.modelo.ArquivoNovo;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Funcao;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Modulo;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.template.GerarTemplate;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.AcaoBanco;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.ObjetoBanco;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.SubAcaoBanco;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.TipoMigration;

public class TemplateSeguranca implements GerarTemplate {

    private final Modulo modulo;
    private final Funcao funcao;
    private final String nomeArquivo;
    private final AcaoBanco acao;
    private final ObjetoBanco objeto;
    private final SubAcaoBanco subAcao;
    private final TipoMigration tipoMigration;

    public TemplateSeguranca(ArquivoNovo arquivoNovo) {
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
                case ROLE   -> "CREATE ROLE nome_usuario WITH LOGIN PASSWORD 'senha123';\n";
                case GRANT  -> "GRANT SELECT, INSERT, UPDATE ON ALL TABLES IN SCHEMA public TO nome_usuario;\n";
                case POLICY -> gerarCreatePolicy();
                default     -> "\n-- Objeto de Segurança não suportado no CREATE.\n";
            };
        }

        if (acao == AcaoBanco.DROP || acao == AcaoBanco.REMOVE) {
            return switch (objeto) {
                case ROLE   -> "DROP ROLE IF EXISTS nome_usuario;\n";
                case GRANT  -> "REVOKE ALL PRIVILEGES ON ALL TABLES IN SCHEMA public FROM nome_usuario;\n";
                case POLICY -> "DROP POLICY IF EXISTS pol_leitura ON nome_tabela;\n";
                default     -> "\n-- Objeto de Segurança não suportado no DROP/REVOKE.\n";
            };
        }

        return "\n-- Ação não aplicável a Segurança.\n";
    }

    private String gerarCreatePolicy() {
        return """
                CREATE POLICY pol_leitura_propria
                    ON nome_tabela
                    FOR SELECT
                    USING (usuario_id = current_user_id());

                COMMENT ON POLICY pol_leitura_propria ON nome_tabela IS 'Usuário vê apenas os próprios registros';
                """;
    }
}
