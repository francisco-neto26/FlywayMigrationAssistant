package com.supergestao.Flyway.migration.assistant.ui.estado;

import com.supergestao.Flyway.migration.assistant.aplicacao.sincronizar.SincronizarModulos;
import com.supergestao.Flyway.migration.assistant.dominio.configuracao.GerenciadorConfiguracao;
import com.supergestao.Flyway.migration.assistant.dominio.configuracao.IGerenciadorConfiguracao;
import com.supergestao.Flyway.migration.assistant.persistencia.gerenciador.modulos.arquivos.IGerenciadorModulosArquivosDisco;
import com.supergestao.Flyway.migration.assistant.persistencia.gerenciador.modulos.arquivos.GerenciadorModulosArquivosDisco;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.GerenciadorJanelas;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.IGerenciadorJanelas;

public class ContextoAplicacao {
    private final IGerenciadorModulosArquivosDisco iGerenciadorModulosArquivosDisco;
    private final IGerenciadorConfiguracao iGerenciadorConfiguracao;
    private final IGerenciadorJanelas iGerenciadorJanelas;
    private final SincronizarModulos sincronizarModulos;

    public ContextoAplicacao() {
        this.iGerenciadorModulosArquivosDisco = new GerenciadorModulosArquivosDisco();
        this.iGerenciadorConfiguracao = new GerenciadorConfiguracao();
        this.iGerenciadorJanelas = new GerenciadorJanelas(this);
        this.sincronizarModulos = new SincronizarModulos(getIGerenciadorModulosArquivos());
    }

    public IGerenciadorModulosArquivosDisco getIGerenciadorModulosArquivosDisco() {
        return iGerenciadorModulosArquivosDisco;
    }

    public IGerenciadorConfiguracao getIGerenciadorConfiguracao() {
        return iGerenciadorConfiguracao;
    }

    public IGerenciadorJanelas getIGerenciadorJanelas() {
        return iGerenciadorJanelas;
    }

    public IGerenciadorModulosArquivosDisco getIGerenciadorModulosArquivos() {
        return this.iGerenciadorModulosArquivosDisco;
    }

    public SincronizarModulos getSincronizarModulos() {
        return this.sincronizarModulos;
    }

    public String getDiretorioArquivo(){
        return this.iGerenciadorConfiguracao.getDiretorioArquivo();
    }

    public String getDiretorioModulo(){
        return this.iGerenciadorConfiguracao.getDiretorioModulo();
    }

}

