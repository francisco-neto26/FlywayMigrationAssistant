package com.supergestao.Flyway.migration.assistant.aplicacao.buscar;

import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemErro;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Arquivo;
import com.supergestao.Flyway.migration.assistant.exception.ValidacaoException;
import com.supergestao.Flyway.migration.assistant.persistencia.gerenciador.modulos.arquivos.GerenciadorModulosArquivos;

import java.nio.file.Paths;
import java.util.HashSet;

public class BuscarArquivos {
    private final GerenciadorModulosArquivos repositorioModulo;

    public BuscarArquivos(GerenciadorModulosArquivos repositorioModulo) {
        this.repositorioModulo = repositorioModulo;
    }

    public HashSet<Arquivo> carregarArquivos(String caminho, String nomeModulo, String nomeFuncao) {

        try {
            return repositorioModulo.carregarArquivos(caminho, nomeModulo, nomeFuncao);
        } catch (Exception e) {
            throw new ValidacaoException(MensagemErro.ERRO_ACESSAR_ARQ_EXISTENTE.MensagemComParametro(nomeFuncao));
        }
    }

    public String buscarConteudoArquivo(String caminho) {
        try {
            return repositorioModulo.buscarConteudoArquivo(caminho);
        } catch (Exception e) {
            throw new ValidacaoException(MensagemErro.ERRO_ACESSAR_ARQ_EXISTENTE.MensagemComParametro(Paths.get(caminho).getFileName().toString()));
        }
    }

}

