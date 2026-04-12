package com.supergestao.Flyway.migration.assistant.aplicacao.salvar;

import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemErro;
import com.supergestao.Flyway.migration.assistant.exception.ValidacaoException;
import com.supergestao.Flyway.migration.assistant.persistencia.gerenciador.modulos.arquivos.GerenciadorModulosArquivos;

import java.nio.file.Paths;

public class SalvarArquivo {
    private final GerenciadorModulosArquivos repositorioModulo;

    public SalvarArquivo(GerenciadorModulosArquivos repositorioModulo) {
        this.repositorioModulo = repositorioModulo;
    }

    public void salvarArquivo(String caminhoDoArquivo, String conteudo) {

        try {

            //precisa implementar a validação do sql, para somente salvar se passar na validação uusar
            repositorioModulo.salvarArquivo(caminhoDoArquivo, conteudo);
        } catch (Exception e) {
            throw new ValidacaoException(MensagemErro.ERRO_ACESSAR_ARQ_EXISTENTE.MensagemComParametro(Paths.get(caminhoDoArquivo).getFileName().toString()));
        }
    }
}
