package com.supergestao.FlywayMigrationAssistant.config;

import java.io.File;
import java.util.prefs.Preferences;

public class GerenciamentoDiretorioPadrao {
    private static final String PREF_PASTA_RAIZ = "caminho_pasta_migracao";
    private static final Preferences prefs = Preferences.userNodeForPackage(GerenciamentoDiretorioPadrao.class);

    public static void salvaPastaRaiz(String caminho) {
        if (caminho != null && !caminho.isEmpty()) {
            prefs.put(PREF_PASTA_RAIZ, caminho);
            System.out.println("Pasta raiz salva nas preferências: " + caminho);
        }
    }

    public static String carregaPastaRaiz() {
        String caminho = prefs.get(PREF_PASTA_RAIZ, null);
        if (caminho != null) {
            System.out.println("Pasta raiz carregada das preferências: " + caminho);
        }
        return caminho;
    }

    public static boolean possuiPastaRaiz() {
        return carregaPastaRaiz() != null;
    }

    public static void limparPastaRaiz() {
        prefs.remove(PREF_PASTA_RAIZ);
        System.out.println("Pasta raiz removida das preferências");
    }

    public static File obterPastaRaizComArquivo() {
        String caminho = carregaPastaRaiz();
        if (caminho != null) {
            File arquivo = new File(caminho);
            if (arquivo.exists() && arquivo.isDirectory()) {
                return arquivo;
            } else {
                System.out.println("Pasta salva não existe mais: " + caminho);
                limparPastaRaiz();
            }
        }
        return null;
    }
}
