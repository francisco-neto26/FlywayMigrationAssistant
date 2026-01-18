package com.supergestao.FlywayMigrationAssistant.service;

import com.formdev.flatlaf.*;
import mdlaf.MaterialLookAndFeel;
import javax.swing.*;
import java.awt.*;

public class TemaService {

    public static void configurarLookAndFeel(String nomeTema) {
        try {
            switch (nomeTema) {
                case "Metal":
                    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                    break;
                case "Nimbus":
                    UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
                    break;
                case "Motif":
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
                    break;
                case "Flat Light":
                    FlatLightLaf.setup();
                    break;
                case "Flat Dark":
                    FlatDarkLaf.setup();
                    break;
                case "IntelliJ":
                    FlatIntelliJLaf.setup();
                    break;
                case "Darcula":
                    FlatDarculaLaf.setup();
                    break;
                default:
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    break;
            }
            aplicarRegrasMenu();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void aplicarRegrasMenu() {
        Object zero = 0;
        Insets margemPadrao = new Insets(6, 12, 6, 15);

        String temaNome = UIManager.getLookAndFeel().getName();
        boolean temaOldNome = temaNome.contains("Metal") || temaNome.contains("CDE/Motif");

        Color hoverColor;
        if (temaOldNome) {
            hoverColor = new Color(210, 210, 210);
        } else {

            hoverColor = new Color(0, 0, 0, 35);
        }
        String[] prefixos = {"MenuItem", "Menu", "CheckBoxMenuItem", "RadioButtonMenuItem"};
        for (String p : prefixos) {
            UIManager.put(p + ".checkIconOffset", zero);
            UIManager.put(p + ".minimumTextOffset", zero);
            UIManager.put(p + ".iconTextGap", zero);
            UIManager.put(p + ".margin", margemPadrao);
            UIManager.put(p + ".selectionBackground", hoverColor);
            UIManager.put(p + ".selectionForeground", UIManager.getColor(p + ".foreground"));
            UIManager.put(p + ".height", 30);

            if (temaOldNome) {
                UIManager.put(p + ".opaque", true);
            }
        }

        if (temaOldNome) {
            UIManager.put("MenuBar.opaque", true);
        }
    }
}