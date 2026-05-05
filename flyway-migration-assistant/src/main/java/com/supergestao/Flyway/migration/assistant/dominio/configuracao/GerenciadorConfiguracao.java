package com.supergestao.Flyway.migration.assistant.dominio.configuracao;

import atlantafx.base.theme.*;

import java.util.List;
import java.util.prefs.Preferences;

public class GerenciadorConfiguracao {

    // Caminho no Regedit: HKEY_CURRENT_USER\Software\JavaSoft\Prefs\com\supergestao\flywayassistant
    private static final Preferences prefs = Preferences.userRoot().node("com/supergestao/flywayassistant");

    private static final String CHAVE_DIRETORIO_MODULO = "DIRETORIO_MODULO";
    private static final String CHAVE_DIRETORIO_ARQUIVO = "DIRETORIO_ARQUIVO";
    public static final List<Theme> CHAVE_TEMAS_DISPONIVEIS = List.of(
            new PrimerLight(), new PrimerDark(),
            new CupertinoLight(), new CupertinoDark(),
            new NordLight(), new NordDark(), new Dracula()
    );

    public static void salvarDiretorioModulo(String caminho) {
        prefs.put(CHAVE_DIRETORIO_MODULO, caminho);
    }

    public static String getDiretorioModulo() {
        return prefs.get(CHAVE_DIRETORIO_MODULO, "");
    }

    public static void salvarDiretorioArquivo(String caminho) {
        prefs.put(CHAVE_DIRETORIO_ARQUIVO, caminho);
    }

    public static String getDiretorioArquivo() {
        return prefs.get(CHAVE_DIRETORIO_ARQUIVO, "");
    }

    public static void salvarTema(String tema) {
        prefs.put(CHAVE_TEMAS_DISPONIVEIS.toString(), tema);
    }

    public static String getTema() {
        return prefs.get(CHAVE_TEMAS_DISPONIVEIS.toString(), "Primer Claro (Padrão)");
    }

    public static void aplicarTema(Theme tema) {
        if (tema != null) {
            javafx.application.Application.setUserAgentStylesheet(tema.getUserAgentStylesheet());
        }
    }
}

