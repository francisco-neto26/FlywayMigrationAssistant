package com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao;

import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemSistema;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.modelo.BlocoSql;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.modelo.TipoSql;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.util.CapturarErro;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.util.RemoverComentarios;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.validadores.RegraValidacaoSql;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.validadores.ValidarAntlr;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.validadores.ValidarFunction;

import java.util.List;

public class ValidacaoCompletaSql {

    private final List<RegraValidacaoSql> regras;

    public ValidacaoCompletaSql() {

        this.regras = List.of(
               new ValidarAntlr()
               // new ValidarFunction());
        );
    }

    public void validarScriptCompleto(String script) {

        CapturarErro capturarErro = new CapturarErro();

        if (script == null || script.trim().isEmpty()) {
            capturarErro.registrarErro(MensagemSistema.SCRIPT_VAZIO.getMensagem());
            return;
        }

        String scriptLimpo = RemoverComentarios.removerComentarios(script, false);
        List<BlocoSql> blocosSql = ExtrairBlocosSql.extrair(scriptLimpo);



        for (RegraValidacaoSql regra : regras) {

            //regra.validar(capturarErro, blocosSql);


            for(BlocoSql bloco : blocosSql){

                regra.validar(capturarErro, bloco);


                /*switch (regra.getClass().getSimpleName()) {
                    case "ValidarScriptSQL" -> regra.validar(capturarErro, bloco);
                    case "ValidarFunction" -> {
                        if( bloco.getTipo().equals(TipoSql.FUNCTION)){
                            regra.validar(capturarErro, bloco);
                        }
                    }
                    default -> throw new IllegalArgumentException("Erro ao processar classes de validação: " + regra.getClass().getSimpleName());
                }*/
            }
        }

        if (capturarErro.temErros()) {
            for (String erro : capturarErro.getErros()){
                throw new RuntimeException(erro);
            }

        }

    }
}