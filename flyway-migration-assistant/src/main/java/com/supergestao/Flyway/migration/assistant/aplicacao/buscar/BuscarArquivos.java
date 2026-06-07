package com.supergestao.Flyway.migration.assistant.aplicacao.buscar;

import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemSistema;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Arquivo;
import com.supergestao.Flyway.migration.assistant.exception.ValidacaoException;
import com.supergestao.Flyway.migration.assistant.persistencia.gerenciador.modulos.arquivos.IGerenciadorModulosArquivosDisco;

import java.nio.file.Paths;
import java.util.HashSet;

public class BuscarArquivos {
    private final IGerenciadorModulosArquivosDisco repositorioModulo;

    public BuscarArquivos(IGerenciadorModulosArquivosDisco repositorioModulo) {
        this.repositorioModulo = repositorioModulo;
    }

    public HashSet<Arquivo> carregarArquivos(String caminho, String nomeModulo, String nomeFuncao) {

        try {
            return repositorioModulo.carregarArquivos(caminho, nomeModulo, nomeFuncao);
        } catch (Exception e) {
            throw new ValidacaoException(MensagemSistema.ERRO_ACESSAR_ARQ_EXISTENTE.MensagemComParametro(nomeFuncao));
        }
    }

    public String buscarConteudoArquivo(String caminho) {
        try {
            return repositorioModulo.buscarConteudoArquivo(caminho);
        } catch (Exception e) {
            throw new ValidacaoException(MensagemSistema.ERRO_ACESSAR_ARQ_EXISTENTE.MensagemComParametro(Paths.get(caminho).getFileName().toString()));
        }
    }

}

