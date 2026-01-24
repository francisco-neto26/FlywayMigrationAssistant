package com.supergestao.FlywayMigrationAssistant.service;

import com.supergestao.FlywayMigrationAssistant.model.Modulo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModuloService {

    private File pastaRaizModulosNovos;
    private File pastaRaizModulosExistentes;

    public File getpastaRaizModulosNovos() {
        return pastaRaizModulosNovos;
    }

    public void setpastaRaizModulosNovos(File pastaRaiz) {
        this.pastaRaizModulosNovos = pastaRaiz;
    }

    public File getPastaRaizModulosExistentes() {
        return pastaRaizModulosExistentes;
    }

    public void setPastaRaizModulosExistentes(File pastaRaizModulosExistentes) {
        this.pastaRaizModulosExistentes = pastaRaizModulosExistentes;
    }

    public Set<String> obterModulosMainEnums(String diretorioEntrada) {
        Set<Modulo> modulosCriar = new HashSet<>();
        Set<String> modulosCriarNome = new HashSet<>();
        try {

            //le todo o arquivo que esta no caminho de entrada
            String conteudo = Files.readString(Path.of(diretorioEntrada));

            // Regex explicada:
            // (\\w+)             -> Grupo 1: Constante
            // (\\d+)L?           -> Grupo 2: ID numérico, ignora o 'L' se houver (1)
            // \"([^\"]+)\"       -> Grupo 3: Nome
            // \"([^\"]+)\"       -> Grupo 4: Prefixo
            // \"([^\"]+)\"       -> Grupo 5: Descrição
            Pattern pattern = Pattern.compile("(\\w+)\\s*\\(\\s*(\\d+)L?\\s*,\\s*\"([^\"]+)\"\\s*,\\s*\"([^\"]+)\"\\s*,\\s*\"([^\"]+)\"\\s*\\)");
            Matcher matcher = pattern.matcher(conteudo);

            while (matcher.find()) {
                String constante = matcher.group(1);
                Long id = Long.valueOf(matcher.group(2)); // Converte o ID para Long
                String nome = matcher.group(3);
                String prefixo = matcher.group(4);
                String descricao = matcher.group(5);

                modulosCriar.add(new Modulo(
                        id,
                        nome,
                        constante,
                        prefixo,
                        descricao
                ));
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo externo: " + e.getMessage());
        }
        for (Modulo moduloRetorno : modulosCriar){
            modulosCriarNome.add(moduloRetorno.getNome());
        }
        return modulosCriarNome;
    }

    public List<String> obterModulos(String diretorioEntrada) {
        List<String> modulos = new ArrayList<>();
        File diretorioEntradaFile = new File(diretorioEntrada);
        File[] pastaModulos = diretorioEntradaFile.listFiles(File::isDirectory);
        if (pastaModulos != null) {
            for (File dir : pastaModulos) {
                modulos.add(dir.getName());
            }
            modulos.sort(String::compareTo);
        }
        return modulos;
    }

    public String criarModulo(String nomeModulo, String diretorioModulo) {
        String validado = "";
        try {
            Path caminhoCompleto = Paths.get(diretorioModulo, nomeModulo);
            if (Files.notExists(caminhoCompleto)) {
                Files.createDirectories(caminhoCompleto);
                validado = "true";
            }
        } catch (IOException e) {
            validado = e.getMessage();
        }
        return validado;
    }
}
