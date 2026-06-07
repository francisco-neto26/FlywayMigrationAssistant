package com.supergestao.Flyway.migration.assistant.dominio.regra.sql.template.padrao;

import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.template.GerarTemplate;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.AcaoBanco;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.ObjetoBanco;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.SubAcaoBanco;

public class TemplateTabela implements GerarTemplate {

    private final AcaoBanco acao;
    private final ObjetoBanco objeto;
    private final SubAcaoBanco subAcao;

    public TemplateTabela(AcaoBanco acao, ObjetoBanco objeto, SubAcaoBanco subAcao) {
        this.acao = acao;
        this.objeto = objeto;
        this.subAcao = subAcao != null ? subAcao : SubAcaoBanco.NENHUMA;
    }

    @Override
    public String gerar() {
        return switch (acao) {
            case CREATE -> gerarCreateTable();
            case DROP   -> switch (objeto) {
                case TABLE      -> gerarDropTable();
                case CONSTRAINT -> gerarDropConstraint();
                case COLUMN     -> gerarAlterDropColumn();
                default         -> naoSuportado();
            };
            case ADD    -> switch (objeto) {
                case COLUMN      -> gerarAlterAddColumn();
                case PRIMARY_KEY -> gerarAddConstraintPk();
                case FOREIGN_KEY -> gerarAddConstraintFk();
                case UNIQUE      -> gerarAddConstraintUq();
                case CHECK       -> gerarAddConstraintCheck();
                default          -> naoSuportado();
            };
            case ALTER  -> switch (objeto) {
                case TABLE  -> switch (subAcao) {
                    case RENAME_TABLE -> gerarAlterRenameTable();
                    case SET_SCHEMA   -> gerarAlterSetSchema();
                    case OWNER_TO     -> gerarAlterOwner();
                    case ENABLE_ROW_LEVEL_SECURITY -> gerarAlterRls();
                    default           -> "-- Selecione uma Sub-Ação para a Tabela";
                };
                case COLUMN -> switch (subAcao) {
                    case RENAME_COLUMN -> gerarAlterRenameColumn();
                    case ALTER_TYPE    -> gerarAlterTypeColumn();
                    case SET_NOT_NULL  -> gerarAlterNotNull();
                    case DROP_NOT_NULL -> gerarAlterDropNotNull();
                    case SET_DEFAULT   -> gerarAlterSetDefault();
                    case DROP_DEFAULT  -> gerarAlterDropDefault();
                    default            -> "-- Selecione uma Sub-Ação para a Coluna";
                };
                case CONSTRAINT -> switch (subAcao) {
                    case RENAME_CONSTRAINT -> gerarAlterRenameConstraint();
                    case VALIDATE_CONSTRAINT -> gerarAlterValidateConstraint();
                    case SET_DEFERRABLE -> gerarAlterDeferrable();
                    default -> "-- Selecione uma Sub-Ação para a Constraint";
                };
                default     -> naoSuportado();
            };
            default -> naoSuportado();
        };
    }

    private String naoSuportado() {
        return "\n-- Combinação [" + acao + " + " + objeto + " + " + subAcao + "] não suportada.\n";
    }

    // -------------------------------------------------------------------------
    // CREATE TABLE
    // -------------------------------------------------------------------------
    private String gerarCreateTable() {
        return """
                CREATE TABLE nome_tabela (
                    id            BIGSERIAL                  NOT NULL,
                    descricao     VARCHAR(255)               NOT NULL,
                    ativo         BOOLEAN                    NOT NULL DEFAULT TRUE,
                    criado_em     TIMESTAMP WITH TIME ZONE   NOT NULL DEFAULT NOW(),
                    atualizado_em TIMESTAMP WITH TIME ZONE   NOT NULL DEFAULT NOW(),

                    CONSTRAINT pk_nome_tabela PRIMARY KEY (id)
                );

                COMMENT ON TABLE  nome_tabela                IS 'Descrição da tabela';
                COMMENT ON COLUMN nome_tabela.id             IS 'Identificador único da tabela (PK)';
                COMMENT ON COLUMN nome_tabela.descricao      IS 'Descrição do registro';
                COMMENT ON COLUMN nome_tabela.ativo          IS 'Indica se o registro está ativo';
                COMMENT ON COLUMN nome_tabela.criado_em      IS 'Data e hora de criação do registro';
                COMMENT ON COLUMN nome_tabela.atualizado_em  IS 'Data e hora da última atualização do registro';
                """;
    }

    // -------------------------------------------------------------------------
    // ALTER TABLE — COLUNAS (ADD / DROP)
    // -------------------------------------------------------------------------
    private String gerarAlterAddColumn() {
        return """
                ALTER TABLE nome_tabela
                    ADD COLUMN nova_coluna VARCHAR(100) NOT NULL DEFAULT '';

                COMMENT ON COLUMN nome_tabela.nova_coluna IS 'Descrição da nova coluna';
                """;
    }

    private String gerarAlterDropColumn() {
        return "ALTER TABLE nome_tabela DROP COLUMN nome_coluna;\n";
    }

    // -------------------------------------------------------------------------
    // ALTER TABLE — COLUNAS (ALTER / SUBAÇÕES)
    // -------------------------------------------------------------------------
    private String gerarAlterRenameColumn() {
        return """
                ALTER TABLE nome_tabela RENAME COLUMN coluna_antiga TO coluna_nova;
                COMMENT ON COLUMN nome_tabela.coluna_nova IS 'Descrição atualizada da coluna';
                """;
    }

    private String gerarAlterTypeColumn() {
        return "ALTER TABLE nome_tabela ALTER COLUMN nome_coluna TYPE NOVO_TIPO USING nome_coluna::NOVO_TIPO;\n";
    }

    private String gerarAlterNotNull() {
        return "ALTER TABLE nome_tabela ALTER COLUMN nome_coluna SET NOT NULL;\n";
    }

    private String gerarAlterDropNotNull() {
        return "ALTER TABLE nome_tabela ALTER COLUMN nome_coluna DROP NOT NULL;\n";
    }

    private String gerarAlterSetDefault() {
        return "ALTER TABLE nome_tabela ALTER COLUMN nome_coluna SET DEFAULT 'valor_padrao';\n";
    }

    private String gerarAlterDropDefault() {
        return "ALTER TABLE nome_tabela ALTER COLUMN nome_coluna DROP DEFAULT;\n";
    }

    // -------------------------------------------------------------------------
    // ALTER TABLE — TABELAS (SUBAÇÕES)
    // -------------------------------------------------------------------------
    private String gerarAlterRenameTable() {
        return """
                ALTER TABLE tabela_antiga RENAME TO tabela_nova;
                COMMENT ON TABLE tabela_nova IS 'Descrição atualizada da tabela';
                """;
    }

    private String gerarAlterSetSchema() {
        return "ALTER TABLE schema_antigo.nome_tabela SET SCHEMA schema_novo;\n";
    }

    private String gerarAlterOwner() {
        return "ALTER TABLE nome_tabela OWNER TO nova_role;\n";
    }

    private String gerarAlterRls() {
        return """
                -- Habilitar RLS
                ALTER TABLE nome_tabela ENABLE ROW LEVEL SECURITY;
                -- Criar política de acesso (exemplo)
                CREATE POLICY pol_nome_tabela_select
                    ON nome_tabela
                    FOR SELECT
                    USING (criado_por = current_user);
                COMMENT ON POLICY pol_nome_tabela_select
                    ON nome_tabela IS 'Política de leitura: usuário vê apenas seus próprios registros';
                """;
    }

    // -------------------------------------------------------------------------
    // ALTER TABLE — CONSTRAINTS (ADD)
    // -------------------------------------------------------------------------
    private String gerarAddConstraintFk() {
        return """
                ALTER TABLE nome_tabela
                    ADD CONSTRAINT fk_nome_tabela_nome_referencia
                        FOREIGN KEY (coluna_fk)
                        REFERENCES tabela_referencia (id)
                        ON UPDATE RESTRICT
                        ON DELETE RESTRICT;

                COMMENT ON CONSTRAINT fk_nome_tabela_nome_referencia
                    ON nome_tabela IS 'FK: nome_tabela.coluna_fk → tabela_referencia.id';
                """;
    }

    private String gerarAddConstraintPk() {
        return """
                ALTER TABLE nome_tabela
                    ADD CONSTRAINT pk_nome_tabela PRIMARY KEY (id);
                COMMENT ON CONSTRAINT pk_nome_tabela
                    ON nome_tabela IS 'Chave primária da tabela nome_tabela';
                """;
    }

    private String gerarAddConstraintUq() {
        return """
                ALTER TABLE nome_tabela
                    ADD CONSTRAINT uq_nome_tabela_nome_coluna UNIQUE (nome_coluna);

                COMMENT ON CONSTRAINT uq_nome_tabela_nome_coluna
                    ON nome_tabela IS 'Garante unicidade de nome_coluna em nome_tabela';
                """;
    }

    private String gerarAlterDeferrable() {
        return """
                ALTER TABLE nome_tabela
                    ALTER CONSTRAINT nome_constraint DEFERRABLE INITIALLY DEFERRED;
                """;
    }

    // -------------------------------------------------------------------------
    // ALTER TABLE — CONSTRAINTS (DROP / SUBAÇÕES)
    // -------------------------------------------------------------------------
    private String gerarDropConstraint() {
        return "ALTER TABLE nome_tabela DROP CONSTRAINT nome_constraint;\n";
    }

    private String gerarAlterRenameConstraint() {
        return "ALTER TABLE nome_tabela RENAME CONSTRAINT constraint_antiga TO constraint_nova;\n";
    }

    private String gerarAlterValidateConstraint() {
        return "ALTER TABLE nome_tabela VALIDATE CONSTRAINT nome_constraint;\n";
    }

    private String gerarAddConstraintCheck() {
        return """
                ALTER TABLE nome_tabela
                    ADD CONSTRAINT chk_nome_tabela_nome_coluna CHECK (nome_coluna > 0);

                COMMENT ON CONSTRAINT chk_nome_tabela_nome_coluna
                    ON nome_tabela IS 'Valida que nome_coluna deve ser maior que zero';
                """;
    }

    // -------------------------------------------------------------------------
    // DROP TABLE
    // -------------------------------------------------------------------------
    private String gerarDropTable() {
        return "DROP TABLE nome_tabela;\n";
    }
}
