package com.supergestao.FlywayMigrationAssistant.util;

import com.supergestao.FlywayMigrationAssistant.model.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class GeradorTemplateSql {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final String INDENT = "    ";
    private static final String DOUBLE_INDENT = "        ";

    public static String gerar(String descricao, Tipo tipo, Acao acao, ObjetoBanco objeto, String modulo) {
        String nomeSugerido = formatarNomeObjeto(descricao);
        return gerarCabecalho(descricao, tipo, acao, objeto, modulo) +
                gerarCorpo(acao, objeto, nomeSugerido, descricao);
    }

    private static String formatarNomeObjeto(String descricao) {
        if (descricao == null || descricao.isBlank()) {
            return "nome_objeto";
        }

        String s = descricao.trim().toLowerCase();

        if (s.contains("__")) {
            s = s.substring(s.indexOf("__") + 2);
        }

        return s.replaceAll("\\s+", "_")
                .replaceAll("[^a-z0-9_]", "")
                .replaceAll("_+", "_");
    }

    private static String gerarCabecalho(String descricao, Tipo tipo, Acao acao, ObjetoBanco objeto, String modulo) {
        StringBuilder sb = new StringBuilder();
        sb.append("-- ================================================================\n");
        sb.append("-- MÓDULO:    ").append(modulo != null ? modulo.toUpperCase() : "N/A").append("\n");
        sb.append("-- MIGRATION: ").append(descricao != null ? descricao : "Sem descrição").append("\n");
        sb.append("-- AÇÃO:      ").append(acao != null ? acao : "N/A").append("\n");
        sb.append("-- OBJETO:    ").append(objeto != null ? objeto : "N/A").append("\n");
        sb.append("-- TIPO:      ").append(tipo != null ? tipo.name() : "N/A").append("\n");
        sb.append("-- DATA:      ").append(LocalDateTime.now().format(DATE_FORMATTER)).append("\n");
        sb.append("-- ================================================================\n\n");
        return sb.toString();
    }

    private static String gerarCorpo(Acao acao, ObjetoBanco objeto, String nome, String descricao) {
        if (acao == null || objeto == null) {
            return "-- Selecione Ação e Objeto para gerar o template.\n";
        }

        return switch (acao) {
            case CRIAR -> templateCriar(objeto, nome, descricao);
            case ALTERAR -> templateAlterar(objeto, nome, descricao);
            case REMOVER -> templateRemover(objeto, nome, descricao);
            case RENOMEAR -> templateRenomear(objeto, nome, descricao);
            default -> "-- Comando não implementado para esta combinação.\n";
        };
    }

    private static String templateCriar(ObjetoBanco objeto, String nome, String descricao) {
        return switch (objeto) {
            case TABELA -> criarTabela(nome, descricao);
            case COLUNA -> criarColuna(nome, descricao);
            case INDICE -> criarIndice(nome, descricao);
            case VIEW -> criarView(nome, descricao);
            case VIEW_MATERIALIZADA -> criarViewMaterializada(nome, descricao);
            case FUNCAO -> criarFuncao(nome, descricao);
            case PROCEDIMENTO -> criarProcedimento(nome, descricao);
            case GATILHO -> criarTrigger(nome, descricao);
            case SEQUENCIA -> criarSequence(nome, descricao);
            case CONSTRAINT -> criarConstraint(nome, descricao);
            case ESQUEMA -> criarSchema(nome, descricao);
            default -> "CREATE " + traduzirObjeto(objeto) + " " + nome + ";\n";
        };
    }

    private static String criarTabela(String nome, String descricao) {
        StringBuilder sb = new StringBuilder();

        sb.append("CREATE TABLE IF NOT EXISTS ").append(nome).append(" (\n");
        sb.append(INDENT).append("id BIGSERIAL PRIMARY KEY,\n");
        sb.append(INDENT).append("nome VARCHAR(255) NOT NULL,\n");
        sb.append(INDENT).append("descricao TEXT,\n");
        sb.append(INDENT).append("ativo BOOLEAN NOT NULL DEFAULT TRUE,\n");
        sb.append(INDENT).append("criado_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,\n");
        sb.append(INDENT).append("atualizado_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,\n");
        sb.append(INDENT).append("criado_por VARCHAR(100),\n");
        sb.append(INDENT).append("atualizado_por VARCHAR(100)\n");
        sb.append(");\n\n");

        sb.append("COMMENT ON TABLE ").append(nome).append(" IS '").append(descricao).append("';\n");
        sb.append("COMMENT ON COLUMN ").append(nome).append(".id IS 'Identificador único da tabela';\n");
        sb.append("COMMENT ON COLUMN ").append(nome).append(".nome IS 'Nome do registro';\n");
        sb.append("COMMENT ON COLUMN ").append(nome).append(".descricao IS 'Descrição detalhada do registro';\n");
        sb.append("COMMENT ON COLUMN ").append(nome).append(".ativo IS 'Indica se o registro está ativo (TRUE) ou inativo (FALSE)';\n");
        sb.append("COMMENT ON COLUMN ").append(nome).append(".criado_em IS 'Data e hora de criação do registro';\n");
        sb.append("COMMENT ON COLUMN ").append(nome).append(".atualizado_em IS 'Data e hora da última atualização do registro';\n");
        sb.append("COMMENT ON COLUMN ").append(nome).append(".criado_por IS 'Usuário que criou o registro';\n");
        sb.append("COMMENT ON COLUMN ").append(nome).append(".atualizado_por IS 'Usuário que fez a última atualização';\n\n");

        sb.append("CREATE INDEX IF NOT EXISTS idx_").append(nome).append("_ativo ON ").append(nome).append(" (ativo);\n");
        sb.append("CREATE INDEX IF NOT EXISTS idx_").append(nome).append("_criado_em ON ").append(nome).append(" (criado_em);\n\n");

        sb.append("CREATE OR REPLACE FUNCTION atualizar_timestamp_").append(nome).append("()\n");
        sb.append("RETURNS TRIGGER AS $$\n");
        sb.append("BEGIN\n");
        sb.append(INDENT).append("NEW.atualizado_em = CURRENT_TIMESTAMP;\n");
        sb.append(INDENT).append("RETURN NEW;\n");
        sb.append("END;\n");
        sb.append("$$ LANGUAGE plpgsql;\n\n");

        sb.append("CREATE TRIGGER trigger_atualizar_timestamp_").append(nome).append("\n");
        sb.append(INDENT).append("BEFORE UPDATE ON ").append(nome).append("\n");
        sb.append(INDENT).append("FOR EACH ROW\n");
        sb.append(INDENT).append("EXECUTE FUNCTION atualizar_timestamp_").append(nome).append("();\n");

        return sb.toString();
    }

    private static String criarColuna(String nome, String descricao) {
        StringBuilder sb = new StringBuilder();

        sb.append("ALTER TABLE nome_tabela ADD COLUMN IF NOT EXISTS ").append(nome).append(" VARCHAR(255);\n\n");
        sb.append("COMMENT ON COLUMN nome_tabela.").append(nome).append(" IS '").append(descricao).append("';\n");

        return sb.toString();
    }

    private static String criarIndice(String nome, String descricao) {
        StringBuilder sb = new StringBuilder();

        sb.append("CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_").append(nome).append("\n");
        sb.append(INDENT).append("ON nome_tabela (coluna);\n\n");
        sb.append("COMMENT ON INDEX idx_").append(nome).append(" IS '").append(descricao).append("';\n");

        return sb.toString();
    }

    private static String criarView(String nome, String descricao) {
        StringBuilder sb = new StringBuilder();

        sb.append("CREATE OR REPLACE VIEW vw_").append(nome).append(" AS\n");
        sb.append("SELECT\n");
        sb.append(INDENT).append("t.id,\n");
        sb.append(INDENT).append("t.nome,\n");
        sb.append(INDENT).append("t.descricao,\n");
        sb.append(INDENT).append("t.ativo,\n");
        sb.append(INDENT).append("t.criado_em\n");
        sb.append("FROM nome_tabela t\n");
        sb.append("WHERE t.ativo = TRUE;\n\n");
        sb.append("COMMENT ON VIEW vw_").append(nome).append(" IS '").append(descricao).append("';\n");

        return sb.toString();
    }

    private static String criarViewMaterializada(String nome, String descricao) {
        StringBuilder sb = new StringBuilder();

        sb.append("CREATE MATERIALIZED VIEW IF NOT EXISTS mv_").append(nome).append(" AS\n");
        sb.append("SELECT\n");
        sb.append(INDENT).append("t.id,\n");
        sb.append(INDENT).append("t.nome,\n");
        sb.append(INDENT).append("COUNT(*) as total\n");
        sb.append("FROM nome_tabela t\n");
        sb.append("WHERE t.ativo = TRUE\n");
        sb.append("GROUP BY t.id, t.nome;\n\n");
        sb.append("COMMENT ON MATERIALIZED VIEW mv_").append(nome).append(" IS '").append(descricao).append("';\n\n");
        sb.append("CREATE UNIQUE INDEX IF NOT EXISTS idx_mv_").append(nome).append("_id ON mv_").append(nome).append(" (id);\n");

        return sb.toString();
    }

    private static String criarFuncao(String nome, String descricao) {
        StringBuilder sb = new StringBuilder();

        sb.append("CREATE OR REPLACE FUNCTION fn_").append(nome).append("(\n");
        sb.append(INDENT).append("p_parametro1 INTEGER,\n");
        sb.append(INDENT).append("p_parametro2 VARCHAR\n");
        sb.append(")\n");
        sb.append("RETURNS TABLE (\n");
        sb.append(INDENT).append("id BIGINT,\n");
        sb.append(INDENT).append("nome VARCHAR\n");
        sb.append(") AS $$\n");
        sb.append("BEGIN\n");
        sb.append(INDENT).append("RETURN QUERY\n");
        sb.append(INDENT).append("SELECT t.id, t.nome\n");
        sb.append(INDENT).append("FROM nome_tabela t\n");
        sb.append(INDENT).append("WHERE t.ativo = TRUE;\n");
        sb.append("END;\n");
        sb.append("$$ LANGUAGE plpgsql;\n\n");
        sb.append("COMMENT ON FUNCTION fn_").append(nome).append("(INTEGER, VARCHAR) IS '").append(descricao).append("';\n");

        return sb.toString();
    }

    private static String criarProcedimento(String nome, String descricao) {
        StringBuilder sb = new StringBuilder();

        sb.append("CREATE OR REPLACE PROCEDURE proc_").append(nome).append("(\n");
        sb.append(INDENT).append("p_parametro1 INTEGER,\n");
        sb.append(INDENT).append("p_parametro2 VARCHAR\n");
        sb.append(")\n");
        sb.append("LANGUAGE plpgsql\n");
        sb.append("AS $$\n");
        sb.append("BEGIN\n");
        sb.append(INDENT).append("-- Lógica do procedimento\n");
        sb.append(INDENT).append("RAISE NOTICE 'Executando procedimento';\n");
        sb.append(INDENT).append("\n");
        sb.append(INDENT).append("COMMIT;\n");
        sb.append("END;\n");
        sb.append("$$;\n\n");
        sb.append("COMMENT ON PROCEDURE proc_").append(nome).append("(INTEGER, VARCHAR) IS '").append(descricao).append("';\n");

        return sb.toString();
    }

    private static String criarTrigger(String nome, String descricao) {
        StringBuilder sb = new StringBuilder();

        sb.append("CREATE OR REPLACE FUNCTION fn_trigger_").append(nome).append("()\n");
        sb.append("RETURNS TRIGGER AS $$\n");
        sb.append("BEGIN\n");
        sb.append(INDENT).append("-- Lógica do trigger\n");
        sb.append(INDENT).append("IF TG_OP = 'INSERT' THEN\n");
        sb.append(DOUBLE_INDENT).append("-- Ação para INSERT\n");
        sb.append(DOUBLE_INDENT).append("RETURN NEW;\n");
        sb.append(INDENT).append("ELSIF TG_OP = 'UPDATE' THEN\n");
        sb.append(DOUBLE_INDENT).append("-- Ação para UPDATE\n");
        sb.append(DOUBLE_INDENT).append("RETURN NEW;\n");
        sb.append(INDENT).append("ELSIF TG_OP = 'DELETE' THEN\n");
        sb.append(DOUBLE_INDENT).append("-- Ação para DELETE\n");
        sb.append(DOUBLE_INDENT).append("RETURN OLD;\n");
        sb.append(INDENT).append("END IF;\n");
        sb.append(INDENT).append("RETURN NULL;\n");
        sb.append("END;\n");
        sb.append("$$ LANGUAGE plpgsql;\n\n");
        sb.append("COMMENT ON FUNCTION fn_trigger_").append(nome).append("() IS '").append(descricao).append("';\n\n");

        sb.append("CREATE TRIGGER trigger_").append(nome).append("\n");
        sb.append(INDENT).append("BEFORE INSERT OR UPDATE OR DELETE ON nome_tabela\n");
        sb.append(INDENT).append("FOR EACH ROW\n");
        sb.append(INDENT).append("EXECUTE FUNCTION fn_trigger_").append(nome).append("();\n");

        return sb.toString();
    }

    private static String criarSequence(String nome, String descricao) {
        StringBuilder sb = new StringBuilder();

        sb.append("CREATE SEQUENCE IF NOT EXISTS seq_").append(nome).append("\n");
        sb.append(INDENT).append("START WITH 1\n");
        sb.append(INDENT).append("INCREMENT BY 1\n");
        sb.append(INDENT).append("NO MINVALUE\n");
        sb.append(INDENT).append("NO MAXVALUE\n");
        sb.append(INDENT).append("CACHE 1;\n\n");
        sb.append("COMMENT ON SEQUENCE seq_").append(nome).append(" IS '").append(descricao).append("';\n");

        return sb.toString();
    }

    private static String criarConstraint(String nome, String descricao) {
        StringBuilder sb = new StringBuilder();

        sb.append("-- Constraint de chave única\n");
        sb.append("ALTER TABLE nome_tabela\n");
        sb.append(INDENT).append("ADD CONSTRAINT uk_").append(nome).append(" UNIQUE (coluna);\n\n");

        sb.append("-- Constraint de chave estrangeira\n");
        sb.append("ALTER TABLE nome_tabela\n");
        sb.append(INDENT).append("ADD CONSTRAINT fk_").append(nome).append("\n");
        sb.append(INDENT).append("FOREIGN KEY (coluna_id)\n");
        sb.append(INDENT).append("REFERENCES tabela_referenciada (id)\n");
        sb.append(INDENT).append("ON DELETE CASCADE\n");
        sb.append(INDENT).append("ON UPDATE CASCADE;\n\n");

        sb.append("-- Constraint de check\n");
        sb.append("ALTER TABLE nome_tabela\n");
        sb.append(INDENT).append("ADD CONSTRAINT ck_").append(nome).append("\n");
        sb.append(INDENT).append("CHECK (coluna > 0);\n\n");

        sb.append("COMMENT ON CONSTRAINT uk_").append(nome).append(" ON nome_tabela IS '").append(descricao).append("';\n");

        return sb.toString();
    }

    private static String criarSchema(String nome, String descricao) {
        StringBuilder sb = new StringBuilder();

        sb.append("CREATE SCHEMA IF NOT EXISTS ").append(nome).append(";\n\n");
        sb.append("COMMENT ON SCHEMA ").append(nome).append(" IS '").append(descricao).append("';\n\n");
        sb.append("GRANT USAGE ON SCHEMA ").append(nome).append(" TO PUBLIC;\n");
        sb.append("GRANT ALL ON SCHEMA ").append(nome).append(" TO postgres;\n");

        return sb.toString();
    }

    private static String templateAlterar(ObjetoBanco objeto, String nome, String descricao) {
        return switch (objeto) {
            case TABELA -> alterarTabela(nome, descricao);
            case COLUNA -> alterarColuna(nome, descricao);
            case INDICE -> alterarIndice(nome, descricao);
            case VIEW -> alterarView(nome, descricao);
            case VIEW_MATERIALIZADA -> alterarViewMaterializada(nome, descricao);
            case FUNCAO -> alterarFuncao(nome, descricao);
            case CONSTRAINT -> alterarConstraint(nome, descricao);
            default -> "ALTER " + traduzirObjeto(objeto) + " " + nome + " ...;\n";
        };
    }

    private static String alterarTabela(String nome, String descricao) {
        StringBuilder sb = new StringBuilder();

        sb.append("-- Adicionar coluna\n");
        sb.append("ALTER TABLE ").append(nome).append(" ADD COLUMN IF NOT EXISTS nova_coluna VARCHAR(255);\n");
        sb.append("COMMENT ON COLUMN ").append(nome).append(".nova_coluna IS 'Descrição da nova coluna';\n\n");

        sb.append("-- Modificar tipo de coluna\n");
        sb.append("ALTER TABLE ").append(nome).append(" ALTER COLUMN coluna TYPE INTEGER USING coluna::INTEGER;\n\n");

        sb.append("-- Adicionar NOT NULL\n");
        sb.append("ALTER TABLE ").append(nome).append(" ALTER COLUMN coluna SET NOT NULL;\n\n");

        sb.append("-- Adicionar valor padrão\n");
        sb.append("ALTER TABLE ").append(nome).append(" ALTER COLUMN coluna SET DEFAULT 0;\n\n");

        sb.append("-- Remover coluna\n");
        sb.append("-- ALTER TABLE ").append(nome).append(" DROP COLUMN IF EXISTS coluna_antiga CASCADE;\n");

        return sb.toString();
    }

    private static String alterarColuna(String nome, String descricao) {
        StringBuilder sb = new StringBuilder();

        sb.append("ALTER TABLE nome_tabela ALTER COLUMN ").append(nome).append(" TYPE VARCHAR(500);\n\n");
        sb.append("ALTER TABLE nome_tabela ALTER COLUMN ").append(nome).append(" SET NOT NULL;\n\n");
        sb.append("ALTER TABLE nome_tabela ALTER COLUMN ").append(nome).append(" SET DEFAULT 'valor';\n\n");
        sb.append("COMMENT ON COLUMN nome_tabela.").append(nome).append(" IS '").append(descricao).append("';\n");

        return sb.toString();
    }

    private static String alterarIndice(String nome, String descricao) {
        StringBuilder sb = new StringBuilder();

        sb.append("-- Remover índice antigo\n");
        sb.append("DROP INDEX CONCURRENTLY IF EXISTS idx_").append(nome).append(";\n\n");

        sb.append("-- Recriar índice com nova definição\n");
        sb.append("CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_").append(nome).append("\n");
        sb.append(INDENT).append("ON nome_tabela (coluna1, coluna2);\n\n");
        sb.append("COMMENT ON INDEX idx_").append(nome).append(" IS '").append(descricao).append("';\n");

        return sb.toString();
    }

    private static String alterarView(String nome, String descricao) {
        return criarView(nome, descricao);
    }

    private static String alterarViewMaterializada(String nome, String descricao) {
        StringBuilder sb = new StringBuilder();

        sb.append("REFRESH MATERIALIZED VIEW CONCURRENTLY mv_").append(nome).append(";\n\n");

        sb.append("-- Para alterar a definição, remova e recrie:\n");
        sb.append("-- DROP MATERIALIZED VIEW IF EXISTS mv_").append(nome).append(" CASCADE;\n");
        sb.append("-- ").append(criarViewMaterializada(nome, descricao).replace("\n", "\n-- "));

        return sb.toString();
    }

    private static String alterarFuncao(String nome, String descricao) {
        return criarFuncao(nome, descricao);
    }

    private static String alterarConstraint(String nome, String descricao) {
        StringBuilder sb = new StringBuilder();

        sb.append("-- Remover constraint existente\n");
        sb.append("ALTER TABLE nome_tabela DROP CONSTRAINT IF EXISTS ").append(nome).append(";\n\n");

        sb.append("-- Recriar constraint\n");
        sb.append("ALTER TABLE nome_tabela\n");
        sb.append(INDENT).append("ADD CONSTRAINT ").append(nome).append("\n");
        sb.append(INDENT).append("CHECK (condicao);\n\n");
        sb.append("COMMENT ON CONSTRAINT ").append(nome).append(" ON nome_tabela IS '").append(descricao).append("';\n");

        return sb.toString();
    }

    private static String templateRemover(ObjetoBanco objeto, String nome, String descricao) {
        StringBuilder sb = new StringBuilder();

        sb.append("-- ATENÇÃO: Esta operação é IRREVERSÍVEL!\n");
        sb.append("-- Certifique-se de ter um backup antes de executar.\n\n");

        if (objeto == ObjetoBanco.TABELA) {
            sb.append("-- Remover triggers associados\n");
            sb.append("DROP TRIGGER IF EXISTS trigger_atualizar_timestamp_").append(nome).append(" ON ").append(nome).append(" CASCADE;\n\n");
            sb.append("-- Remover função de trigger\n");
            sb.append("DROP FUNCTION IF EXISTS atualizar_timestamp_").append(nome).append("() CASCADE;\n\n");
        }

        sb.append("DROP ").append(traduzirObjeto(objeto)).append(" IF EXISTS ");

        if (objeto == ObjetoBanco.INDICE) {
            sb.append("idx_");
        } else if (objeto == ObjetoBanco.VIEW) {
            sb.append("vw_");
        } else if (objeto == ObjetoBanco.VIEW_MATERIALIZADA) {
            sb.append("mv_");
        } else if (objeto == ObjetoBanco.FUNCAO) {
            sb.append("fn_");
        } else if (objeto == ObjetoBanco.PROCEDIMENTO) {
            sb.append("proc_");
        } else if (objeto == ObjetoBanco.SEQUENCIA) {
            sb.append("seq_");
        }

        sb.append(nome).append(" CASCADE;\n");

        return sb.toString();
    }

    private static String templateRenomear(ObjetoBanco objeto, String nome, String descricao) {
        StringBuilder sb = new StringBuilder();

        sb.append("ALTER ");

        if (objeto == ObjetoBanco.COLUNA) {
            sb.append("TABLE nome_tabela RENAME COLUMN ").append(nome).append(" TO ").append(nome).append("_novo;\n\n");
            sb.append("COMMENT ON COLUMN nome_tabela.").append(nome).append("_novo IS '").append(descricao).append("';\n");
        } else {
            sb.append(traduzirObjeto(objeto)).append(" ");

            if (objeto == ObjetoBanco.INDICE) {
                sb.append("idx_");
            } else if (objeto == ObjetoBanco.VIEW) {
                sb.append("vw_");
            } else if (objeto == ObjetoBanco.FUNCAO) {
                sb.append("fn_");
            } else if (objeto == ObjetoBanco.PROCEDIMENTO) {
                sb.append("proc_");
            } else if (objeto == ObjetoBanco.SEQUENCIA) {
                sb.append("seq_");
            }

            sb.append(nome).append(" RENAME TO ");

            if (objeto == ObjetoBanco.INDICE) {
                sb.append("idx_");
            } else if (objeto == ObjetoBanco.VIEW) {
                sb.append("vw_");
            } else if (objeto == ObjetoBanco.FUNCAO) {
                sb.append("fn_");
            } else if (objeto == ObjetoBanco.PROCEDIMENTO) {
                sb.append("proc_");
            } else if (objeto == ObjetoBanco.SEQUENCIA) {
                sb.append("seq_");
            }

            sb.append(nome).append("_novo;\n\n");

            if (objeto != ObjetoBanco.COLUNA && objeto != ObjetoBanco.CONSTRAINT) {
                String tipoObjeto = traduzirObjetoParaComment(objeto);
                sb.append("COMMENT ON ").append(tipoObjeto).append(" ");

                if (objeto == ObjetoBanco.INDICE) {
                    sb.append("idx_");
                } else if (objeto == ObjetoBanco.VIEW) {
                    sb.append("vw_");
                } else if (objeto == ObjetoBanco.FUNCAO) {
                    sb.append("fn_");
                } else if (objeto == ObjetoBanco.PROCEDIMENTO) {
                    sb.append("proc_");
                } else if (objeto == ObjetoBanco.SEQUENCIA) {
                    sb.append("seq_");
                }

                sb.append(nome).append("_novo IS '").append(descricao).append("';\n");
            }
        }

        return sb.toString();
    }

    private static String traduzirObjeto(ObjetoBanco objeto) {
        return switch (objeto) {
            case VIEW_MATERIALIZADA -> "MATERIALIZED VIEW";
            case TABELA -> "TABLE";
            case FUNCAO -> "FUNCTION";
            case PROCEDIMENTO -> "PROCEDURE";
            case COLUNA -> "COLUMN";
            case INDICE -> "INDEX";
            case VIEW -> "VIEW";
            case GATILHO -> "TRIGGER";
            case SEQUENCIA -> "SEQUENCE";
            case CONSTRAINT -> "CONSTRAINT";
            case ESQUEMA -> "SCHEMA";
            default -> objeto.name();
        };
    }

    private static String traduzirObjetoParaComment(ObjetoBanco objeto) {
        return switch (objeto) {
            case VIEW_MATERIALIZADA -> "MATERIALIZED VIEW";
            case FUNCAO -> "FUNCTION";
            case PROCEDIMENTO -> "PROCEDURE";
            default -> traduzirObjeto(objeto);
        };
    }
}