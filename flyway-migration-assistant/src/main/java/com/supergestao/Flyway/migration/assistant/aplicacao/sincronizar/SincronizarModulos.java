package com.supergestao.Flyway.migration.assistant.aplicacao.sincronizar;

import com.supergestao.Flyway.migration.assistant.dominio.modelo.Modulo;
import com.supergestao.Flyway.migration.assistant.persistencia.gerenciador.modulos.arquivos.IGerenciadorModulosArquivosDisco;

import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SincronizarModulos {

    private final IGerenciadorModulosArquivosDisco iGerenciadorModulosArquivosDisco;

    public SincronizarModulos(IGerenciadorModulosArquivosDisco IGerenciadorModulosArquivosDisco) {
        this.iGerenciadorModulosArquivosDisco = IGerenciadorModulosArquivosDisco;
    }

    public boolean existeModuloParaSincronizar(String caminhoOrigem, String caminhoExistentes) {
        Map<String, Modulo> novosModulos = obterModulosNovos(
                iGerenciadorModulosArquivosDisco.obterModuloOrigem(caminhoOrigem),
                iGerenciadorModulosArquivosDisco.obterModulosFuncoes(caminhoExistentes)
        );
        return !novosModulos.isEmpty();
    }

    public void executarSincronizacao(String caminhoOrigem, String caminhoExistentes) {
        Map<String, Modulo> novosModulos = obterModulosNovos(
                iGerenciadorModulosArquivosDisco.obterModuloOrigem(caminhoOrigem),
                iGerenciadorModulosArquivosDisco.obterModulosFuncoes(caminhoExistentes)
        );
        criarNovoModulo(novosModulos, caminhoExistentes);
    }

    public void criarNovoModulo(Map<String, Modulo> modulosNovos, String caminhoExistentes) {
        for (Modulo modulo : modulosNovos.values()) {
            String nome = modulo.getNome();
            String caminhoCompleto = Paths.get(caminhoExistentes, nome).toString();
            iGerenciadorModulosArquivosDisco.salvarModuloFuncao(caminhoCompleto);
        }
    }

    public void criarNovaFuncao(String modulo, String funcao, String caminhoExistentes) {
        String caminhoCompleto = Paths.get(caminhoExistentes, modulo, funcao).toString();
        iGerenciadorModulosArquivosDisco.salvarModuloFuncao(caminhoCompleto);

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
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }
}
