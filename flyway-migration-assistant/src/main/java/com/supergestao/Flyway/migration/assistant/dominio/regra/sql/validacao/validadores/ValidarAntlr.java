package com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.validadores;

import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.antlr.PostgreSQLLexer;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.antlr.PostgreSQLParser;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.modelo.BlocoSql;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.modelo.TipoSql;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.util.CapturarErro;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.List;

public class ValidarAntlr implements RegraValidacaoSql {

    @Override
    public void validar(CapturarErro capturarErro, BlocoSql blocosSql) {
        String conteudo = blocosSql.getScriptOriginal();
        int linhaInicialBloco = 1;
        //Configura os Streams do ANTLR4
        CharStream charStream = CharStreams.fromString(conteudo.toString());
        PostgreSQLLexer lexer = new PostgreSQLLexer(charStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        PostgreSQLParser parser = new PostgreSQLParser(tokens);

        //Limpa listeners padrão e usa o capturador de erros sintáticos
        lexer.removeErrorListeners();
        parser.removeErrorListeners();
        parser.addErrorListener(new CapturarErrosAntlr(capturarErro, linhaInicialBloco));

        //Executa a análise sintática para construir a Árvore de Sintaxe (AST)
        PostgreSQLParser.RootContext arvore = parser.root();

        //Executa o ParseTreeWalker com o ValidadorAntlrSql para checar regras semânticas
        boolean modoCorpoFuncao = (blocosSql.getTipo() == TipoSql.FUNCTION || blocosSql.getTipo() == TipoSql.BLOCO_ANONIMO);
        ValidadorAntlrSql fiscalizador = new ValidadorAntlrSql(capturarErro, linhaInicialBloco, modoCorpoFuncao);

        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(fiscalizador, arvore);

    }
}
