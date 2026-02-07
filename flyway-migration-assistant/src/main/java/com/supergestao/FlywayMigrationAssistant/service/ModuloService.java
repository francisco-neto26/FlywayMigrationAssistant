package com.supergestao.FlywayMigrationAssistant.service;

import com.supergestao.FlywayMigrationAssistant.model.Modulo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModuloService {
    public static Set<String> obterModulosMainEnums(String diretorioModulo, String diretorioMigration) {
        Set<Modulo> modulosCriar = new HashSet<>();
        Set<String> modulosCriarNome = new HashSet<>();
        Set<String> modulosExistentes = new HashSet<String>();
        try {

            //le todo o arquivo que esta no caminho de entrada
            String conteudo = Files.readString(Path.of(diretorioModulo));

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
        for (Modulo moduloRetorno : modulosCriar) {
            modulosCriarNome.add(moduloRetorno.getNome());
        }
        modulosCriarNome.stream()
                .sorted()
                .toList();
        modulosExistentes = obterModulos(diretorioMigration);

        return obterModulosCriar(modulosCriarNome, modulosExistentes);
    }

    public static Set<String> obterModulosCriar(Set<String> modulosNovos, Set<String> modulosExistentes){
        Set<String> modulosFaltantes = new HashSet<>(modulosNovos);
        modulosFaltantes.removeAll(modulosExistentes);
        return modulosFaltantes;
    }

    public static Set<String> obterModulos(String diretorioEntrada) {
        Set<String> modulos = new HashSet<>();
        File diretorioEntradaFile = new File(diretorioEntrada);
        File[] pastaModulos = diretorioEntradaFile.listFiles(File::isDirectory);
        if (pastaModulos != null) {
            for (File dir : pastaModulos) {
                modulos.add(dir.getName());
            }
            modulos.stream()
                    .sorted()
                    .toList();
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
