package com.supergestao.FlywayMigrationAssistant.ui;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.supergestao.FlywayMigrationAssistant.config.GerenciamentoTemaPadrao;
import com.supergestao.FlywayMigrationAssistant.service.TemaService;

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
        configMenu.add(criarMenuTemas());

        /*// --- Submenu de Temas ---
        JMenu menuTemas = new JMenu("Temas");
        ButtonGroup grupoTemas = new ButtonGroup();
        String temaAtual = GerenciamentoTemaPadrao.carregaTema();

        adicionarOpcaoTema(menuTemas, grupoTemas, "System", temaAtual);
        adicionarOpcaoTema(menuTemas, grupoTemas, "Dark", temaAtual);
        adicionarOpcaoTema(menuTemas, grupoTemas, "Light", temaAtual);

        configMenu.add(menuTemas);
        configMenu.addSeparator();

         */



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
            TemaService.configurarLookAndFeel(nomeTema);
            GerenciamentoTemaPadrao.salvaTema(nomeTema);
            SwingUtilities.updateComponentTreeUI(tela);
            tela.pack();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(tela, "Erro ao aplicar o tema: " + ex.getMessage(),
                    "Erro de Interface", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public JMenu criarMenuTemas() {
        JMenu menuTemas = new JMenu("Temas e Estilos");
        ButtonGroup grupo = new ButtonGroup();
        String atual = GerenciamentoTemaPadrao.carregaTema();
        System.out.println("Tema atual: " + atual);

        // --- Submenu: Nativos/Padrão ---
        JMenu menuPadrao = new JMenu("Padrão Java");
        adicionarOpcaoTema(menuPadrao, grupo, "Sistema", atual);
        adicionarOpcaoTema(menuPadrao, grupo, "Metal", atual);
        adicionarOpcaoTema(menuPadrao, grupo, "Nimbus", atual);
        adicionarOpcaoTema(menuPadrao, grupo, "Motif", atual);
        menuTemas.add(menuPadrao);

        // --- Submenu: FlatLaf (Modernos) ---
        JMenu menuFlat = new JMenu("Modernos (FlatLaf)");
        adicionarOpcaoTema(menuFlat, grupo, "Flat Light", atual);
        adicionarOpcaoTema(menuFlat, grupo, "Flat Dark", atual);
        adicionarOpcaoTema(menuFlat, grupo, "IntelliJ", atual);
        adicionarOpcaoTema(menuFlat, grupo, "Darcula", atual);
        menuTemas.add(menuFlat);

        // --- Outros ---
        adicionarOpcaoTema(menuTemas, grupo, "Material", atual);

        return menuTemas;
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
