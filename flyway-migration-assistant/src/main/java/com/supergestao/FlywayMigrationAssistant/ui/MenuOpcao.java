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

        // Menu Configurações
        JMenu menuConfig = new JMenu("Configurações");
        estilizarItem(menuConfig);

        menuConfig.add(criarItemSimples("Selecionar Pasta", e -> tela.MigrationSelecionadaDiretorio()));
        menuConfig.addSeparator();
        menuConfig.add(criarMenuTemas());
        menuConfig.addSeparator();
        menuConfig.add(criarItemSimples("Limpar Configuração", e -> tela.limparConfiguracao()));
        menuConfig.addSeparator();
        menuConfig.add(criarItemSimples("Sair", e -> System.exit(0)));

        menuBar.add(menuConfig);

        // Menu Ajuda
        JMenu menuAjuda = new JMenu("Ajuda");
        estilizarItem(menuAjuda);
        menuAjuda.add(criarItemSimples("Sobre", e -> mostrarSobre()));
        menuBar.add(menuAjuda);

        return menuBar;
    }

    public JMenu criarMenuTemas() {
        JMenu menuTemas = new JMenu("Temas e Estilos");
        estilizarItem(menuTemas);

        String atual = GerenciamentoTemaPadrao.carregaTema();

        JMenu menuModernos = new JMenu("FlatLaf");
        estilizarItem(menuModernos);
        String[] modernos = {"Flat Dark", "Flat Light", "IntelliJ", "Darcula"};
        for (String t : modernos) adicionarItemTema(menuModernos, t, atual);

        JMenu menuNativos = new JMenu("Padrão");
        estilizarItem(menuNativos);
        String[] nativos = {"Sistema", "Nimbus", "Metal", "Motif"};
        for (String t : nativos) adicionarItemTema(menuNativos, t, atual);

        menuTemas.add(menuModernos);
        menuTemas.add(menuNativos);
        adicionarItemTema(menuTemas, "Material", atual);

        return menuTemas;
    }

    private void adicionarItemTema(JMenu menu, String nome, String atual) {
        JMenuItem item = new JMenuItem(nome);
        estilizarItem(item);

        if (nome.equals(atual)) {
            item.setFont(item.getFont().deriveFont(Font.BOLD));
            item.setForeground(new Color(0, 102, 204));
        }

        item.addActionListener(e -> {
            TemaService.configurarLookAndFeel(nome);
            GerenciamentoTemaPadrao.salvaTema(nome);
            tela.atualizarLookAndFeel();
            tela.setJMenuBar(this.criarMenuBar());
            tela.revalidate();
            tela.repaint();
        });

        menu.add(item);
    }

    private JMenuItem criarItemSimples(String texto, java.awt.event.ActionListener acao) {
        JMenuItem item = new JMenuItem(texto);
        estilizarItem(item);
        item.addActionListener(acao);
        return item;
    }

    private void estilizarItem(JMenuItem item) {
        item.setIcon(null);
        item.setHorizontalAlignment(SwingConstants.LEFT);
        item.setBorder(new EmptyBorder(5, 12, 5, 12));
        item.setPreferredSize(new Dimension(item.getPreferredSize().width, 30));
    }

    private void mostrarSobre() {
        String message = "Super Gestão - Flyway Migration Assistant\n\n" +
                "Versão: 1.0.0\n\n" +
                "Ferramenta para gestão de migrações Flyway\n" +
                "seguindo as Regras de Ouro de nomenclatura.\n\n" +
                "Desenvolvido com Java 21 e Swing";

        JOptionPane.showMessageDialog(tela, message, "Sobre",
                JOptionPane.INFORMATION_MESSAGE);
    }
}