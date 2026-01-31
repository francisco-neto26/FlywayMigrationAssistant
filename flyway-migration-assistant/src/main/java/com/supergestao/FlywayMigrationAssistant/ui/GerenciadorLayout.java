package com.supergestao.FlywayMigrationAssistant.ui;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.util.Objects;

public class GerenciadorLayout {
    private static final double PROPORCAO_NAVEGACAO = 0.40;
    private static final double PROPORCAO_ALTURA_SUP = 0.68;
    private static final double PROPORCAO_MODULO = 0.37;
    private static final int MIN_MODULO_WIDTH = 200;
    private static final int MIN_ARQUIVOS_WIDTH = 350;
    private static final int MIN_SQL_WIDTH = 450;
    private static final int MIN_CRIACAO_HEIGHT = 180;
    private static final int FALLBACK_LARGURA_NAVEGACAO = 520;
    private static final int FALLBACK_ALTURA_SUPERIOR = 540;
    private static final int FALLBACK_DIVISAO_MODULO = 195;
    private JSplitPane divisaoPrincipal;
    private JSplitPane divisaoEsquerdaVertical;
    private JSplitPane divisaoNavegacaoSuperior;
    private JLabel rotuloStatus;

    public JPanel montarEstruturaCompleta(Component modulo, Component arquivos,
                                          Component sql, Component criacao) {
        Objects.requireNonNull(modulo, "Componente módulo não pode ser null");
        Objects.requireNonNull(arquivos, "Componente arquivos não pode ser null");
        Objects.requireNonNull(sql, "Componente SQL não pode ser null");
        Objects.requireNonNull(criacao, "Componente criação não pode ser null");

        modulo.setMinimumSize(new Dimension(MIN_MODULO_WIDTH, 0));
        arquivos.setMinimumSize(new Dimension(MIN_ARQUIVOS_WIDTH, 0));
        sql.setMinimumSize(new Dimension(MIN_SQL_WIDTH, 0));
        criacao.setMinimumSize(new Dimension(0, MIN_CRIACAO_HEIGHT));

        divisaoNavegacaoSuperior = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, modulo, arquivos);
        estilizarSplitPane(divisaoNavegacaoSuperior);
        divisaoNavegacaoSuperior.setResizeWeight(PROPORCAO_MODULO);
        divisaoEsquerdaVertical = new JSplitPane(JSplitPane.VERTICAL_SPLIT, divisaoNavegacaoSuperior, criacao);
        estilizarSplitPane(divisaoEsquerdaVertical);
        divisaoEsquerdaVertical.setResizeWeight(PROPORCAO_ALTURA_SUP);
        divisaoPrincipal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, divisaoEsquerdaVertical, sql);
        estilizarSplitPane(divisaoPrincipal);
        divisaoPrincipal.setResizeWeight(0.0);
        aplicarPosicoesIniciais();

        return construirContainerComQuadro();
    }

    private void aplicarPosicoesIniciais() {
        SwingUtilities.invokeLater(() -> {
            try {
                int larguraTotal = divisaoPrincipal.getWidth();
                int alturaTotal = divisaoEsquerdaVertical.getHeight();
                if (larguraTotal > 100 && alturaTotal > 100) {
                    divisaoPrincipal.setDividerLocation((int)(larguraTotal * PROPORCAO_NAVEGACAO));
                    divisaoEsquerdaVertical.setDividerLocation((int)(alturaTotal * PROPORCAO_ALTURA_SUP));
                } else {
                    divisaoPrincipal.setDividerLocation(FALLBACK_LARGURA_NAVEGACAO);
                    divisaoEsquerdaVertical.setDividerLocation(FALLBACK_ALTURA_SUPERIOR);
                }
                divisaoNavegacaoSuperior.setDividerLocation(FALLBACK_DIVISAO_MODULO);

            } catch (Exception e) {
                System.err.println("Aviso: Erro ao aplicar posições iniciais: " + e.getMessage());
                divisaoPrincipal.setDividerLocation(FALLBACK_LARGURA_NAVEGACAO);
                divisaoEsquerdaVertical.setDividerLocation(FALLBACK_ALTURA_SUPERIOR);
                divisaoNavegacaoSuperior.setDividerLocation(FALLBACK_DIVISAO_MODULO);
            }
        });
    }

    private JPanel construirContainerComQuadro() {
        JPanel workspace = new JPanel(new BorderLayout());
        workspace.setBorder(new CompoundBorder(
                new EtchedBorder(EtchedBorder.LOWERED),
                new EmptyBorder(2, 2, 2, 2)
        ));
        workspace.add(divisaoPrincipal, BorderLayout.CENTER);
        JPanel containerComMargem = new JPanel(new BorderLayout());
        containerComMargem.setBorder(new EmptyBorder(8, 8, 8, 8));
        containerComMargem.add(workspace, BorderLayout.CENTER);

        return containerComMargem;
    }

    private void estilizarSplitPane(JSplitPane divisor) {
        if (divisor == null) return;

        divisor.setBorder(null);
        divisor.setContinuousLayout(true);
        divisor.setOneTouchExpandable(true);
        divisor.setDividerSize(8);
    }

    public JPanel criarBarraStatusComPainel(String mensagemInicial) {
        JPanel painelStatus = new JPanel(new BorderLayout());
        painelStatus.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

        rotuloStatus = new JLabel(mensagemInicial != null ? mensagemInicial : "Pronto");
        rotuloStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rotuloStatus.setBorder(new EmptyBorder(4, 10, 4, 10));

        painelStatus.add(rotuloStatus, BorderLayout.WEST);
        return painelStatus;
    }
    public void atualizarStatus(String texto) {
        if (rotuloStatus != null && texto != null) {
            SwingUtilities.invokeLater(() -> rotuloStatus.setText(texto));
        }
    }

    public void sincronizarUI(JFrame frame) {
        if (frame == null) {
            System.err.println("Aviso: Frame null ao sincronizar UI");
            return;
        }

        try {
            int d1 = divisaoPrincipal != null ? divisaoPrincipal.getDividerLocation() : FALLBACK_LARGURA_NAVEGACAO;
            int d2 = divisaoEsquerdaVertical != null ? divisaoEsquerdaVertical.getDividerLocation() : FALLBACK_ALTURA_SUPERIOR;
            int d3 = divisaoNavegacaoSuperior != null ? divisaoNavegacaoSuperior.getDividerLocation() : FALLBACK_DIVISAO_MODULO;

            SwingUtilities.updateComponentTreeUI(frame);

            SwingUtilities.invokeLater(() -> {
                if (divisaoPrincipal != null) {
                    divisaoPrincipal.setDividerLocation(d1);
                }
                if (divisaoEsquerdaVertical != null) {
                    divisaoEsquerdaVertical.setDividerLocation(d2);
                }
                if (divisaoNavegacaoSuperior != null) {
                    divisaoNavegacaoSuperior.setDividerLocation(d3);
                }
            });

        } catch (Exception e) {
            System.err.println("Erro ao sincronizar UI: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void limparRecursos() {
        try {
            if (divisaoPrincipal != null) {
                divisaoPrincipal.removeAll();
                divisaoPrincipal = null;
            }
            if (divisaoEsquerdaVertical != null) {
                divisaoEsquerdaVertical.removeAll();
                divisaoEsquerdaVertical = null;
            }
            if (divisaoNavegacaoSuperior != null) {
                divisaoNavegacaoSuperior.removeAll();
                divisaoNavegacaoSuperior = null;
            }
            rotuloStatus = null;
        } catch (Exception e) {
            System.err.println("Erro ao limpar recursos: " + e.getMessage());
        }
    }

    public void reaplicarPosicoes() {
        aplicarPosicoesIniciais();
    }

    public boolean isInicializado() {
        return divisaoPrincipal != null
                && divisaoEsquerdaVertical != null
                && divisaoNavegacaoSuperior != null;
    }
}