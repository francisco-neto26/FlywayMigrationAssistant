package com.supergestao.Flyway.migration.assistant.dominio.configuracao;

import atlantafx.base.theme.Theme;

import java.util.List;

public interface IGerenciadorConfiguracao {

    String getDiretorioModulo();

    String getDiretorioArquivo();

    Theme getTema();

    List<Theme> getListaTema();

    String getChaveFonte();

    boolean getChaveUsaModulo();

    boolean diretoriosConfigurados();

    void salvarDiretorioModulo(String caminho);

    void salvarDiretorioArquivo(String caminho);

    void salvarTema(String tema);

    void salvarChaveFonte(String fonte);

    void salvarChaveUsaModulo(String usa_modulo);

}
