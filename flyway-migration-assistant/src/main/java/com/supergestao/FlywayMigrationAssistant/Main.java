package com.supergestao.FlywayMigrationAssistant;

import com.supergestao.FlywayMigrationAssistant.ui.TelaInicial;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            //UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            //FlatLightLaf.setup();
            //FlatDarkLaf.setup();
            //UIManager.setLookAndFeel(new MaterialLookAndFeel());
            //WebLookAndFeel.install();
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            TelaInicial window = new TelaInicial();
            window.setVisible(true);
        });
    }
}