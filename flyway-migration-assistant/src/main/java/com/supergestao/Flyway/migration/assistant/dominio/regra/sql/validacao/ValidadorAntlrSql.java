package com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao;

import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemSistema;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.antlr.PostgreSQLParser;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.antlr.PostgreSQLParserBaseListener;
import com.supergestao.Flyway.migration.assistant.exception.SqlException;

import java.util.ArrayList;
import java.util.List;

public class ValidadorAntlrSql extends PostgreSQLParserBaseListener {

    // =========================================================================
    // BLOCO A: SEGURANÇA EXTREMA
    // =========================================================================

    @Override
    public void enterDropdbstmt(PostgreSQLParser.DropdbstmtContext ctx) {
        throw new SqlException(MensagemSistema.SCRIPT_NAO_PERMITIDO.MensagemComParametro("DROP DATABASE"));
    }

    @Override
    public void enterDropstmt(PostgreSQLParser.DropstmtContext ctx) {
        String texto = ctx.getText().toUpperCase();

        if (texto.contains("CASCADE")) {
            throw new SqlException(MensagemSistema.SCRIPT_NAO_PERMITIDO.MensagemComParametro("DROP com CASCADE"));
        }
        if (texto.contains("SCHEMA")) {
            throw new SqlException(MensagemSistema.SCRIPT_NAO_PERMITIDO.MensagemComParametro("DROP SCHEMA"));
        }
    }

    @Override
    public void enterDroprolestmt(PostgreSQLParser.DroprolestmtContext ctx) {
        throw new SqlException(MensagemSistema.SCRIPT_NAO_PERMITIDO.MensagemComParametro("DROP ROLE / DROP USER"));
    }

    @Override
    public void enterDropownedstmt(PostgreSQLParser.DropownedstmtContext ctx) {
        throw new SqlException(MensagemSistema.SCRIPT_NAO_PERMITIDO.MensagemComParametro("DROP OWNED BY"));
    }

    @Override
    public void enterTruncatestmt(PostgreSQLParser.TruncatestmtContext ctx) {
        throw new SqlException(MensagemSistema.SCRIPT_NAO_PERMITIDO.MensagemComParametro("TRUNCATE TABLE"));
    }

    @Override
    public void enterAltersystemstmt(PostgreSQLParser.AltersystemstmtContext ctx) {
        throw new SqlException(MensagemSistema.SCRIPT_NAO_PERMITIDO.MensagemComParametro("ALTER SYSTEM"));
    }

    @Override
    public void enterAlterrolestmt(PostgreSQLParser.AlterrolestmtContext ctx) {
        throw new SqlException(MensagemSistema.SCRIPT_NAO_PERMITIDO.MensagemComParametro("ALTER ROLE / ALTER USER"));
    }

    @Override
    public void enterCreateextensionstmt(PostgreSQLParser.CreateextensionstmtContext ctx) {
        throw new SqlException(MensagemSistema.SCRIPT_NAO_PERMITIDO.MensagemComParametro("CREATE EXTENSION"));
    }

    @Override
    public void enterCopystmt(PostgreSQLParser.CopystmtContext ctx) {
        throw new SqlException(MensagemSistema.SCRIPT_NAO_PERMITIDO.MensagemComParametro("COPY"));
    }

    @Override
    public void enterDostmt(PostgreSQLParser.DostmtContext ctx) {
        throw new SqlException(MensagemSistema.SCRIPT_NAO_PERMITIDO.MensagemComParametro("DO $$"));
    }

    @Override
    public void enterGrantstmt(PostgreSQLParser.GrantstmtContext ctx) {
        String texto = ctx.getText().toUpperCase();
        if (texto.startsWith("REVOKE")) {
            throw new SqlException(MensagemSistema.SCRIPT_NAO_PERMITIDO.MensagemComParametro("REVOKE"));
        }
        if (texto.startsWith("GRANT")) {
            throw new SqlException(MensagemSistema.SCRIPT_NAO_PERMITIDO.MensagemComParametro("GRANT"));
        }

    }

    @Override
    public void enterCreaterolestmt(PostgreSQLParser.CreaterolestmtContext ctx) {
        throw new SqlException(MensagemSistema.SCRIPT_NAO_PERMITIDO.MensagemComParametro("CREATE ROLE / CREATE USER"));
    }

    @Override
    public void enterVariablesetstmt(PostgreSQLParser.VariablesetstmtContext ctx) {
        throw new SqlException(MensagemSistema.SCRIPT_NAO_PERMITIDO.MensagemComParametro(
                "SET de variável de sessão/configuração (SET search_path, SET role, etc.)"));
    }

    @Override
    public void enterVariableresetstmt(PostgreSQLParser.VariableresetstmtContext ctx) {
        throw new SqlException(MensagemSistema.SCRIPT_NAO_PERMITIDO.MensagemComParametro("RESET de variável de configuração"));
    }

    @Override
    public void enterVacuumstmt(PostgreSQLParser.VacuumstmtContext ctx) {
        throw new SqlException(MensagemSistema.SCRIPT_NAO_PERMITIDO.MensagemComParametro("VACUUM / ANALYZE"));
    }

    @Override
    public void enterClusterstmt(PostgreSQLParser.ClusterstmtContext ctx) {
        throw new SqlException(MensagemSistema.SCRIPT_NAO_PERMITIDO.MensagemComParametro("CLUSTER"));
    }

    @Override
    public void enterReindexstmt(PostgreSQLParser.ReindexstmtContext ctx) {
        throw new SqlException(MensagemSistema.SCRIPT_NAO_PERMITIDO.MensagemComParametro("REINDEX"));
    }

    @Override
    public void enterLoadstmt(PostgreSQLParser.LoadstmtContext ctx) {
        throw new SqlException(MensagemSistema.SCRIPT_NAO_PERMITIDO.MensagemComParametro("LOAD"));
    }

    @Override
    public void enterListenstmt(PostgreSQLParser.ListenstmtContext ctx) {
        throw new SqlException(MensagemSistema.SCRIPT_NAO_PERMITIDO.MensagemComParametro("LISTEN"));
    }

    @Override
    public void enterNotifystmt(PostgreSQLParser.NotifystmtContext ctx) {
        throw new SqlException(MensagemSistema.SCRIPT_NAO_PERMITIDO.MensagemComParametro("NOTIFY"));
    }

    // =========================================================================
    // BLOCO B: PADRÕES FLYWAY
    // =========================================================================

    @Override
    public void enterTransactionstmt(PostgreSQLParser.TransactionstmtContext ctx) {
        String texto = ctx.getText().toUpperCase();

        if (texto.startsWith("SAVEPOINT")) {
            throw new SqlException(MensagemSistema.SCRIPT_NAO_PERMITIDO.MensagemComParametro("SAVEPOINT"));
        }
        if (texto.startsWith("RELEASE")) {
            throw new SqlException(MensagemSistema.SCRIPT_NAO_PERMITIDO.MensagemComParametro("RELEASE SAVEPOINT"));
        }

        throw new SqlException(MensagemSistema.SCRIPT_NAO_PERMITIDO.MensagemComParametro(
                "Comandos de transação (BEGIN/COMMIT/ROLLBACK)"));
    }

    @Override
    public void enterLockstmt(PostgreSQLParser.LockstmtContext ctx) {
        throw new SqlException(MensagemSistema.SCRIPT_NAO_PERMITIDO.MensagemComParametro("LOCK TABLE"));
    }

    // =========================================================================
    // BLOCO C: SEMÂNTICA DE DML
    // =========================================================================
    @Override
    public void enterUpdatestmt(PostgreSQLParser.UpdatestmtContext ctx) {
        if (ctx.where_or_current_clause() == null) {
            throw new SqlException(
                    MensagemSistema.SCRIPT_NAO_PERMITIDO.MensagemComParametro("UPDATE sem WHERE detectado.")
            );
        }

        String texto = ctx.getText().toUpperCase();
        if (texto.contains("WHERE1=1") || texto.contains("WHERETRUE")) {
            throw new SqlException(
                    MensagemSistema.SCRIPT_NAO_PERMITIDO.MensagemComParametro("UPDATE com condição genérica (1=1) detectado.")
            );
        }
    }

    @Override
    public void enterDeletestmt(PostgreSQLParser.DeletestmtContext ctx) {
        if (ctx.where_or_current_clause() == null) {
            throw new SqlException(
                    MensagemSistema.SCRIPT_NAO_PERMITIDO.MensagemComParametro("DELETE sem WHERE detectado.")
            );
        }

        String texto = ctx.getText().toUpperCase();
        if (texto.contains("WHERE1=1") || texto.contains("WHERETRUE")) {
            throw new SqlException(
                    MensagemSistema.SCRIPT_NAO_PERMITIDO.MensagemComParametro("DELETE com condição genérica (1=1) detectado.")
            );
        }
    }

    @Override
    public void enterInsertstmt(PostgreSQLParser.InsertstmtContext ctx) {
        if (ctx.insert_rest() != null && ctx.insert_rest().DEFAULT() != null) {
            throw new SqlException(MensagemSistema.SCRIPT_NAO_PERMITIDO.MensagemComParametro(
                    "INSERT inválido ou incompleto"));
        }

        // NOVO — bloqueia INSERT sem lista de colunas explícita: INSERT INTO tabela VALUES(...)
        if (ctx.insert_rest() != null
                && ctx.insert_rest().insert_column_list() == null
                && ctx.insert_rest().selectstmt() != null) {
            throw new SqlException(MensagemSistema.SCRIPT_NAO_PERMITIDO.MensagemComParametro(
                    "INSERT sem lista de colunas explícita. Use: INSERT INTO tabela (col1, col2) VALUES (...)"));
        }
    }

    @Override
    public void enterSelectstmt(PostgreSQLParser.SelectstmtContext ctx) {
        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (ctx.getChild(i) instanceof PostgreSQLParser.Into_clauseContext) {
                throw new SqlException(MensagemSistema.SCRIPT_NAO_PERMITIDO.MensagemComParametro(
                        "SELECT INTO não é permitido. Use CREATE TABLE AS SELECT para criar tabelas explicitamente."));
            }
        }
    }

    // =========================================================================
    // BLOCO D: DICIONÁRIO DE DADOS
    // =========================================================================

    private final List<String> objetosCriados = new ArrayList<>();
    private final List<String> objetosComentados = new ArrayList<>();

    @Override
    public void enterCreatestmt(PostgreSQLParser.CreatestmtContext ctx) {
        if (ctx.qualified_name() != null && !ctx.qualified_name().isEmpty()) {
            String nomeTabela = ctx.qualified_name(0).getText().toLowerCase();
            objetosCriados.add("table " + nomeTabela);
            validarNomeSnakeCase(nomeTabela, "table");
            validarTamanhoNome(nomeTabela, "table");
        }

        boolean temPk = false;

        if (ctx.opttableelementlist() != null
                && ctx.opttableelementlist().tableelementlist() != null) {

            for (PostgreSQLParser.TableelementContext elemento :
                    ctx.opttableelementlist().tableelementlist().tableelement()) {

                if (elemento.tableconstraint() != null) {
                    String textoConstraint = elemento.tableconstraint().constraintelem().getText().toUpperCase();
                    if (textoConstraint.startsWith("PRIMARYKEY")) {
                        temPk = true;
                        break;
                    }
                }

                if (elemento.columnDef() != null
                        && elemento.columnDef().colquallist() != null) {
                    for (PostgreSQLParser.ColconstraintContext colConstraint :
                            elemento.columnDef().colquallist().colconstraint()) {
                        if (colConstraint.colconstraintelem() != null
                                && colConstraint.colconstraintelem().getText().toUpperCase().contains("PRIMARYKEY")) {
                            temPk = true;
                            break;
                        }
                    }
                }
            }
        }

        if (!temPk) {
            throw new SqlException(MensagemSistema.SCRIPT_SEM_DICIONARIO.MensagemComParametro(
                    "Tabela criada sem PRIMARY KEY"));
        }
    }

    @Override
    public void enterColumnDef(PostgreSQLParser.ColumnDefContext ctx) {
        String nomeColuna = ctx.colid().getText().toLowerCase();
        objetosCriados.add("column " + nomeColuna);
        validarNomeSnakeCase(nomeColuna, "column");
        validarTamanhoNome(nomeColuna, "column");
    }

    @Override
    public void enterCreatetrigstmt(PostgreSQLParser.CreatetrigstmtContext ctx) {
        String nomeTrigger = ctx.name().getText().toLowerCase();
        objetosCriados.add("trigger " + nomeTrigger);
        validarNomeSnakeCase(nomeTrigger, "Trigger");
        validarTamanhoNome(nomeTrigger, "Trigger");
    }

    @Override
    public void enterTableconstraint(PostgreSQLParser.TableconstraintContext ctx) {
        if (ctx.name() != null) {
            String nomeConstraint = ctx.name().getText().toLowerCase();
            objetosCriados.add("constraint " + nomeConstraint);
            validarNomeSnakeCase(nomeConstraint, "Constraint");
            validarTamanhoNome(nomeConstraint, "Constraint");
        }
    }

    @Override
    public void enterCreateseqstmt(PostgreSQLParser.CreateseqstmtContext ctx) {
        if (ctx.qualified_name() != null) {
            String nomeSeq = ctx.qualified_name().getText().toLowerCase();
            objetosCriados.add("sequence " + nomeSeq);
            validarNomeSnakeCase(nomeSeq, "Sequence");
            validarTamanhoNome(nomeSeq, "Sequence");
        }
    }

    @Override
    public void enterViewstmt(PostgreSQLParser.ViewstmtContext ctx) {
        if (ctx.qualified_name() != null) {
            String nomeView = ctx.qualified_name().getText().toLowerCase();
            objetosCriados.add("view " + nomeView);
            validarNomeSnakeCase(nomeView, "View");
            validarTamanhoNome(nomeView, "View");
        }
    }

    @Override
    public void enterCreatematviewstmt(PostgreSQLParser.CreatematviewstmtContext ctx) {
        if (ctx.create_mv_target() != null && ctx.create_mv_target().qualified_name() != null) {
            String nomeView = ctx.create_mv_target().qualified_name().getText().toLowerCase();
            objetosCriados.add("materialized view " + nomeView);
            validarNomeSnakeCase(nomeView, "Materialized view");
            validarTamanhoNome(nomeView, "Materialized view");
        }
    }

    @Override
    public void enterDefinestmt(PostgreSQLParser.DefinestmtContext ctx) {
        String texto = ctx.getText().toUpperCase();
        if (!texto.startsWith("CREATETYPE") && !texto.startsWith("CREATEENUM")) {
            return;
        }
        if (ctx.any_name() != null && !ctx.any_name().isEmpty()) {
            String nomeTipo = ctx.any_name(0).getText().toLowerCase();
            objetosCriados.add("type " + nomeTipo);
            validarNomeSnakeCase(nomeTipo, "type (TYPE/ENUM)");
            validarTamanhoNome(nomeTipo, "type (TYPE/ENUM)");
        }
    }

    @Override
    public void enterCommentstmt(PostgreSQLParser.CommentstmtContext ctx) {
        objetosComentados.add(ctx.getText().toLowerCase());
    }

    @Override
    public void exitRoot(PostgreSQLParser.RootContext ctx) {
        for (String objetoCriado : objetosCriados) {
            boolean temComentario = false;
            for (String comentario : objetosComentados) {
                if (comentario.contains(objetoCriado)) {
                    temComentario = true;
                    break;
                }
            }
            if (!temComentario) {
                throw new SqlException(MensagemSistema.SCRIPT_SEM_DICIONARIO.MensagemComParametro(
                        "O elemento (" + objetoCriado + ") foi criado mas não possui comentário!"));
            }
        }
    }

    // =========================================================================
    // BLOCO E: DDL / ESTRUTURA
    // =========================================================================

    @Override
    public void enterIndexstmt(PostgreSQLParser.IndexstmtContext ctx) {
        if (ctx.name() == null || ctx.name().getText().isBlank()) {
            throw new SqlException(MensagemSistema.SCRIPT_SEM_DICIONARIO.MensagemComParametro(
                    "Index sem nome definido"));
        }
        String nomeIndex = ctx.name().getText().toLowerCase();
        validarNomeSnakeCase(nomeIndex, "Index");
        validarTamanhoNome(nomeIndex, "Index");
    }

    @Override
    public void enterAltertablestmt(PostgreSQLParser.AltertablestmtContext ctx) {
        if (ctx.relation_expr() != null && ctx.relation_expr().getText().isBlank()) {
            throw new SqlException(MensagemSistema.SCRIPT_SEM_DICIONARIO.MensagemComParametro(
                    "ALTER TABLE sem identificação da tabela"));
        }
    }

    @Override
    public void enterCreatefunctionstmt(PostgreSQLParser.CreatefunctionstmtContext ctx) {
        if (ctx.func_name() == null || ctx.func_name().getText().isBlank()) {
            throw new SqlException(
                    MensagemSistema.SCRIPT_SEM_DICIONARIO.MensagemComParametro("Function sem nome")
            );
        }

        String texto = ctx.getText().toUpperCase();

        if (texto.contains("SECURITYDEFINER")) {
            throw new SqlException(
                    MensagemSistema.SCRIPT_NAO_PERMITIDO.MensagemComParametro("SECURITY DEFINER não permitido")
            );
        }

        String nomeFuncao = ctx.func_name().getText().toLowerCase();
        validarNomeSnakeCase(nomeFuncao, "Function");
        validarTamanhoNome(nomeFuncao, "Function");
    }

    @Override
    public void enterCreateplangstmt(PostgreSQLParser.CreateplangstmtContext ctx) {
        throw new SqlException(MensagemSistema.SCRIPT_NAO_PERMITIDO.MensagemComParametro("CREATE LANGUAGE"));
    }

    // =========================================================================
    // BLOCO F: NOMENCLATURA
    // =========================================================================
    private void validarNomeSnakeCase(String nome, String tipoObjeto) {
        if (nome == null || nome.isBlank()) return;
        // Remove schema prefix se houver (ex: public.minha_tabela → minha_tabela)
        String nomeSimples = nome.contains(".") ? nome.substring(nome.lastIndexOf('.') + 1) : nome;
        if (!nomeSimples.matches("^[a-z][a-z0-9_]*$")) {
            throw new SqlException(MensagemSistema.SCRIPT_SEM_PADRAO_SQL.MensagemComParametro(
                    tipoObjeto + " \"" + nomeSimples + "\" não segue o padrão snake_case obrigatório. "
                            + "Use apenas letras minúsculas, números e underscore, começando com letra."));
        }
    }

    private void validarTamanhoNome(String nome, String tipoObjeto) {
        if (nome == null) return;
        String nomeSimples = nome.contains(".") ? nome.substring(nome.lastIndexOf('.') + 1) : nome;
        if (nomeSimples.length() > 63) {
            throw new SqlException(MensagemSistema.SCRIPT_SEM_PADRAO_SQL.MensagemComParametro(
                    tipoObjeto + " \"" + nomeSimples + "\" ultrapassa 63 caracteres (limite do PostgreSQL). "
                            + "Nomes maiores são truncados silenciosamente pelo banco."));
        }
    }
}