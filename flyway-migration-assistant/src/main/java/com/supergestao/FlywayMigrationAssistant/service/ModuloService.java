package com.supergestao.FlywayMigrationAssistant.service;

import com.supergestao.FlywayMigrationAssistant.model.Modulo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModuloService {

    private File pastaRaiz;

    public File getpastaRaiz() {
        return pastaRaiz;
    }

    public void setpastaRaiz(File pastaRaiz) {
        this.pastaRaiz = pastaRaiz;
    }

    public List<Modulo> extrairModulosExternos(String caminhoArqEnumModuloJava) {
        List<Modulo> modulosACriar = new ArrayList<>();
        List<String> modulosExistentes = getModulos();

        try {
            //le todo o arquivo que esta no caminho de entrada
            String conteudo = Files.readString(Path.of(caminhoArqEnumModuloJava));

            // Regex explicada:
            // (\\w+)             -> Grupo 1: Constante (ADMINISTRACAO)
            // (\\d+)L?           -> Grupo 2: ID numérico, ignora o 'L' se houver (1)
            // \"([^\"]+)\"       -> Grupo 3: Nome ("Administração")
            // \"([^\"]+)\"       -> Grupo 4: Prefixo ("Adm")
            // \"([^\"]+)\"       -> Grupo 5: Descrição ("Gerenciamento...")
            Pattern pattern = Pattern.compile("(\\w+)\\s*\\(\\s*(\\d+)L?\\s*,\\s*\"([^\"]+)\"\\s*,\\s*\"([^\"]+)\"\\s*,\\s*\"([^\"]+)\"\\s*\\)");
            Matcher matcher = pattern.matcher(conteudo);

            while (matcher.find()) {
                String constante = matcher.group(1);
                Long id = Long.valueOf(matcher.group(2)); // Converte o ID para Long
                String nome = matcher.group(3);
                String prefixo = matcher.group(4);
                String descricao = matcher.group(5);

                modulosACriar.add(new Modulo(
                        id,
                        nome,
                        constante,
                        prefixo,
                        descricao
                ));

                if (modulosExistentes.contains(constante)) {
                    criarPastaModulo(constante);
                }

            }

        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo externo: " + e.getMessage());
        }

        return modulosACriar;
    }

    public List<String> getModulos() {
        List<String> modulos = new ArrayList<>();

        if (pastaRaiz != null && pastaRaiz.exists()) {
            File[] pastaModulos = pastaRaiz.listFiles(File::isDirectory);
            if (pastaModulos != null) {
                for (File dir : pastaModulos) {
                    modulos.add(dir.getName());
                }
                modulos.sort(String::compareTo);
            }
        }

        return modulos;
    }

    public boolean criarPastaModulo(String nomePastaACriar) {

        if (pastaRaiz == null || !pastaRaiz.exists()) {
            System.err.println("Erro: Pasta raiz não configurada ou não existe.");
        }
        nomePastaACriar = nomePastaACriar.toUpperCase();

        // Criamos o objeto File apontando para a nova subpasta
        File novaPasta = new File(this.pastaRaiz, nomePastaACriar);

        if (!novaPasta.exists()) {
            // .mkdirs() cria a pasta e também as pastas pai, se necessário
            boolean criado = novaPasta.mkdirs();
            if (criado) {
                System.out.println("Diretório criado com sucesso: " + novaPasta.getAbsolutePath());
                return true;
            } else {
                System.err.println("Falha ao criar o diretório: " + novaPasta);
                return false;
            }

        } else {
            return true;
        }
    }
}
