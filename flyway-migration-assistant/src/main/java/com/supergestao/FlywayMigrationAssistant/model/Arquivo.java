package com.supergestao.FlywayMigrationAssistant.model;

import java.io.File;
import java.time.LocalDateTime;

public class Arquivo {
    private String nome;
    private String modulo;
    private File arquivo;
    private Tipo tipo;
    private LocalDateTime criacao;

    public Arquivo(String nome, String modulo, File arquivo, Tipo tipo) {
        this.nome = nome;
        this.modulo = modulo;
        this.arquivo = arquivo;
        this.tipo = tipo;
        this.criacao = LocalDateTime.now();
    }

    public String getnome() {
        return nome;
    }

    public void setnome(String nome) {
        this.nome = nome;
    }

    public String getmodulo() {
        return modulo;
    }

    public void setmodulo(String modulo) {
        this.modulo = modulo;
    }

    public File getarquivo() {
        return arquivo;
    }

    public void setarquivo(File arquivo) {
        this.arquivo = arquivo;
    }

    public Tipo gettipo() {
        return tipo;
    }

    public void settipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public LocalDateTime getcracao() {
        return criacao;
    }

    public void setcracao(LocalDateTime cracao) {
        this.criacao = cracao;
    }

    @Override
    public String toString() {
        return nome;
    }
}
