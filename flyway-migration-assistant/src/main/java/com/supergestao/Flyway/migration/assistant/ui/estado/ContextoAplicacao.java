package com.supergestao.Flyway.migration.assistant.ui.estado;

import atlantafx.base.theme.Theme;
import com.supergestao.Flyway.migration.assistant.aplicacao.sincronizar.SincronizarModulos;
import com.supergestao.Flyway.migration.assistant.dominio.configuracao.GerenciadorConfiguracao;
import com.supergestao.Flyway.migration.assistant.persistencia.gerenciador.modulos.arquivos.GerenciadorModulosArquivos;
import com.supergestao.Flyway.migration.assistant.persistencia.gerenciador.modulos.arquivos.GerenciadorModulosArquivosDisco;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.GerenciadorJanelas;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.Mensageiro;

import java.util.List;

public class ContextoAplicacao {
    private final String diretorioModulos;
    private final String diretorioArquivos;
    private final String tema;
    private final List<Theme> temasDisponiveis;
    private final GerenciadorModulosArquivos gerenciadorModulosArquivos;
    private final Mensageiro mensageiro;
    private final SincronizarModulos sincronizarModulos;
    private final boolean diretoriosConfigurados;
    private final boolean usaModulo;

    public ContextoAplicacao() {
        this.diretorioModulos = GerenciadorConfiguracao.getDiretorioModulo();
        this.diretorioArquivos = GerenciadorConfiguracao.getDiretorioArquivo();
        this.tema = GerenciadorConfiguracao.getTema().getName();
        this.temasDisponiveis = GerenciadorConfiguracao.getListaTema();
        this.diretoriosConfigurados = GerenciadorConfiguracao.diretoriosConfigurados();
        this.usaModulo = GerenciadorConfiguracao.getChaveUsaModulo();
        this.gerenciadorModulosArquivos = new GerenciadorModulosArquivosDisco();
        this.mensageiro = new GerenciadorJanelas();
        this.sincronizarModulos = new SincronizarModulos(gerenciadorModulosArquivos);
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

    public boolean getDiretoriosConfigurados() {
        return diretoriosConfigurados;
    }

    public GerenciadorModulosArquivos getGerenciadorModulosArquivos() {
        return gerenciadorModulosArquivos;
    }

    public Mensageiro getMensageiro() {
        return mensageiro;
    }

    public SincronizarModulos getSincronizarModulos() {
        return sincronizarModulos;
    }

    public boolean getUsaModulo() {
        return usaModulo;
    }
}

