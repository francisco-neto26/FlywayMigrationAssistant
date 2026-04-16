package com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao;

import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemErro;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.antlr.PostgreSQLLexer;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.antlr.PostgreSQLParser;
import com.supergestao.Flyway.migration.assistant.exception.SqlException;
import org.antlr.v4.runtime.*;

import java.util.ArrayList;
import java.util.List;

public class ValidarScriptSQL implements RegraValidacaoSql {

    @Override
    public void validar(String script) {
        CharStream charStream = CharStreams.fromString(script);
        PostgreSQLLexer lexer = new PostgreSQLLexer(charStream);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        PostgreSQLParser parser = new PostgreSQLParser(tokens);

        lexer.removeErrorListeners();
        parser.removeErrorListeners();

        CapturarErrosSintaxe capturador = new CapturarErrosSintaxe();
        lexer.addErrorListener(capturador);
        parser.addErrorListener(capturador);

        parser.root();


        if (!capturador.getErros().isEmpty()) {
            throw new SqlException(MensagemErro.SCRIPT_ERRO_SINTAXE.MensagemComParametro(String.join("\n", capturador.getErros())));
        }
    }
    private static class CapturarErrosSintaxe extends BaseErrorListener {
        private final List<String> erros = new ArrayList<>();

        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol,
                                int linha, int posicaoColuna, String msg_erro, RecognitionException e) {
            erros.add(String.format("Erro [Linha %d:%d] -> %s", linha, posicaoColuna, msg_erro));
        }

        public List<String> getErros() {
            return erros;
        }
    }
}
