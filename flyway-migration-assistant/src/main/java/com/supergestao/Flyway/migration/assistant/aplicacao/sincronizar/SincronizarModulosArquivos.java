package com.supergestao.Flyway.migration.assistant.aplicacao.sincronizar;

import com.supergestao.Flyway.migration.assistant.aplicacao.buscar.BuscarArquivos;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Arquivo;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Modulo;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Resultado;
import com.supergestao.Flyway.migration.assistant.persistencia.gerenciador.modulos.arquivos.IGerenciadorModulosArquivosDisco;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class SincronizarModulosArquivos {

    private final IGerenciadorModulosArquivosDisco iGerenciadorModulosArquivosDisco;

    public SincronizarModulosArquivos(IGerenciadorModulosArquivosDisco IGerenciadorModulosArquivosDisco) {
        this.iGerenciadorModulosArquivosDisco = IGerenciadorModulosArquivosDisco;
    }

    public Map<String, Modulo> moduloParaSincronizar(String caminhoOrigem, String caminhoExistentes) {
        return obterModulosNovos(
                iGerenciadorModulosArquivosDisco.obterModuloOrigem(caminhoOrigem),
                iGerenciadorModulosArquivosDisco.obterModulosFuncoes(caminhoExistentes)
        );
    }

    public List<Resultado> criarNovoModulo(Map<String, Modulo> modulosNovos, String caminhoExistentes) {
        List<Resultado> resultados = new ArrayList<>();
        for (Modulo modulo : modulosNovos.values()) {
            String nome = modulo.getNome();
            String caminhoCompleto = Paths.get(caminhoExistentes, nome).toString();
            resultados.addAll(salvarModuloFuncao(nome, caminhoCompleto));
        }
        return resultados;
    }

    public List<Resultado> criarModuloFuncao(String moduloFuncao, String caminhoCompleto) {
        return salvarModuloFuncao(moduloFuncao, caminhoCompleto);
    }

    private List<Resultado> salvarModuloFuncao(String moduloFuncao, String caminhoCompleto) {
        List<Resultado> resultados = new ArrayList<>();
        boolean criado = iGerenciadorModulosArquivosDisco.salvarModuloFuncao(caminhoCompleto);
        resultados.add(new Resultado(criado, moduloFuncao, null));
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

    public String buscarConteudoArquivo(String caminho) {
        return iGerenciadorModulosArquivosDisco.buscarConteudoArquivo(caminho);
    }

    public void salvarArquivo(String caminhoDoArquivo, String conteudoSQL) {
        this.iGerenciadorModulosArquivosDisco.salvarArquivo(caminhoDoArquivo, conteudoSQL);
    }

    public Map<String, Map<String, Set<String>>> buscarArquivosPorTermo(String diretorioRaiz, String termoBusca) {
        Map<String, Map<String, Set<String>>> resultados = new HashMap<>();
        if (diretorioRaiz == null || diretorioRaiz.isEmpty()) {
            return resultados;
        }
        File pastaRaiz = new File(diretorioRaiz);
        if (pastaRaiz.exists() && pastaRaiz.isDirectory()) {
            varrerDiretorioBusca(pastaRaiz, pastaRaiz, termoBusca.toLowerCase().trim(), resultados);
        }
        return resultados;
    }
    private void varrerDiretorioBusca(File raiz, File dirAtual, String termoBusca, Map<String, Map<String, Set<String>>> resultados) {
        File[] arquivos = dirAtual.listFiles();
        if (arquivos == null) {
            return;
        }
        for (File arquivo : arquivos) {
            if (arquivo.isDirectory()) {
                varrerDiretorioBusca(raiz, arquivo, termoBusca, resultados);
            } else if (arquivo.isFile() && arquivo.getName().toLowerCase().endsWith(".sql")) {
                if (arquivo.getName().toLowerCase().contains(termoBusca)) {
                    String caminhoRelativo = raiz.toURI().relativize(arquivo.toURI()).getPath();
                    caminhoRelativo = caminhoRelativo.replace("\\", "/");
                    String[] partes = caminhoRelativo.split("/");
                    if (partes.length >= 2) {
                        String nomeModulo = partes[0];
                        String nomeFuncao = partes.length == 3 ? partes[1] : "";
                        String nomeArquivo = partes[partes.length - 1];
                        resultados.computeIfAbsent(nomeModulo, k -> new HashMap<>())
                                .computeIfAbsent(nomeFuncao, k -> new TreeSet<>())
                                .add(nomeArquivo);
                    }
                }
            }
        }
    }


}
