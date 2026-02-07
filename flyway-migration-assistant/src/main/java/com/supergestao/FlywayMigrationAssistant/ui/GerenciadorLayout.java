package com.supergestao.FlywayMigrationAssistant.ui;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;

public class GerenciadorLayout {
    private static final double PROPORCAO_LARGURA_ESQ = 0.00;
    private static final double PROPORCAO_ALTURA_ESQ = 0.72;

    private JSplitPane divisaoPrincipal;
    private JSplitPane divisaoEsquerdaVertical;
    private JLabel rotuloStatus;

    public JPanel montarEstruturaCompleta(Component explorador, Component sql, Component criacao) {
        explorador.setMinimumSize(new Dimension(250, 0));
        sql.setMinimumSize(new Dimension(500, 0));
        criacao.setMinimumSize(new Dimension(0, 180));

        divisaoEsquerdaVertical = new JSplitPane(JSplitPane.VERTICAL_SPLIT, explorador, criacao);
        estilizarSplitPane(divisaoEsquerdaVertical);
        divisaoEsquerdaVertical.setResizeWeight(PROPORCAO_ALTURA_ESQ);

        divisaoPrincipal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, divisaoEsquerdaVertical, sql);
        estilizarSplitPane(divisaoPrincipal);
        divisaoPrincipal.setResizeWeight(1.0);

        aplicarPosicoesIniciais();

        return construirContainerComQuadro();
    }

    private void aplicarPosicoesIniciais() {
        SwingUtilities.invokeLater(() -> {
            int larg = divisaoPrincipal.getWidth();
            int alt = divisaoEsquerdaVertical.getHeight();
            divisaoPrincipal.setDividerLocation((int)(larg * PROPORCAO_LARGURA_ESQ));
            divisaoEsquerdaVertical.setDividerLocation((int)(alt * PROPORCAO_ALTURA_ESQ));
        });
    }

    private JPanel construirContainerComQuadro() {
        JPanel workspace = new JPanel(new BorderLayout());
        workspace.setBorder(new CompoundBorder(
                new EtchedBorder(EtchedBorder.LOWERED),
                new EmptyBorder(2, 2, 2, 2)
        ));
        workspace.add(divisaoPrincipal, BorderLayout.CENTER);

        JPanel margem = new JPanel(new BorderLayout());
        margem.setBorder(new EmptyBorder(6, 6, 6, 6));
        margem.add(workspace, BorderLayout.CENTER);
        return margem;
    }

    private void estilizarSplitPane(JSplitPane divisor) {
        divisor.setBorder(null);
        divisor.setContinuousLayout(true);
        divisor.setOneTouchExpandable(true);
        divisor.setDividerSize(9);
    }

    public JPanel criarBarraStatusComPainel(String msg) {
        JPanel painel = new JPanel(new BorderLayout());
        painel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));
        rotuloStatus = new JLabel(msg);
        rotuloStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rotuloStatus.setBorder(new EmptyBorder(4, 10, 4, 10));
        painel.add(rotuloStatus, BorderLayout.WEST);
        return painel;
    }

    public void atualizarStatus(String texto) {
        if (rotuloStatus != null) {
            SwingUtilities.invokeLater(() -> rotuloStatus.setText(texto));
        }
    }

    public void sincronizarUI(JFrame frame) {
        int d1 = divisaoPrincipal.getDividerLocation();
        int d2 = divisaoEsquerdaVertical.getDividerLocation();
        SwingUtilities.updateComponentTreeUI(frame);
        SwingUtilities.invokeLater(() -> {
            divisaoPrincipal.setDividerLocation(d1);
            divisaoEsquerdaVertical.setDividerLocation(d2);
        });
    }
}