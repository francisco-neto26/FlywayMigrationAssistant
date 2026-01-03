package com.supergestao.FlywayMigrationAssistant.config;

import java.util.prefs.Preferences;

public class GerenciamentoTemaPadrao {
    private static final String PREF_TEMA = "caminho_tema";
    private static final Preferences prefs = Preferences.userNodeForPackage(GerenciamentoDiretorioPadrao.class);

    public static void salvaTema(String nomeTema) {
        prefs.put(PREF_TEMA, nomeTema);
    }

    public static String carregaTema() {
        return prefs.get(PREF_TEMA, "Sistema");
    }
}
