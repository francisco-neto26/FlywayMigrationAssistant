package com.supergestao.FlywayMigrationAssistant.ui;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.supergestao.FlywayMigrationAssistant.config.GerenciamentoTemaPadrao;

import javax.swing.*;

public class MenuOpcao {

    private final TelaInicial tela;

    public MenuOpcao(TelaInicial tela) {
        this.tela = tela;
    }

    public JMenuBar criarMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // --- Menu Configurações ---
        JMenu configMenu = new JMenu("Configurações");

        JMenuItem caminhoRaizItem = new JMenuItem("Selecionar Pasta db/migration");
        caminhoRaizItem.addActionListener(e -> tela.MigrationSelecionadaDiretorio());
        configMenu.add(caminhoRaizItem);

        configMenu.addSeparator();

        // --- Submenu de Temas ---
        JMenu menuTemas = new JMenu("Temas");
        ButtonGroup grupoTemas = new ButtonGroup();
        String temaAtual = GerenciamentoTemaPadrao.carregaTema();

        adicionarOpcaoTema(menuTemas, grupoTemas, "System", temaAtual);
        adicionarOpcaoTema(menuTemas, grupoTemas, "Dark", temaAtual);
        adicionarOpcaoTema(menuTemas, grupoTemas, "Light", temaAtual);

        configMenu.add(menuTemas);
        configMenu.addSeparator();

        JMenuItem limparConfigItem = new JMenuItem("🗑️ Limpar Configuração");
        limparConfigItem.addActionListener(e -> tela.limparConfiguracao());
        configMenu.add(limparConfigItem);

        configMenu.addSeparator();

        JMenuItem sairItem = new JMenuItem("Sair");
        sairItem.addActionListener(e -> System.exit(0));
        configMenu.add(sairItem);

        menuBar.add(configMenu);

        // --- Menu Ajuda ---
        JMenu menuAjuda = new JMenu("Ajuda");
        JMenuItem sobreItem = new JMenuItem("Sobre");
        sobreItem.addActionListener(e -> mostrarSobre());
        menuAjuda.add(sobreItem);
        menuBar.add(menuAjuda);

        return menuBar;
    }

    private void adicionarOpcaoTema(JMenu menu, ButtonGroup grupo, String nome, String atual) {
        JRadioButtonMenuItem item = new JRadioButtonMenuItem(nome, nome.equals(atual));
        item.addActionListener(e -> aplicarTema(nome));
        grupo.add(item);
        menu.add(item);
    }

    private void aplicarTema(String nomeTema) {
        try {
            if (nomeTema.equals("Dark")) {
                FlatDarkLaf.setup();
            } else if (nomeTema.equals("Light")) {
                FlatLightLaf.setup();
            } else {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }

            GerenciamentoTemaPadrao.salvaTema(nomeTema);

            SwingUtilities.updateComponentTreeUI(tela);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void mostrarSobre() {
        String message = "Super Gestão - Flyway Migration Assistant\n\n" +
                "Versão: 1.0.0\n\n" +
                "Ferramenta para gestão de migrações Flyway\n" +
                "seguindo as Regras de Ouro de nomenclatura.\n\n" +
                "Desenvolvido com Java 21 e Swing";

        JOptionPane.showMessageDialog(this, message, "Sobre",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
