package com.supergestao.Flyway.migration.assistant.persistencia.funcao;

import com.supergestao.Flyway.migration.assistant.dominio.modelo.Funcao;

import java.util.Map;

public interface RepositorioFuncao {
    void criarDiretorioFuncao(String caminhoCompleto);

    Map<String, Funcao> obterFuncaoExistentes(String caminho);

}
