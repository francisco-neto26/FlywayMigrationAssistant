package com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.validadores;

import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemSistema;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.antlr.PostgreSQLParser;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.antlr.PostgreSQLParserBaseListener;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.util.CapturarErro;
import org.antlr.v4.runtime.ParserRuleContext;

import java.util.ArrayList;
import java.util.List;

public class ValidadorAntlrSql extends PostgreSQLParserBaseListener {

    private final CapturarErro capturarErro;
    private final int linhaInicial;
    private final boolean modoCorpoFuncao;// para validar apenas o código interno procedural de uma função ($BODY$).

    public ValidadorAntlrSql(CapturarErro capturarErro, int offsetLinha, boolean modoCorpoFuncao) {
        this.capturarErro = capturarErro;
        this.linhaInicial = offsetLinha;
        this.modoCorpoFuncao = modoCorpoFuncao;
    }

    public ValidadorAntlrSql(CapturarErro capturarErro, int offsetLinha) {
        this(capturarErro, offsetLinha, false);
    }

    public ValidadorAntlrSql(CapturarErro capturarErro) {
        this(capturarErro, 1, false);
    }

    // =========================================================================
    // MÉTODOS AUXILIARES E ATALHOS DE REGISTRO DE ERROS
    // =========================================================================

    /**
     * Atalho limpo para registrar o bloqueio de um comando não permitido.
     */
    private void bloquear(ParserRuleContext ctx, MensagemSistema comando) {
        String msg = MensagemSistema.SCRIPT_NAO_PERMITIDO.MensagemComParametro(comando.getMensagem());
        registrarErro(ctx, msg);
    }

    /**
     * Registra um erro de validação calculando a linha real no arquivo original.
     */
    private void registrarErro(ParserRuleContext ctx, String mensagem) {
        if (capturarErro == null) return;
        int linhaOriginal = (ctx != null && ctx.getStart() != null) ? ctx.getStart().getLine() : 1;
        int linhaReal = (linhaInicial > 0) ? (linhaInicial + linhaOriginal - 1) : linhaOriginal;
        capturarErro.registrarErro(linhaReal, mensagem);
    }

    // =========================================================================
    // BLOCO A: SEGURANÇA EXTREMA (BLOQUEIOS)
    // =========================================================================

    @Override
    public void enterDropdbstmt(PostgreSQLParser.DropdbstmtContext ctx) {
        bloquear(ctx, MensagemSistema.CMD_DROP_DATABASE);
    }

    @Override
    public void enterDropstmt(PostgreSQLParser.DropstmtContext ctx) {
        String texto = ctx.getText().toUpperCase();
        if (texto.contains("CASCADE")) {
            bloquear(ctx, MensagemSistema.CMD_DROP_CASCADE);
        }
        if (texto.contains("SCHEMA")) {
            bloquear(ctx, MensagemSistema.CMD_DROP_SCHEMA);
        }
    }

    @Override
    public void enterDroprolestmt(PostgreSQLParser.DroprolestmtContext ctx) {
        bloquear(ctx, MensagemSistema.CMD_DROP_ROLE);
    }

    @Override
    public void enterDropownedstmt(PostgreSQLParser.DropownedstmtContext ctx) {
        bloquear(ctx, MensagemSistema.CMD_DROP_OWNED);
    }

    @Override
    public void enterTruncatestmt(PostgreSQLParser.TruncatestmtContext ctx) {
        bloquear(ctx, MensagemSistema.CMD_TRUNCATE);
    }

    @Override
    public void enterAltersystemstmt(PostgreSQLParser.AltersystemstmtContext ctx) {
        bloquear(ctx, MensagemSistema.CMD_ALTER_SYSTEM);
    }

    @Override
    public void enterAlterrolestmt(PostgreSQLParser.AlterrolestmtContext ctx) {
        bloquear(ctx, MensagemSistema.CMD_ALTER_ROLE);
    }

    @Override
    public void enterCreateextensionstmt(PostgreSQLParser.CreateextensionstmtContext ctx) {
        bloquear(ctx, MensagemSistema.CMD_CREATE_EXTENSION);
    }

    @Override
    public void enterCopystmt(PostgreSQLParser.CopystmtContext ctx) {
        bloquear(ctx, MensagemSistema.CMD_COPY);
    }

    @Override
    public void enterDostmt(PostgreSQLParser.DostmtContext ctx) {
        bloquear(ctx, MensagemSistema.CMD_DO_BLOCK);
    }

    @Override
    public void enterGrantstmt(PostgreSQLParser.GrantstmtContext ctx) {
        String texto = ctx.getText().toUpperCase();
        if (texto.startsWith("REVOKE")) {
            bloquear(ctx, MensagemSistema.CMD_REVOKE);
        }
        if (texto.startsWith("GRANT")) {
            bloquear(ctx, MensagemSistema.CMD_GRANT);
        }
    }

    @Override
    public void enterCreaterolestmt(PostgreSQLParser.CreaterolestmtContext ctx) {
        bloquear(ctx, MensagemSistema.CMD_CREATE_ROLE);
    }

    @Override
    public void enterVariablesetstmt(PostgreSQLParser.VariablesetstmtContext ctx) {
        bloquear(ctx, MensagemSistema.CMD_SET_VARIABLE);
    }

    @Override
    public void enterVariableresetstmt(PostgreSQLParser.VariableresetstmtContext ctx) {
        bloquear(ctx, MensagemSistema.CMD_RESET_VARIABLE);
    }

    @Override
    public void enterVacuumstmt(PostgreSQLParser.VacuumstmtContext ctx) {
        bloquear(ctx, MensagemSistema.CMD_VACUUM);
    }

    @Override
    public void enterClusterstmt(PostgreSQLParser.ClusterstmtContext ctx) {
        bloquear(ctx, MensagemSistema.CMD_CLUSTER);
    }

    @Override
    public void enterReindexstmt(PostgreSQLParser.ReindexstmtContext ctx) {
        bloquear(ctx, MensagemSistema.CMD_REINDEX);
    }

    @Override
    public void enterLoadstmt(PostgreSQLParser.LoadstmtContext ctx) {
        bloquear(ctx, MensagemSistema.CMD_LOAD);
    }

    @Override
    public void enterListenstmt(PostgreSQLParser.ListenstmtContext ctx) {
        bloquear(ctx, MensagemSistema.CMD_LISTEN);
    }

    @Override
    public void enterNotifystmt(PostgreSQLParser.NotifystmtContext ctx) {
        bloquear(ctx, MensagemSistema.CMD_NOTIFY);
    }

    // =========================================================================
    // BLOCO B: PADRÕES FLYWAY
    // =========================================================================

    @Override
    public void enterTransactionstmt(PostgreSQLParser.TransactionstmtContext ctx) {
        String texto = ctx.getText().toUpperCase();
        if (texto.startsWith("SAVEPOINT")) {
            bloquear(ctx, MensagemSistema.CMD_SAVEPOINT);
            return;
        }
        if (texto.startsWith("RELEASE")) {
            bloquear(ctx, MensagemSistema.CMD_RELEASE_SAVEPOINT);
            return;
        }
        bloquear(ctx, MensagemSistema.CMD_TRANSACTION);
    }

    @Override
    public void enterLockstmt(PostgreSQLParser.LockstmtContext ctx) {
        bloquear(ctx, MensagemSistema.CMD_LOCK_TABLE);
    }

    // =========================================================================
    // BLOCO C: SEMÂNTICA DE DML
    // =========================================================================

    @Override
    public void enterUpdatestmt(PostgreSQLParser.UpdatestmtContext ctx) {
        if (ctx.where_or_current_clause() == null) {
            registrarErro(ctx, MensagemSistema.ERRO_UPDATE_SEM_WHERE.getMensagem());
        }

        String texto = ctx.getText().toUpperCase();
        if (texto.contains("WHERE1=1") || texto.contains("WHERETRUE")) {
            registrarErro(ctx, MensagemSistema.ERRO_UPDATE_WHERE_GENERICO.getMensagem());
        }
    }

    @Override
    public void enterDeletestmt(PostgreSQLParser.DeletestmtContext ctx) {
        if (ctx.where_or_current_clause() == null) {
            registrarErro(ctx, MensagemSistema.ERRO_DELETE_SEM_WHERE.getMensagem());
        }

        String texto = ctx.getText().toUpperCase();
        if (texto.contains("WHERE1=1") || texto.contains("WHERETRUE")) {
            registrarErro(ctx, MensagemSistema.ERRO_DELETE_WHERE_GENERICO.getMensagem());
        }
    }

    @Override
    public void enterInsertstmt(PostgreSQLParser.InsertstmtContext ctx) {
        if (ctx.insert_rest() != null && ctx.insert_rest().DEFAULT() != null) {
            registrarErro(ctx, MensagemSistema.ERRO_INSERT_INVALIDO.getMensagem());
        }

        if (ctx.insert_rest() != null
                && ctx.insert_rest().insert_column_list() == null
                && ctx.insert_rest().selectstmt() != null) {
            registrarErro(ctx, MensagemSistema.ERRO_INSERT_SEM_COLUNAS.getMensagem());
        }
    }

    @Override
    public void enterSelectstmt(PostgreSQLParser.SelectstmtContext ctx) {
        if (this.modoCorpoFuncao) {
            return;
        }

        for (int i = 0; i < ctx.getChildCount(); i++) {
            if (ctx.getChild(i) instanceof PostgreSQLParser.Into_clauseContext) {
                registrarErro(ctx, MensagemSistema.ERRO_SELECT_INTO.getMensagem());
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
            validarNomeSnakeCase(ctx, nomeTabela, "table");
            validarTamanhoNome(ctx, nomeTabela, "table");
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
            registrarErro(ctx, MensagemSistema.ERRO_TABELA_SEM_PK.getMensagem());
        }
    }

    @Override
    public void enterColumnDef(PostgreSQLParser.ColumnDefContext ctx) {
        String nomeColuna = ctx.colid().getText().toLowerCase();
        objetosCriados.add("column " + nomeColuna);
        validarNomeSnakeCase(ctx, nomeColuna, "column");
        validarTamanhoNome(ctx, nomeColuna, "column");
    }

    @Override
    public void enterCreatetrigstmt(PostgreSQLParser.CreatetrigstmtContext ctx) {
        String nomeTrigger = ctx.name().getText().toLowerCase();
        objetosCriados.add("trigger " + nomeTrigger);
        validarNomeSnakeCase(ctx, nomeTrigger, "Trigger");
        validarTamanhoNome(ctx, nomeTrigger, "Trigger");
    }

    @Override
    public void enterTableconstraint(PostgreSQLParser.TableconstraintContext ctx) {
        if (ctx.name() != null) {
            String nomeConstraint = ctx.name().getText().toLowerCase();
            objetosCriados.add("constraint " + nomeConstraint);
            validarNomeSnakeCase(ctx, nomeConstraint, "Constraint");
            validarTamanhoNome(ctx, nomeConstraint, "Constraint");
        }
    }

    @Override
    public void enterCreateseqstmt(PostgreSQLParser.CreateseqstmtContext ctx) {
        if (ctx.qualified_name() != null) {
            String nomeSeq = ctx.qualified_name().getText().toLowerCase();
            objetosCriados.add("sequence " + nomeSeq);
            validarNomeSnakeCase(ctx, nomeSeq, "Sequence");
            validarTamanhoNome(ctx, nomeSeq, "Sequence");
        }
    }

    @Override
    public void enterViewstmt(PostgreSQLParser.ViewstmtContext ctx) {
        if (ctx.qualified_name() != null) {
            String nomeView = ctx.qualified_name().getText().toLowerCase();
            objetosCriados.add("view " + nomeView);
            validarNomeSnakeCase(ctx, nomeView, "View");
            validarTamanhoNome(ctx, nomeView, "View");
        }
    }

    @Override
    public void enterCreatematviewstmt(PostgreSQLParser.CreatematviewstmtContext ctx) {
        if (ctx.create_mv_target() != null && ctx.create_mv_target().qualified_name() != null) {
            String nomeView = ctx.create_mv_target().qualified_name().getText().toLowerCase();
            objetosCriados.add("materialized view " + nomeView);
            validarNomeSnakeCase(ctx, nomeView, "Materialized view");
            validarTamanhoNome(ctx, nomeView, "Materialized view");
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
            validarNomeSnakeCase(ctx, nomeTipo, "type (TYPE/ENUM)");
            validarTamanhoNome(ctx, nomeTipo, "type (TYPE/ENUM)");
        }
    }

    @Override
    public void enterCommentstmt(PostgreSQLParser.CommentstmtContext ctx) {
        objetosComentados.add(ctx.getText().toLowerCase());
    }

    @Override
    public void exitRoot(PostgreSQLParser.RootContext ctx) {
        if (this.modoCorpoFuncao) {
            return;
        }

        for (String objetoCriado : objetosCriados) {
            boolean temComentario = false;
            for (String comentario : objetosComentados) {
                if (comentario.contains(objetoCriado)) {
                    temComentario = true;
                    break;
                }
            }
            if (!temComentario) {
                registrarErro(ctx, MensagemSistema.ERRO_ELEMENTO_SEM_COMENTARIO.MensagemComParametro(objetoCriado));
            }
        }
    }

    // =========================================================================
    // BLOCO E: DDL / ESTRUTURA
    // =========================================================================

    @Override
    public void enterIndexstmt(PostgreSQLParser.IndexstmtContext ctx) {
        if (ctx.name() == null || ctx.name().getText().isBlank()) {
            registrarErro(ctx, MensagemSistema.ERRO_INDEX_SEM_NOME.getMensagem());
            return;
        }
        String nomeIndex = ctx.name().getText().toLowerCase();
        validarNomeSnakeCase(ctx, nomeIndex, "Index");
        validarTamanhoNome(ctx, nomeIndex, "Index");
    }

    @Override
    public void enterAltertablestmt(PostgreSQLParser.AltertablestmtContext ctx) {
        if (ctx.relation_expr() != null && ctx.relation_expr().getText().isBlank()) {
            registrarErro(ctx, MensagemSistema.ERRO_ALTER_TABLE_SEM_TABELA.getMensagem());
        }
    }

    @Override
    public void enterCreatefunctionstmt(PostgreSQLParser.CreatefunctionstmtContext ctx) {
        if (ctx.func_name() == null || ctx.func_name().getText().isBlank()) {
            registrarErro(ctx, MensagemSistema.ERRO_FUNCTION_SEM_NOME.getMensagem());
            return;
        }

        String texto = ctx.getText().toUpperCase();
        if (texto.contains("SECURITYDEFINER")) {
            bloquear(ctx, MensagemSistema.CMD_SECURITY_DEFINER);
        }

        String nomeFuncao = ctx.func_name().getText().toLowerCase();
        validarNomeSnakeCase(ctx, nomeFuncao, "Function");
        validarTamanhoNome(ctx, nomeFuncao, "Function");
    }

    @Override
    public void enterCreateplangstmt(PostgreSQLParser.CreateplangstmtContext ctx) {
        bloquear(ctx, MensagemSistema.CMD_CREATE_LANGUAGE);
    }

    // =========================================================================
    // BLOCO F: NOMENCLATURA
    // =========================================================================

    private void validarNomeSnakeCase(ParserRuleContext ctx, String nome, String tipoObjeto) {
        if (nome == null || nome.isBlank()) return;
        String nomeSimples = nome.contains(".") ? nome.substring(nome.lastIndexOf('.') + 1) : nome;
        if (!nomeSimples.matches("^[a-z][a-z0-9_]*$")) {
            registrarErro(ctx, MensagemSistema.ERRO_NOMENCLATURA_SNAKE_CASE.MensagemComParametro(tipoObjeto, nomeSimples));
        }
    }

    private void validarTamanhoNome(ParserRuleContext ctx, String nome, String tipoObjeto) {
        if (nome == null) return;
        String nomeSimples = nome.contains(".") ? nome.substring(nome.lastIndexOf('.') + 1) : nome;
        if (nomeSimples.length() > 63) {
            registrarErro(ctx, MensagemSistema.ERRO_TAMANHO_NOME_EXCEDIDO.MensagemComParametro(tipoObjeto, nomeSimples));
        }
    }
}
