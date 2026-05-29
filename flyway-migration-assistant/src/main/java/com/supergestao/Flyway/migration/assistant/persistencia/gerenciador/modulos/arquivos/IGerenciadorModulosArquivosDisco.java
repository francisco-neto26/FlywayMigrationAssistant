package com.supergestao.Flyway.migration.assistant.persistencia.gerenciador.modulos.arquivos;

import com.supergestao.Flyway.migration.assistant.dominio.modelo.Arquivo;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Modulo;

import java.util.HashSet;
import java.util.Map;

public interface IGerenciadorModulosArquivosDisco {
    void salvarModuloFuncao(String caminhoDoModulo);

    void salvarArquivo(String caminhoDoArquivo, String conteudoSQL);

    String buscarConteudoArquivo(String caminhoDoArquivo);

    Map<String, Modulo> obterModulosFuncoes(String diretorioRaiz);

    Map<String, Modulo> obterModuloOrigem(String diretorioRaiz);

    HashSet<Arquivo> carregarArquivos(String caminhoFuncao, String nomeModulo, String nomeFuncao);

}
