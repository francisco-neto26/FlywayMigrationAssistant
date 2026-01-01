package com.supergestao.FlywayMigrationAssistant.service;

import com.supergestao.FlywayMigrationAssistant.config.ModuloConfig;
import com.supergestao.FlywayMigrationAssistant.model.Arquivo;
import com.supergestao.FlywayMigrationAssistant.model.Tipo;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ArquivoService {
    private File pastaRaiz;

    public File getpastaRaiz() {
        return pastaRaiz;
    }

    public void setpastaRaiz(File pastaRaiz) {
        this.pastaRaiz = pastaRaiz;
    }

    public List<String> getModulos() {
        List<String> modulos = new ArrayList<>();

        if (pastaRaiz != null && pastaRaiz.exists()) {
            File[] subdirs = pastaRaiz.listFiles(File::isDirectory);
            if (subdirs != null) {
                for (File dir : subdirs) {
                    modulos.add(dir.getName());
                }
                modulos.sort(String::compareTo);
            }
        }

        return modulos;
    }

    public List<Arquivo> getFilesInModule(String moduleName) {
        List<Arquivo> files = new ArrayList<>();

        if (pastaRaiz == null) {
            return files;
        }

        File moduleFolder = new File(pastaRaiz, moduleName);
        if (!moduleFolder.exists() || !moduleFolder.isDirectory()) {
            return files;
        }

        File[] sqlFiles = moduleFolder.listFiles((dir, name) -> name.endsWith(".sql"));
        if (sqlFiles != null) {
            Arrays.sort(sqlFiles, Comparator.comparing(File::getName));

            for (File file : sqlFiles) {
                Tipo type = file.getName().startsWith("V") ?
                        Tipo.VERSIONED : Tipo.REPEATABLE;
                files.add(new Arquivo(file.getName(), moduleName, file, type));
            }
        }

        return files;
    }

    public String lerConteudoArquivo(File file) throws IOException {
        if (file == null || !file.exists()) {
            throw new IOException("Arquivo não existe");
        }
        return Files.readString(file.toPath(), StandardCharsets.UTF_8);
    }

    public void createArquivo(String moduleName, String fileName,
                                    String content) throws IOException {
        if (pastaRaiz == null) {
            throw new IOException("Pasta raiz não definida");
        }

        File moduleFolder = new File(pastaRaiz, moduleName);
        if (!moduleFolder.exists()) {
            moduleFolder.mkdirs();
        }

        File newFile = new File(moduleFolder, fileName);
        Files.writeString(newFile.toPath(), content, StandardCharsets.UTF_8);
    }

    public boolean fileExists(String moduleName, String fileName) {
        if (pastaRaiz == null) {
            return false;
        }

        File moduleFolder = new File(pastaRaiz, moduleName);
        File file = new File(moduleFolder, fileName);
        return file.exists();
    }

    public boolean createModule(String moduleName, String prefix) throws IOException {
        if (pastaRaiz == null) {
            throw new IOException("Pasta raiz não definida");
        }

        File moduleFolder = new File(pastaRaiz, moduleName);
        if (moduleFolder.exists()) {
            return false;
        }

        boolean created = moduleFolder.mkdirs();
        if (created && prefix != null && !prefix.isEmpty()) {
            ModuloConfig.addModule(moduleName, prefix);
        }

        return created;
    }

}
