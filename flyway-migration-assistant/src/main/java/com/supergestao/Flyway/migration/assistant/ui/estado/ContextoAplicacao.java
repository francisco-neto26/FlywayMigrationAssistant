package com.supergestao.Flyway.migration.assistant.ui.estado;

import atlantafx.base.theme.Theme;
import com.supergestao.Flyway.migration.assistant.persistencia.gerenciador.modulos.arquivos.GerenciadorModulosArquivos;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.Mensageiro;

import java.util.List;

public class ContextoAplicacao {
    private final String diretorioModulos;
    private final String diretorioArquivos;
    private final String tema;
    private final List<Theme> temasDisponiveis;
    private final GerenciadorModulosArquivos gerenciadorModulosArquivos;
    private final Mensageiro mensageiro;

    public ContextoAplicacao(String diretorioModulos,
                             String diretorioArquivos,
                             String tema,
                             List<Theme> temasDisponiveis,
                             GerenciadorModulosArquivos gerenciadorModulosArquivos,
                             Mensageiro mensageiro) {
        this.diretorioModulos = diretorioModulos;
        this.diretorioArquivos = diretorioArquivos;
        this.tema = tema;
        this.temasDisponiveis = temasDisponiveis;
        this.gerenciadorModulosArquivos = gerenciadorModulosArquivos;
        this.mensageiro = mensageiro;
    }

    public String getDiretorioModulos() {
        return diretorioModulos;
    }

    public String getDiretorioArquivos() {
        return diretorioArquivos;
    }

    public String getTema() {
        return tema;
    }

    public List<Theme> getTemasDisponiveis() {
        return temasDisponiveis;
    }

    public GerenciadorModulosArquivos getGerenciadorModulosArquivos() {
        return gerenciadorModulosArquivos;
    }

    public Mensageiro getMensageiro() {
        return mensageiro;
    }
}