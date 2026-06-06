package com.supergestao.Flyway.migration.assistant.dominio.regra.migration;

import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemSistema;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.AcaoBanco;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.ObjetoBanco;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.TipoMigration;
import com.supergestao.Flyway.migration.assistant.exception.ValidacaoException;

import java.text.Normalizer;

public class GerarNomeArquivoMigration {
    public String gerarNomeArquivoMigration(TipoMigration tipoMigration,
                                            AcaoBanco acaoBanco,
                                            ObjetoBanco objetoBanco,
                                            String dataHoraFlyway,
                                            String funcao,
                                            String nome) {

        if (tipoMigration == null) {
            throw new ValidacaoException(MensagemSistema.CAMPO_OBRIGATORIO.MensagemComParametro("Tipo de migration"));
        }

        if (acaoBanco == null) {
            throw new ValidacaoException(MensagemSistema.CAMPO_OBRIGATORIO.MensagemComParametro("Ação no banco"));
        }

        if (objetoBanco == null) {
            throw new ValidacaoException(MensagemSistema.CAMPO_OBRIGATORIO.MensagemComParametro("Objeto no banco"));
        }

        if (dataHoraFlyway == null || dataHoraFlyway.isBlank()) {
            throw new ValidacaoException(MensagemSistema.CAMPO_OBRIGATORIO.MensagemComParametro("Data/Hora Flyway"));
        }

        if (funcao == null || funcao.isBlank()) {
            throw new ValidacaoException(MensagemSistema.CAMPO_OBRIGATORIO.MensagemComParametro("Função"));
        }

        if (nome == null || nome.isBlank()) {
            throw new ValidacaoException(MensagemSistema.CAMPO_OBRIGATORIO.MensagemComParametro("Nome"));
        }


        if (tipoMigration.getRequerTimestamp()) {
            return String.format("%s%s__%s_%s_%s_%s.sql",
                    tipoMigration.getPrefixo(),
                    dataHoraFlyway,
                    formataCamelCase(funcao),
                    acaoBanco.getAcao(),
                    objetoBanco.getIdentificador(),
                    formataCamelCase(nome));
        } else {
            return String.format("%s__%s_%s_%s_%s.sql",
                    tipoMigration.getPrefixo(),
                    formataCamelCase(funcao),
                    acaoBanco.getAcao(),
                    objetoBanco.getIdentificador(),
                    formataCamelCase(nome));

        }
    }

    private String formataCamelCase(String texto) {
        StringBuilder resultado = new StringBuilder();
        String textoNormalizado = Normalizer.normalize(texto, Normalizer.Form.NFD);
        String[] palavras = textoNormalizado.split("[\\s_\\-]+");
        int contador = 0;
        for (String palavra : palavras) {

            if (!palavra.isEmpty()) {
                // Define a primeira letra dependendo do contador
                String primeiraLetra = (contador == 0)
                        ? palavra.substring(0, 1).toLowerCase()
                        : palavra.substring(0, 1).toUpperCase();
                // O resto da palavra é sempre igual! Limpa os caracteres e deixa minúsculo
                String restoDaPalavra = palavra.substring(1).toLowerCase().replaceAll("[^a-zA-Z0-9]", "");
                // Junta tudo
                resultado.append(primeiraLetra).append(restoDaPalavra);
                contador++;
            }
        }
        return resultado.toString();
    }

    public String gerarNomeArquivoMigrationUndo(String nomeArquivo) {
        if (nomeArquivo == null || !nomeArquivo.contains("__")) {
            throw new ValidacaoException(MensagemSistema.ERRO_CONVERTER_NOME_UNDO.MensagemComParametro(nomeArquivo));
        }
        if (!nomeArquivo.toUpperCase().startsWith("V")) {
            throw new ValidacaoException(MensagemSistema.ERRO_ARQUIVO_NAO_VERSIONED.getMensagem());
        }
        nomeArquivo = nomeArquivo.replace(".sql", "");
        String parteUmNome = nomeArquivo.split("__")[0];
        parteUmNome = "U" + parteUmNome.substring(1);

        String partedoisNome = nomeArquivo.split("__")[1];
        String[] nomeSeparado = partedoisNome.split("_");
        if (nomeSeparado.length > 0) {
            String acao = nomeSeparado[1];
            AcaoBanco acaoBanco = AcaoBanco.obterAcaoBanco(acao);
            StringBuilder finalNomeConcatenado = new StringBuilder();
            if (nomeSeparado.length > 2) {
                for (int i = 2; i < nomeSeparado.length; i++) {
                    finalNomeConcatenado.append("_").append(nomeSeparado[i]);
                }
            }

            return String.format("%s__%s_%s%s.sql",
                    parteUmNome,
                    nomeSeparado[0],
                    acaoBanco.getOposto(),
                    finalNomeConcatenado);
        }
        throw new ValidacaoException(MensagemSistema.ERRO_CONVERTER_NOME_UNDO.MensagemComParametro(nomeArquivo));
    }
}