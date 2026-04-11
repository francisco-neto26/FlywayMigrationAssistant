package com.supergestao.Flyway.migration.assistant.persistencia.modulo;

import com.supergestao.Flyway.migration.assistant.dominio.modelo.Arquivo;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Modulo;

import java.io.File;
import java.util.HashSet;
import java.util.Map;

public interface RepositorioModulo {
    void salvarDiretorio(String caminhoCompleto);

    Map<String, Modulo> obterModulosFuncoes(String caminho);

    Map<String, Modulo> obterModuloOrigem(String caminho);

    HashSet<Arquivo> carregarArquivos(File dirFuncao);
}
