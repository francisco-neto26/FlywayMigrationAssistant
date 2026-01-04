package com.supergestao.FlywayMigrationAssistant;

import com.supergestao.FlywayMigrationAssistant.config.GerenciamentoTemaPadrao;
import com.supergestao.FlywayMigrationAssistant.service.TemaService;
import com.supergestao.FlywayMigrationAssistant.ui.TelaInicial;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            String temaSalvo = GerenciamentoTemaPadrao.carregaTema();
            TemaService.configurarLookAndFeel(temaSalvo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            TelaInicial window = new TelaInicial();
            window.setVisible(true);
        });
    }
}