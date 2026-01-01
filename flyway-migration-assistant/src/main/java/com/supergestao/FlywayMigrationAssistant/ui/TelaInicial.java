package com.supergestao.FlywayMigrationAssistant.ui;

import com.supergestao.FlywayMigrationAssistant.service.ArquivoService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

public class TelaInicial extends JFrame {
    private ArquivoService arquivoService;
    private PainelModulo painelModulo;
    private PainelArquivos painelArquivos;
    private PainelSql painelSql;
    private MigrationCreatorPanel creatorPanel;
    private JLabel statusBar;

    public TelaInicial() {
        this.arquivoService = new ArquivoService();
        incializarUI();
        SwingUtilities.invokeLater(() -> loadSavedRootFolder());
    }

    private void incializarUI() {
        setTitle("Super Gestão - Flyway Migration Assistant");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 850);
        setLocationRelativeTo(null);

        criaMenuFuncao();
        criaPainelPrincipal();
        criaStatusBarra();
    }

    private void criaMenuFuncao() {
        JMenuBar menuBar = new JMenuBar();
        JMenu configMenu = new JMenu("Configurações");

        JMenuItem caminhoRaizItem = new JMenuItem("Selecionar Pasta db/migration");
        caminhoRaizItem.addActionListener(e -> selectMigrationRoot());
        configMenu.add(caminhoRaizItem);

        configMenu.addSeparator();

        JMenuItem limparConfigItem = new JMenuItem("🗑️ Limpar Configuração");
        limparConfigItem.addActionListener(e -> clearConfiguration());
        configMenu.add(limparConfigItem);

        configMenu.addSeparator();

        JMenuItem sairItem = new JMenuItem("Sair");
        sairItem.addActionListener(e -> System.exit(0));
        configMenu.add(sairItem);

        menuBar.add(configMenu);

        JMenu menuAjuda = new JMenu("Ajuda");
        JMenuItem sobreItem = new JMenuItem("Sobre");
        sobreItem.addActionListener(e -> showAbout());
        menuAjuda.add(sobreItem);
        menuBar.add(menuAjuda);

        setJMenuBar(menuBar);
    }

    private void criaPainelPrincipal() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Painéis
        painelModulo = new PainelModulo(arquivoService);
        painelArquivos = new PainelArquivos(arquivoService);
        painelSql = new PainelSql();
        creatorPanel = new MigrationCreatorPanel(arquivoService);

        // Conectar eventos
        painelModulo.addModuloSelecionadoListener(module -> {
            painelArquivos.carregarArquivos(module);
            atualizarStatus("Módulo selecionado: " + module);
        });

        painelArquivos.addSeletorArquivosListener(arquivo -> {
            painelSql.displayArquivo(arquivo);
            atualizarStatus("Arquivo: " + arquivo.getnome());
        });

        creatorPanel.addFileCreatedListener(() -> {
            String selectedModule = painelModulo.obterModuloSelecionado();
            if (selectedModule != null) {
                painelArquivos.carregarArquivos(selectedModule);
            }
            atualizarStatus("Novo arquivo de migração criado com sucesso!");
        });

        // Layout
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(painelModulo, BorderLayout.CENTER);
        leftPanel.setPreferredSize(new Dimension(250, 0));

        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        JSplitPane centerSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                painelArquivos, painelSql);
        centerSplit.setDividerLocation(300);
        centerPanel.add(centerSplit, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(creatorPanel, BorderLayout.CENTER);
        rightPanel.setPreferredSize(new Dimension(400, 0));

        JSplitPane leftCenterSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                leftPanel, centerPanel);
        leftCenterSplit.setDividerLocation(250);

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                leftCenterSplit, rightPanel);
        mainSplit.setDividerLocation(900);

        mainPanel.add(mainSplit, BorderLayout.CENTER);
        add(mainPanel);
    }

    private void criaStatusBarra() {
        statusBar = new JLabel("Selecione a pasta db/migration para começar");
        statusBar.setBorder(new EmptyBorder(5, 5, 5, 5));
        add(statusBar, BorderLayout.SOUTH);
    }

    private void selectMigrationRoot() {
        System.out.println("=== Iniciando seleção de pasta ===");

        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Selecionar pasta db/migration");
        chooser.setApproveButtonText("Selecionar");

        File currentRoot = arquivoService.getpastaRaiz();
        if (currentRoot != null) {
            System.out.println("Pasta atual: " + currentRoot.getAbsolutePath());
            chooser.setCurrentDirectory(currentRoot.getParentFile());
        } else {
            String userHome = System.getProperty("user.home");
            chooser.setCurrentDirectory(new File(userHome));
            System.out.println("Iniciando em: " + userHome);
        }

        System.out.println("Abrindo JFileChooser...");
        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = chooser.getSelectedFile();
            System.out.println("Pasta selecionada: " + selectedFolder.getAbsolutePath());

            arquivoService.setpastaRaiz(selectedFolder);

            PreferencesManager.saveRootFolder(selectedFolder.getAbsolutePath());

            painelModulo.atualizar();
            creatorPanel.refresh();

            atualizarStatus("Pasta selecionada: " + selectedFolder.getAbsolutePath());

            JOptionPane.showMessageDialog(this,
                    "Pasta configurada com sucesso!\n\n" +
                            "Raiz: " + selectedFolder.getAbsolutePath() + "\n" +
                            "Módulos encontrados: " + arquivoService.getModulos().size() + "\n\n" +
                            "Esta pasta será lembrada na próxima vez.",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } else if (result == JFileChooser.CANCEL_OPTION) {
            System.out.println("Seleção cancelada pelo usuário");
        } else {
            System.out.println("Erro ao abrir o seletor: " + result);
        }

        System.out.println("=== Fim da seleção de pasta ===");
    }

    private void showAbout() {
        String message = "Super Gestão - Flyway Migration Assistant\n\n" +
                "Versão: 1.0.0\n\n" +
                "Ferramenta para gestão de migrações Flyway\n" +
                "seguindo as Regras de Ouro de nomenclatura.\n\n" +
                "Desenvolvido com Java 21 e Swing";

        JOptionPane.showMessageDialog(this, message, "Sobre",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void atualizarStatus(String message) {
        statusBar.setText(message);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            TelaInicial window = new TelaInicial();
            window.setVisible(true);
        });
    }

    private void loadSavedRootFolder() {
        File savedRoot = PreferencesManager.getRootFolderAsFile();

        if (savedRoot != null) {
            arquivoService.setpastaRaiz(savedRoot);
            painelModulo.atualizar();
            creatorPanel.refresh();
            atualizarStatus("Pasta carregada: " + savedRoot.getAbsolutePath());
            System.out.println("Pasta raiz carregada automaticamente");
        } else {
            // Não tem pasta configurada, mostrar diálogo
            SwingUtilities.invokeLater(() -> {
                int result = JOptionPane.showConfirmDialog(this,
                        "Nenhuma pasta db/migration configurada.\n\n" +
                                "Deseja selecionar a pasta agora?",
                        "Configuração Inicial",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (result == JOptionPane.YES_OPTION) {
                    selectMigrationRoot();
                } else {
                    atualizarStatus("Selecione a pasta db/migration para começar");
                }
            });
        }
    }

    private void clearConfiguration() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Deseja limpar a pasta configurada?\n\n" +
                        "Você precisará selecioná-la novamente na próxima vez.",
                "Limpar Configuração",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            PreferencesManager.clearRootFolder();
            arquivoService.setpastaRaiz(null);
            painelModulo.atualizar();
            creatorPanel.refresh();
            atualizarStatus("Configuração limpa. Selecione a pasta db/migration novamente.");

            JOptionPane.showMessageDialog(this,
                    "Configuração limpa com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

}