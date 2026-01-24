package com.supergestao.FlywayMigrationAssistant.config;

import javax.swing.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public class GerenciamentoDiretorioPadrao {
    private static final String PREF_PASTA_MIGRATION = "caminho_pasta_migracao";
    private static final String PREF_PASTA_MODULO = "caminho_pasta_modulo";
    private static final Preferences prefs = Preferences.userNodeForPackage(GerenciamentoDiretorioPadrao.class);

    public static void salvaDiretorio(String caminho, String opcao) {
        if (caminho != null && !caminho.isEmpty()) {
            if (opcao.equalsIgnoreCase("Migration")) {
                prefs.put(PREF_PASTA_MIGRATION, caminho);
            } else if (opcao.equalsIgnoreCase("Modulo")) {
                prefs.put(PREF_PASTA_MODULO, caminho);
            }
        }
    }

    public static String obterDiretorioSalvo(String opcao) {
        String caminho = "";
        if (opcao.equalsIgnoreCase("Migration")) {
            caminho = prefs.get(PREF_PASTA_MIGRATION, null);
        } else if (opcao.equalsIgnoreCase("Modulo")) {
            caminho = prefs.get(PREF_PASTA_MODULO, null);
        }
        return caminho;
    }

    public static boolean registroRegEditExistente() {
        try {
            String[] chavesExistentes = prefs.keys();
            List<String> listaChaves = Arrays.asList(chavesExistentes);
            boolean temMigration = listaChaves.contains(PREF_PASTA_MIGRATION);
            boolean temModulo = listaChaves.contains(PREF_PASTA_MODULO);
            return temMigration && temModulo;
        } catch (BackingStoreException e) {
            e.printStackTrace();
            return false;
        }
    }


    public static String carregaPastaRaiz() {
        String caminho = prefs.get(PREF_PASTA_MIGRATION, null);
        return caminho;
    }

    public static void limparPastaRaiz() {
        prefs.remove(PREF_PASTA_MIGRATION);
    }

}
