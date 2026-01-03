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
            File[] subdiretorio = pastaRaiz.listFiles(File::isDirectory);
            if (subdiretorio != null) {
                for (File dir : subdiretorio) {
                    modulos.add(dir.getName());
                }
                modulos.sort(String::compareTo);
            }
        }

        return modulos;
    }

    public List<Arquivo> obterArquivosModulo(String nomeModulo) {
        List<Arquivo> arquivos = new ArrayList<>();

        if (pastaRaiz == null) {
            return arquivos;
        }

        File pastaModulo = new File(pastaRaiz, nomeModulo);
        if (!pastaModulo.exists() || !pastaModulo.isDirectory()) {
            return arquivos;
        }

        File[] arquivoSql = pastaModulo.listFiles((dir, nome) -> nome.endsWith(".sql"));
        if (arquivoSql != null) {
            Arrays.sort(arquivoSql, Comparator.comparing(File::getName));

            for (File arquivo : arquivoSql) {
                Tipo tipo = arquivo.getName().startsWith("V") ?
                        Tipo.VERSIONED : Tipo.REPEATABLE;
                arquivos.add(new Arquivo(arquivo.getName(), nomeModulo, arquivo, tipo));
            }
        }

        return arquivos;
    }

    public String lerConteudoArquivo(File arquivo) throws IOException {
        if (arquivo == null || !arquivo.exists()) {
            throw new IOException("Arquivo não existe");
        }
        return Files.readString(arquivo.toPath(), StandardCharsets.UTF_8);
    }

    public void criarArquivo(String nomeModulo, String nomeArquivo,
                                    String conteudo) throws IOException {
        if (pastaRaiz == null) {
            throw new IOException("Pasta raiz não definida");
        }

        File pastaModulo = new File(pastaRaiz, nomeModulo);
        if (!pastaModulo.exists()) {
            pastaModulo.mkdirs();
        }

        File novoArquivo = new File(pastaModulo, nomeArquivo);
        Files.writeString(novoArquivo.toPath(), conteudo, StandardCharsets.UTF_8);
    }

    public boolean existeArquivo(String nomeModulo, String nomeArquivo) {
        if (pastaRaiz == null) {
            return false;
        }

        File pastaModulo = new File(pastaRaiz, nomeModulo);
        File arquivo= new File(pastaModulo, nomeArquivo);
        return arquivo.exists();
    }

    public boolean criarModulo(String nomeModulo, String prefixo) throws IOException {
        if (pastaRaiz == null) {
            throw new IOException("Pasta raiz não definida");
        }

        File pastaModulo = new File(pastaRaiz, nomeModulo);
        if (pastaModulo.exists()) {
            return false;
        }

        boolean criado = pastaModulo.mkdirs();
        if (criado && prefixo != null && !prefixo.isEmpty()) {
            ModuloConfig.addModule(nomeModulo, prefixo);//precisa revisar para descontinuar o moduloConfig
        }

        return criado;
    }

}
