package com.supergestao.FlywayMigrationAssistant.service;

import com.supergestao.FlywayMigrationAssistant.config.GerenciamentoDiretorioPadrao;
import java.io.File;

public class DiretorioService {

    public boolean salvarConfiguracoes(String diretorioMigration, String diretorioModulos) {
        boolean sucessoMigration = salvarNovoDiretorio(diretorioMigration, "Migration");
        boolean sucessoModulo = salvarNovoDiretorio(diretorioModulos, "Modulo");
        return sucessoMigration && sucessoModulo;
    }

    public boolean salvarNovoDiretorio(String caminho, String opcao) {
        if (caminho == null || caminho.trim().isEmpty()) {
            return false;
        }
        File pasta = new File(caminho);
        if (pasta.exists() && pasta.isDirectory()) {
            GerenciamentoDiretorioPadrao.salvaDiretorio(caminho, opcao);
            return true;
        }
        return false;
    }

    public boolean validaDiretorios(){
        boolean validado = GerenciamentoDiretorioPadrao.registroRegEditExistente();
        return validado;
    }

    public String obterCaminhoRaizSalvo(String opcao) {
        String diretorioString = GerenciamentoDiretorioPadrao.obterDiretorioSalvo(opcao);

        if (diretorioString == null || diretorioString.trim().isEmpty()) {
            return "";
        }

        File pasta = new File(diretorioString);
        return (pasta != null) ? pasta.getAbsolutePath() : "";
    }

    public boolean validarEstruturaFlyway(File pasta) {
        if (pasta == null || !pasta.isDirectory()) return false;

        File migrationDir = new File(pasta, "db/migration");
        return migrationDir.exists();
    }
}
