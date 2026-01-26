package com.supergestao.FlywayMigrationAssistant.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class GerenciadorLayout {
    private static final int LARGURA_NAVEGACAO_ESQUERDA = 150;
    private static final int ALTURA_SUPERIOR_ESQUERDA = 400;
    private static final int DIVISAO_MODULO_ARQUIVO = 50;

    private JSplitPane divisaoPrincipal;
    private JSplitPane divisaoEsquerdaVertical;
    private JSplitPane divisaoNavHorizontal;
    private JLabel rotuloStatus;


    public JPanel montarEstruturaCompleta(Component painelModulo, Component painelArquivos,
                                          Component painelSql, Component painelCriacao) {

        divisaoNavHorizontal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, painelModulo, painelArquivos);
        divisaoNavHorizontal.setDividerLocation(DIVISAO_MODULO_ARQUIVO);
        divisaoNavHorizontal.setResizeWeight(0.3);
        estilizarSplitPane(divisaoNavHorizontal);

        divisaoEsquerdaVertical = new JSplitPane(JSplitPane.VERTICAL_SPLIT, divisaoNavHorizontal, painelCriacao);
        divisaoEsquerdaVertical.setDividerLocation(ALTURA_SUPERIOR_ESQUERDA);
        divisaoEsquerdaVertical.setResizeWeight(0.8);
        estilizarSplitPane(divisaoEsquerdaVertical);

        divisaoPrincipal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, divisaoEsquerdaVertical, painelSql);
        divisaoPrincipal.setDividerLocation(LARGURA_NAVEGACAO_ESQUERDA);
        divisaoPrincipal.setResizeWeight(1.0);
        estilizarSplitPane(divisaoPrincipal);

        return construirPainelContainer();
    }

    private JPanel construirPainelContainer() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBorder(new EmptyBorder(2, 2, 2, 2));
        container.add(divisaoPrincipal, BorderLayout.CENTER);
        return container;
    }

    private void estilizarSplitPane(JSplitPane split) {
        split.setBorder(null);
        split.setContinuousLayout(true);
        split.setOneTouchExpandable(true);
        split.setDividerSize(8);
    }

    public JLabel criarBarraStatus(String mensagem) {
        rotuloStatus = new JLabel(mensagem);
        rotuloStatus.setBorder(new EmptyBorder(5, 10, 5, 10));
        rotuloStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return rotuloStatus;
    }

    public void atualizarStatus(String texto) {
        if (rotuloStatus != null) {
            rotuloStatus.setText(texto);
        }
    }

    public void sincronizarUI(JFrame frame) {
        int d1 = divisaoPrincipal.getDividerLocation();
        int d2 = divisaoEsquerdaVertical.getDividerLocation();
        int d3 = divisaoNavHorizontal.getDividerLocation();

        SwingUtilities.updateComponentTreeUI(frame);

        SwingUtilities.invokeLater(() -> {
            divisaoPrincipal.setDividerLocation(d1);
            divisaoEsquerdaVertical.setDividerLocation(d2);
            divisaoNavHorizontal.setDividerLocation(d3);
        });
    }
}