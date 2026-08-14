package com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao;

import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.modelo.BlocoSql;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.modelo.SecaoSql;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.modelo.TipoSql;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.util.PadroesTiposSql;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.util.PadroesValidacaoSql;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class ExtrairBlocosSql {

    public static List<BlocoSql> extrair(String scriptBruto) {

        List<BlocoSql> blocosSql = new ArrayList<>();
        Map<TipoSql, Pattern> padroes = PadroesTiposSql.PadroesTiposSql();

        boolean tipoIdentificado = false;

        for (Map.Entry<TipoSql, Pattern> padrao : padroes.entrySet()) {
            TipoSql tipo = padrao.getKey();
            Pattern padraoValidacao = padrao.getValue();
            if (padraoValidacao.matcher(scriptBruto).find()) {
                switch (tipo) {
                    case BLOCO_ANONIMO, FUNCTION -> extrairBlocosProcedurais(tipo, scriptBruto, blocosSql);
                    case TRIGGER -> extrairTriggers(tipo, scriptBruto, blocosSql);
                    case VIEW -> extrairViews(scriptBruto, blocosSql);
                }
                tipoIdentificado = true;
                //break;--comentado para processar outros tipos do arquivo, precisa revisar esta condição
            }
        }

        if (!tipoIdentificado) {
            extrairComandosSoltos(scriptBruto, blocosSql);
        }

        return blocosSql;
    }

    private static void extrairBlocosProcedurais(TipoSql tipoSql, String scriptBruto, List<BlocoSql> blocosSql) {
        Matcher matcher = PadroesValidacaoSql.BLOCO_PROCEDURAL.matcher(scriptBruto);
        while (matcher.find()) {
            int linhaInicial = 1;
            int linhaFinal = (int) scriptBruto.lines().count();
            //linha inicial e final do corpo
            int linhaInicioCorpo = calcularLinhaInicial(scriptBruto, matcher.start());
            int linhaFimCorpo = calcularLinhaInicial(scriptBruto, matcher.end() - 1);

            String conteudocabecalho = scriptIntervaloLinhas(scriptBruto, linhaInicial, linhaInicioCorpo);
            SecaoSql secaoSql = new SecaoSql(conteudocabecalho, linhaInicial);

            String conteudoCorpo = scriptIntervaloLinhas(scriptBruto, linhaInicioCorpo + 1, linhaFimCorpo);
            SecaoSql secaoCorpo = new SecaoSql(conteudoCorpo, linhaInicioCorpo);

            String conteudoFechamento = scriptIntervaloLinhas(scriptBruto, linhaFimCorpo, linhaFinal);
            SecaoSql secaoFechamento = new SecaoSql(conteudoFechamento, linhaFimCorpo);

            blocosSql.add(new BlocoSql(tipoSql, scriptBruto, secaoSql, secaoCorpo, secaoFechamento));
        }
    }

    private static void extrairTriggers(TipoSql tipoSql, String scriptBruto, List<BlocoSql> blocosSql) {
        Matcher matcher = PadroesValidacaoSql.TRIGGER.matcher(scriptBruto);
        while (matcher.find()) {
            //linha inicial e final do corpo
            int linhaInicioCorpo = calcularLinhaInicial(scriptBruto, matcher.start());
            int linhaFimCorpo = (int) scriptBruto.lines().count();

            String conteudoCorpo = scriptIntervaloLinhas(scriptBruto, linhaInicioCorpo, linhaFimCorpo);
            SecaoSql secaoCorpo = new SecaoSql(conteudoCorpo, linhaInicioCorpo);

            blocosSql.add(new BlocoSql(tipoSql, scriptBruto, null, secaoCorpo, null));
        }
    }

    private static void extrairViews(String scriptBruto, List<BlocoSql> blocosSql) {
        Matcher matcher = PadroesValidacaoSql.VIEW.matcher(scriptBruto);
        while (matcher.find()) {
            String conteudo = matcher.group(0);
            int linhaInicial = calcularLinhaInicial(scriptBruto, matcher.start());
            //separaBlocoSql.adicionarBloco(new BlocoSql(conteudo, linhaInicial, TipoSql.VIEW));
        }
    }

    private static void extrairComandosSoltos(String scriptBruto, List<BlocoSql> blocosSql) {
        // Lógica de fallback para capturar comandos soltos terminados em ;
        // que estejam fora dos blocos já capturados.
    }

    private static int calcularLinhaInicial(String script, int inicial) {
        int linha = 1;
        for (int i = 0; i < inicial && i < script.length(); i++) {
            if (script.charAt(i) == '\n') {
                linha++;
            }
        }
        return linha;
    }

    public static String scriptIntervaloLinhas(String script, int linhaInicio, int linhaFim) {
        if (script == null || linhaInicio < 1 || linhaFim < linhaInicio) {
            return "";
        }
        int quantidadeLinhas = (linhaFim - linhaInicio) + 1;
        return script.lines()
                .skip(linhaInicio - 1)
                .limit(quantidadeLinhas)
                .collect(Collectors.joining("\n"));
    }
}
