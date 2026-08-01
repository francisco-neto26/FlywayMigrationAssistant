package com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao;

import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemSistema;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.antlr.PostgreSQLLexer;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.antlr.PostgreSQLParser;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.modelo.BlocoSql;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.modelo.TipoSql;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.util.CapturarErro;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.util.RemoverComentarios;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.validadores.RegraValidacaoSql;
import com.supergestao.Flyway.migration.assistant.exception.SqlException;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.List;

public class ValidarScriptSQL implements RegraValidacaoSql {

    @Override
    public void validar(CapturarErro capturarErro, String script) {

        String scriptLimpo = RemoverComentarios.removerComentarios(script, false);

        //validarAntlr4(script);
        List<BlocoSql> blocosSql = ExtrairBlocosSql.extrair(scriptLimpo);
        System.out.println("Total de blocos SQL encontrados: " + blocosSql.size());
        for (BlocoSql bloco : blocosSql){
            if (bloco.getTipo().equals(TipoSql.FUNCTION)){
                System.out.println("Bloco procedure \n" + bloco.getCorpo().getConteudo());
                System.out.println("Linha inicial " + bloco.getCorpo().getLinhaInicial());
            }

            if (bloco.getTipo().equals(TipoSql.TRIGGER)){
                System.out.println("Bloco TRIGGER \n" + bloco.getCorpo().getConteudo());
                System.out.println("Linha inicial " + bloco.getCorpo().getLinhaInicial());
            }

        }


        /*
        if (!blocosSql.getTriggers().isEmpty()){
            System.out.println("Bloco trigger \n" + blocosSql.getTriggers().getFirst().getConteudo());
        }*/

        /*
        ValidadorCorpoFuncao validadorCorpo = new ValidadorCorpoFuncao();
        // Valida o conteúdo interno de blocos de funções/procedures ($BODY$ ou $$)
        validadorCorpo.validar(script);
        // Valida comandos soltos que não estão dentro de um corpo de função
        validadorCorpo.validarComandosSqlSoltos(script);*/
    }

    private void validarAntlr4(String script){
        CharStream charStream = CharStreams.fromString(script);
        PostgreSQLLexer lexer = new PostgreSQLLexer(charStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        PostgreSQLParser parser = new PostgreSQLParser(tokens);

        lexer.removeErrorListeners();
        parser.removeErrorListeners();

        //listener para lançar uma exceção imediatamente no primeiro erro de sintaxe.
        parser.addErrorListener(new CapturarErrosSintaxe());

        // Análise sintática. Se houver um erro, o listener lançará a exceção.
        PostgreSQLParser.RootContext arvore = parser.root();

        // O ParseTreeWalker aplica as regras de negócio (ex: UPDATE sem WHERE).
        ParseTreeWalker walker = new ParseTreeWalker();
        ValidadorAntlrSql fiscalizador = new ValidadorAntlrSql();
        walker.walk(fiscalizador, arvore);
    }

    private static class CapturarErrosSintaxe extends BaseErrorListener {
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int linha, int posicaoColuna, String msg_erro, RecognitionException e) {
            throw new SqlException(MensagemSistema.ERRO_LINHA_SQL.MensagemComParametro(linha, posicaoColuna, msg_erro));
        }
    }
}
