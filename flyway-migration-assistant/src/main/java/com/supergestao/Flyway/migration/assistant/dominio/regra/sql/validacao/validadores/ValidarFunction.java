package com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.validadores;

import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemSistema;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.modelo.BlocoSql;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.modelo.SecaoSql;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.util.CapturarErro;

import java.util.Locale;

public class ValidarFunction implements RegraValidacaoSql {
    @Override
    public void validar(CapturarErro capturarErro, BlocoSql blocoSql) {


        if (!blocoSql.getCabecalho().isVazio()) {
            String cabecalho = blocoSql.getCabecalho().getConteudo().toLowerCase();
            if(cabecalho.contains("returns trigger")){
                validarCabecalhoTrigger(capturarErro, blocoSql.getCabecalho());
            }
        }

        if (!blocoSql.getCorpo().isVazio()) {
            System.out.println("\nValidando corpo: " + blocoSql.getCorpo().getConteudo());
        }

        if (!blocoSql.getFechamento().isVazio()) {
            System.out.println("\nValidando fechamento: " + blocoSql.getFechamento().getConteudo());
        }
    }


    private void validarCabecalhoTrigger(CapturarErro capturarErro, SecaoSql cabecalho){
        String conteudo = cabecalho.getConteudo();
        if(possuiArgumentos(conteudo)){
            int linhaErro = buscarNumeroLinha(conteudo, "(") + cabecalho.getLinhaInicial() - 1;
            capturarErro.registrarErro(linhaErro,MensagemSistema.FUNTION_TRIGGER_ARG.getMensagem()
                    + cabecalho.getConteudo());
        }
    }

    public static boolean possuiArgumentos(String trecho) {
        int posAbertura = trecho.indexOf('(');
        int posFechamento = trecho.indexOf(')', posAbertura);
        if (posAbertura != -1 && posFechamento != -1 && posFechamento > posAbertura) {
            String dentroParenteses = trecho.substring(posAbertura + 1, posFechamento).trim();
            // Retorna true se houver algum caractere dentro dos parênteses
            return !dentroParenteses.isEmpty();
        }
        return false;
    }

    public static int buscarNumeroLinha(String script, String palavraBuscada) {
        int numeroLinha = 1;
        for (String linha : script.lines().toList()) {
            if (linha.toUpperCase().contains(palavraBuscada.toUpperCase())) {
                return numeroLinha;
            }
            numeroLinha++;
        }
        return -1;
    }


}
