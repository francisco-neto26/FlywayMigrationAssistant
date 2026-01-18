package com.supergestao.FlywayMigrationAssistant.ui;

import com.supergestao.FlywayMigrationAssistant.config.GerenciamentoTemaPadrao;
import com.supergestao.FlywayMigrationAssistant.service.TemaService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MenuOpcao {
    private final TelaInicial tela;

    public MenuOpcao(TelaInicial tela) {
        this.tela = tela;
    }

    public JMenuBar criarMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBorder(BorderFactory.createEmptyBorder());

        JMenu menuConfig = new JMenu("Configurações");
        estilizarItem(menuConfig, 0);

        menuConfig.add(criarItemSimples("Selecionar Pasta", e -> tela.MigrationSelecionadaDiretorio()));
        menuConfig.addSeparator();
        menuConfig.add(criarMenuTemas());
        menuConfig.addSeparator();
        menuConfig.add(criarItemSimples("Limpar Configuração", e -> tela.limparConfiguracao()));
        menuConfig.addSeparator();
        menuConfig.add(criarItemSimples("Sair", e -> System.exit(0)));

        menuBar.add(menuConfig);

        JMenu menuAjuda = new JMenu("Ajuda");
        estilizarItem(menuAjuda, 0);
        menuAjuda.add(criarItemSimples("Sobre", e -> mostrarSobre()));
        menuBar.add(menuAjuda);

        return menuBar;
    }

    public JMenu criarMenuTemas() {
        JMenu menuTemas = new JMenu("Temas e Estilos");
        estilizarItem(menuTemas, 160);

        String atual = GerenciamentoTemaPadrao.carregaTema();

        JMenu menuModernos = new JMenu("FlatLaf");
        estilizarItem(menuModernos, 140);
        String[] modernos = {"Flat Dark", "Flat Light", "IntelliJ", "Darcula"};
        for (String t : modernos) adicionarItemTema(menuModernos, t, atual);

        JMenu menuNativos = new JMenu("Padrão");
        estilizarItem(menuNativos, 140);
        String[] nativos = {"Sistema", "Nimbus", "Metal", "Motif"};
        for (String t : nativos) adicionarItemTema(menuNativos, t, atual);

        menuTemas.add(menuModernos);
        menuTemas.add(menuNativos);

        return menuTemas;
    }

    private void adicionarItemTema(JMenu menu, String nome, String atual) {
        JMenuItem item = new JMenuItem(nome);
        estilizarItem(item, 140);

        if (nome.equals(atual)) {
            item.setFont(item.getFont().deriveFont(Font.BOLD));
            item.setForeground(new Color(0, 102, 204));
        }

        item.addActionListener(e -> {
            TemaService.configurarLookAndFeel(nome);
            GerenciamentoTemaPadrao.salvaTema(nome);

            SwingUtilities.updateComponentTreeUI(tela);
            tela.setJMenuBar(null);
            tela.revalidate();
            tela.repaint();

            tela.atualizarLookAndFeel();

            tela.setJMenuBar(this.criarMenuBar());
            tela.revalidate();
            tela.repaint();

            tela.atualizarLookAndFeel();
        });

        menu.add(item);
    }

    private JMenuItem criarItemSimples(String texto, java.awt.event.ActionListener acao) {
        JMenuItem item = new JMenuItem(texto);
        estilizarItem(item, 180); // Itens principais do dropdown com 180px
        item.addActionListener(acao);
        return item;
    }

    private void estilizarItem(JMenuItem item, int larguraMinima) {
        item.setIcon(null);
        item.setHorizontalAlignment(SwingConstants.LEFT);
        item.setBorder(new EmptyBorder(5, 12, 5, 12));

        int altura = 30;
        int larguraTexto = item.getPreferredSize().width;

        int larguraFinal = Math.max(larguraMinima, larguraTexto);

        item.setPreferredSize(new Dimension(larguraFinal, altura));
    }

    private void mostrarSobre() {
        String message = "Super Gestão - Flyway Migration Assistant\n\nVersão: 1.0.0";
        JOptionPane.showMessageDialog(tela, message, "Sobre", JOptionPane.INFORMATION_MESSAGE);
    }
}