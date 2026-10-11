package com.supergestao.Flyway.migration.assistant.dominio.regra.sql.template.padrao;

import com.supergestao.Flyway.migration.assistant.dominio.modelo.ArquivoNovo;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Funcao;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Modulo;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.template.GerarTemplate;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.AcaoBanco;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.ObjetoBanco;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.SubAcaoBanco;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.TipoMigration;

public class TemplateSequence implements GerarTemplate {

    private final Modulo modulo;
    private final Funcao funcao;
    private final String nomeArquivo;
    private final AcaoBanco acao;
    private final ObjetoBanco objeto;
    private final SubAcaoBanco subAcao;
    private final TipoMigration tipoMigration;

    public TemplateSequence(ArquivoNovo arquivoNovo) {
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

