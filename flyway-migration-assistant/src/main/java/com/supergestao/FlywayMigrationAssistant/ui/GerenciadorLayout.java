package com.supergestao.FlywayMigrationAssistant.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class GerenciadorLayout {
    private JSplitPane divisaoPrincipal;
    private JSplitPane divisaoCentralEsquerda;
    private JSplitPane divisaoCentral;
    private JLabel statusBar;

    public JPanel montarEstruturaCompleta(Component modulo, Component arquivos, Component sql, Component migration) {

        divisaoCentral = new JSplitPane(JSplitPane.VERTICAL_SPLIT, arquivos, sql);
        divisaoCentral.setDividerLocation(300);
        divisaoCentral.setResizeWeight(0.5);

        divisaoCentralEsquerda = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, modulo, divisaoCentral);
        divisaoCentralEsquerda.setDividerLocation(250);

        divisaoPrincipal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, divisaoCentralEsquerda, migration);
        divisaoPrincipal.setDividerLocation(900);
        divisaoPrincipal.setResizeWeight(0.8);

        JPanel containerPrincipal = new JPanel(new BorderLayout(10, 10));
        containerPrincipal.setBorder(new EmptyBorder(10, 10, 10, 10));
        containerPrincipal.add(divisaoPrincipal, BorderLayout.CENTER);

        return containerPrincipal;
    }

    public JLabel criarBarraStatus(String mensagem) {
        statusBar = new JLabel(mensagem);
        statusBar.setBorder(new EmptyBorder(5, 5, 5, 5));
        return statusBar;
    }

    public void atualizarStatus(String mensagem) {
        if (statusBar != null) {
            statusBar.setText(mensagem);
        }
    }

    public void sincronizarUI(JFrame frame) {
        int posPrincipal = divisaoPrincipal.getDividerLocation();
        int posEsqCentro = divisaoCentralEsquerda.getDividerLocation();
        int posCentral = divisaoCentral.getDividerLocation();
        int estadoJanela = frame.getExtendedState();

        SwingUtilities.updateComponentTreeUI(frame);

        frame.setExtendedState(estadoJanela);

        SwingUtilities.invokeLater(() -> {
            divisaoPrincipal.setDividerLocation(posPrincipal);
            divisaoCentralEsquerda.setDividerLocation(posEsqCentro);
            divisaoCentral.setDividerLocation(posCentral);
        });
    }
}