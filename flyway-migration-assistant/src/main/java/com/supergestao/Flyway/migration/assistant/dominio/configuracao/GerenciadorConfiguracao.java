package com.supergestao.Flyway.migration.assistant.dominio.configuracao;

import atlantafx.base.theme.*;

import java.util.List;
import java.util.prefs.Preferences;

public class GerenciadorConfiguracao {

    // Caminho no Regedit: HKEY_CURRENT_USER\Software\JavaSoft\Prefs\com\supergestao\flywayassistant
    private static final Preferences prefs = Preferences.userRoot().node("com/supergestao/flywayassistant");

    private static final String CHAVE_DIRETORIO_MODULO = "diretorio_modulo";
    private static final String CHAVE_DIRETORIO_ARQUIVO = "diretorio_arquivo";
    private static final String CHAVE_FONTE = "fonte_sitema";
    private static final String CHAVE_USA_MODULO = "fonte_usa_modulo";
    private static final String CHAVE_TEMA = "tema_sistema";
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
        prefs.put(CHAVE_TEMA, tema);
    }

    public static Theme getTema() {
        String temaSalvo = prefs.get(CHAVE_TEMA, "Primer Light");

        for (Theme tema : CHAVE_TEMAS_DISPONIVEIS) {
            if (tema.getName().equals(temaSalvo)) {
                return tema;
            }
        }
        return new PrimerLight();
    }

    public static List<Theme> getListaTema() {
        return CHAVE_TEMAS_DISPONIVEIS;
    }

    public static String getChaveFonte() {
        return prefs.get(CHAVE_FONTE, "Segoe UI");
    }

    public static void salvarChaveFonte(String fonte) {
        prefs.put(CHAVE_FONTE, fonte);
    }

    public static boolean getChaveUsaModulo() {
        return prefs.get(CHAVE_USA_MODULO, "Sim").equalsIgnoreCase("Sim");
    }

    public static void salvarChaveUsaModulo(String usa_modulo) {
        prefs.put(CHAVE_USA_MODULO, usa_modulo);
    }

    public static boolean diretoriosConfigurados(){
        return (!getDiretorioModulo().isEmpty() || getChaveUsaModulo()) && !getDiretorioArquivo().isEmpty();
    }

}

