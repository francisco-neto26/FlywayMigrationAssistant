package com.supergestao.FlywayMigrationAssistant.service;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import mdlaf.MaterialLookAndFeel;

import javax.swing.*;

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
                case "Material":
                    UIManager.setLookAndFeel(new mdlaf.MaterialLookAndFeel());
                    break;
                default:
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
