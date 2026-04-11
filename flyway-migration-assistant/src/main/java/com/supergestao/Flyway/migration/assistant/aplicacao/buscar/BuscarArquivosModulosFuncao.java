package com.supergestao.Flyway.migration.assistant.aplicacao.buscar;

import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemErro;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Arquivo;
import com.supergestao.Flyway.migration.assistant.exception.ValidacaoException;
import com.supergestao.Flyway.migration.assistant.persistencia.modulo.RepositorioModulo;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashSet;

public class BuscarArquivosModulosFuncao {
    private final RepositorioModulo repositorioModulo;

    public BuscarArquivosModulosFuncao(RepositorioModulo repositorioModulo) {
        this.repositorioModulo = repositorioModulo;
    }

    public HashSet<Arquivo> carregarArquivos(String caminho, String nomeModulo, String nomeFuncao) {

        try {
            File pastasFuncoes = Paths.get(caminho, nomeModulo, nomeFuncao).toFile();
            return repositorioModulo.carregarArquivos(pastasFuncoes);
        } catch (Exception e) {
            throw new ValidacaoException(MensagemErro.ERRO_ACESSAR_ARQ_EXISTENTE.MensagemComParametro(nomeFuncao));
        }
    }
}

