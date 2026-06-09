package com.supergestao.Flyway.migration.assistant.aplicacao.sincronizar;

import com.supergestao.Flyway.migration.assistant.dominio.modelo.Arquivo;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Modulo;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.RetornoSalvarDiretorio;
import com.supergestao.Flyway.migration.assistant.persistencia.gerenciador.modulos.arquivos.IGerenciadorModulosArquivosDisco;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class SincronizarModulos {

    private final IGerenciadorModulosArquivosDisco iGerenciadorModulosArquivosDisco;

    public SincronizarModulos(IGerenciadorModulosArquivosDisco IGerenciadorModulosArquivosDisco) {
        this.iGerenciadorModulosArquivosDisco = IGerenciadorModulosArquivosDisco;
    }

    public Map<String, Modulo> moduloParaSincronizar(String caminhoOrigem, String caminhoExistentes) {
        return obterModulosNovos(
                iGerenciadorModulosArquivosDisco.obterModuloOrigem(caminhoOrigem),
                iGerenciadorModulosArquivosDisco.obterModulosFuncoes(caminhoExistentes)
        );
    }

    public List<RetornoSalvarDiretorio> criarNovoModulo(Map<String, Modulo> modulosNovos, String caminhoExistentes) {
        List<RetornoSalvarDiretorio> resultados = new ArrayList<>();
        for (Modulo modulo : modulosNovos.values()) {
            String nome = modulo.getNome();
            String caminhoCompleto = Paths.get(caminhoExistentes, nome).toString();
            resultados.addAll(salvarModuloFuncao(nome, caminhoCompleto));
        }
        return resultados;
    }

    public List<RetornoSalvarDiretorio> criarModuloFuncao(String moduloFuncao, String caminhoCompleto) {
        return salvarModuloFuncao(moduloFuncao, caminhoCompleto);
    }

    private List<RetornoSalvarDiretorio> salvarModuloFuncao(String moduloFuncao, String caminhoCompleto) {
        List<RetornoSalvarDiretorio> resultados = new ArrayList<>();
        boolean criado = iGerenciadorModulosArquivosDisco.salvarModuloFuncao(caminhoCompleto);
        resultados.add(new RetornoSalvarDiretorio(criado, moduloFuncao));
        return resultados;
    }

    public Map<String, Modulo> obterModulosExistentes(String caminhoExistentes) {
        return iGerenciadorModulosArquivosDisco.obterModulosFuncoes(caminhoExistentes);
    }

    public Map<String, Modulo> obterModulosNovos(Map<String, Modulo> moduloOrigem, Map<String, Modulo> modulosExistentes) {

        Set<String> nomesExistentes = modulosExistentes.values().stream()
                .map(Modulo::getNome)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        return moduloOrigem.entrySet().stream()
                .filter(entry -> !nomesExistentes.contains(entry.getValue().getNome().toLowerCase()))
                .sorted(Comparator.comparing(entry -> entry.getValue().getNome().toLowerCase()))
                .collect(
                        LinkedHashMap::new,
                        (mapa, entry) -> mapa.put(entry.getKey(), entry.getValue()),
                        Map::putAll
                );
    }

    public HashSet<Arquivo> carregarArquivos(String caminhoFuncao, String nomeModulo, String nomeFuncao) {
        return iGerenciadorModulosArquivosDisco.carregarArquivos(caminhoFuncao, nomeModulo, nomeFuncao);
    }

    public boolean temFuncaoArquivo(String diretorioRaiz, String nomeModulo, String nomeFuncao) {
        File pasta = Paths.get(diretorioRaiz, nomeModulo, nomeFuncao).toFile();
        return Objects.requireNonNull(pasta.list()).length > 0;
    }

}
