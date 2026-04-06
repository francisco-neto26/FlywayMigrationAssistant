package com.supergestao.Flyway.migration.assistant.persistencia.modulo;

import com.supergestao.Flyway.migration.assistant.dominio.modelo.Modulo;

import java.util.Map;

public interface RepositorioModulo {
    void criarDiretorioModulo(String caminhoCompleto);

    Map<String, Modulo> obterModulosExistentes(String caminho);

    Map<String, Modulo> obterModuloOrigem(String caminho);
}
