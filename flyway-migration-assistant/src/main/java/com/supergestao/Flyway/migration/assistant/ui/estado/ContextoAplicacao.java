package com.supergestao.Flyway.migration.assistant.ui.estado;

import atlantafx.base.theme.Theme;
import com.supergestao.Flyway.migration.assistant.aplicacao.sincronizar.SincronizarModulosArquivos;
import com.supergestao.Flyway.migration.assistant.dominio.configuracao.GerenciadorConfiguracao;
import com.supergestao.Flyway.migration.assistant.dominio.configuracao.IGerenciadorConfiguracao;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Arquivo;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Modulo;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Resultado;
import com.supergestao.Flyway.migration.assistant.persistencia.gerenciador.modulos.arquivos.IGerenciadorModulosArquivosDisco;
import com.supergestao.Flyway.migration.assistant.persistencia.gerenciador.modulos.arquivos.GerenciadorModulosArquivosDisco;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.janela.GerenciadorJanelas;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.janela.IGerenciadorJanelas;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.janela.TipoDialogo;

import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class ContextoAplicacao {
    private final IGerenciadorModulosArquivosDisco iGerenciadorModulosArquivosDisco;
    private final IGerenciadorConfiguracao iGerenciadorConfiguracao;
    private final IGerenciadorJanelas iGerenciadorJanelas;
    private final SincronizarModulosArquivos sincronizarModulosArquivos;

    public ContextoAplicacao() {
        this.iGerenciadorModulosArquivosDisco = new GerenciadorModulosArquivosDisco();
        this.iGerenciadorConfiguracao = new GerenciadorConfiguracao();
        this.iGerenciadorJanelas = new GerenciadorJanelas(this);
        this.sincronizarModulosArquivos = new SincronizarModulosArquivos(getIGerenciadorModulosArquivos());
    }

    private IGerenciadorJanelas getIGerenciadorJanelas() {
        return iGerenciadorJanelas;
    }

    private IGerenciadorModulosArquivosDisco getIGerenciadorModulosArquivos() {
        return this.iGerenciadorModulosArquivosDisco;
    }

    public String getDiretorioArquivo(){
        return this.iGerenciadorConfiguracao.getDiretorioArquivo();
    }

    public String getDiretorioModulo(){
        return this.iGerenciadorConfiguracao.getDiretorioModulo();
    }

    public String getChaveFonte(){
        return this.iGerenciadorConfiguracao.getChaveFonte();
    }

    public Theme getTema(){
        return this.iGerenciadorConfiguracao.getTema();
    }

    public List<Theme> getListaTema() {
        return this.iGerenciadorConfiguracao.getListaTema();
    }

    public boolean getChaveUsaModulo(){
        return this.iGerenciadorConfiguracao.getChaveUsaModulo();
    }

    public boolean getDiretoriosConfigurados(){
        return this.iGerenciadorConfiguracao.diretoriosConfigurados();
    }

    public void salvarDiretorioModulo(String diretorioModulo){
        this.iGerenciadorConfiguracao.salvarDiretorioModulo(diretorioModulo);
    }

    public void salvarDiretorioArquivo(String diretorioArquivo){
        this.iGerenciadorConfiguracao.salvarDiretorioArquivo(diretorioArquivo);
    }

    public void salvarTema(String tema){
        this.iGerenciadorConfiguracao.salvarTema(tema);
    }

    public void salvarChaveFonte(String fonte){
        this.iGerenciadorConfiguracao.salvarChaveFonte(fonte);
    }

    public void salvarChaveUsaModulo(String usaModulo){
        this.iGerenciadorConfiguracao.salvarChaveUsaModulo(usaModulo);
    }

    public boolean exibirDialogo(TipoDialogo tipoDialogo, String titulo, String mensagem, String detalhes){
        return this.getIGerenciadorJanelas().exibirDialogo(tipoDialogo, titulo, mensagem, detalhes);
    }

    public Map<String, Modulo> moduloParaSincronizar(String caminhoOrigem, String caminhoExistentes) {
        return this.sincronizarModulosArquivos.moduloParaSincronizar(caminhoOrigem, caminhoExistentes);
    }

    public List<Resultado> criarNovoModulo(Map<String, Modulo> modulosNovos, String caminhoExistentes) {
        return this.sincronizarModulosArquivos.criarNovoModulo(modulosNovos, caminhoExistentes);
    }

    public List<Resultado> criarModuloFuncao(String modulo, String funcao, String caminhoCompleto) {
        String diretorioCompleto = "";
        if (funcao == null){
            diretorioCompleto = Paths.get(caminhoCompleto, modulo).toString();
        }else{
            diretorioCompleto = Paths.get(caminhoCompleto, modulo, funcao).toString();
        }
        String moduloFuncao = funcao == null ? modulo : funcao ;
        return this.sincronizarModulosArquivos.criarModuloFuncao(moduloFuncao, diretorioCompleto);
    }

    public Map<String, Modulo> obterModulosExistentes(String caminhoExistentes){
        return this.sincronizarModulosArquivos.obterModulosExistentes(caminhoExistentes);
    }

    public Map<String, Modulo> obterModulosNovos(Map<String, Modulo> moduloOrigem, Map<String, Modulo> modulosExistentes) {
        return this.sincronizarModulosArquivos.obterModulosNovos(moduloOrigem, modulosExistentes);
    }

    public HashSet<Arquivo> carregarArquivos(String caminhoFuncao, String nomeModulo, String nomeFuncao){
        return this.sincronizarModulosArquivos.carregarArquivos(caminhoFuncao, nomeModulo, nomeFuncao);
    }

    public boolean temFuncaoArquivo(String diretorioRaiz, String nomeModulo, String nomeFuncao){
        return this.sincronizarModulosArquivos.temFuncaoArquivo(diretorioRaiz, nomeModulo, nomeFuncao);
    }

    public String buscarConteudoArquivo(String caminhoArquivo) {
        return this.sincronizarModulosArquivos.buscarConteudoArquivo(caminhoArquivo);
    }

    public void salvarArquivo(String caminhoDoArquivo, String conteudoSQL) {
        this.sincronizarModulosArquivos.salvarArquivo(caminhoDoArquivo, conteudoSQL);
    }




}

