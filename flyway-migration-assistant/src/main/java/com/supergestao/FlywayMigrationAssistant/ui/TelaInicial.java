package com.supergestao.FlywayMigrationAssistant.ui;

import com.supergestao.FlywayMigrationAssistant.config.GerenciamentoDiretorioPadrao;
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
    private PainelMigration criaPainel;
    private JLabel statusBar;

    public TelaInicial() {
        this.arquivoService = new ArquivoService();
        inicializarTelaPrincipal();
        SwingUtilities.invokeLater(() -> carregarCaminhoPastaSalva());
    }

    private void inicializarTelaPrincipal() {
        setTitle("Super Gestão - Flyway Migration Assistant");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 850);
        setLocationRelativeTo(null);

        criaMenuFuncao();
        criaPainelPrincipal();
        criaStatusBarra();
    }

    private void criaMenuFuncao() {
        MenuOpcao menuOpcao = new MenuOpcao(this);
        setJMenuBar(menuOpcao.criarMenuBar());
    }
/*
    private void criaMenuFuncao() {
        JMenuBar menuBar = new JMenuBar();
        JMenu configMenu = new JMenu("Configurações");

        JMenuItem caminhoRaizItem = new JMenuItem("Selecionar Pasta db/migration");
        caminhoRaizItem.addActionListener(e -> MigrationSelecionadaDiretorio());
        configMenu.add(caminhoRaizItem);

        configMenu.addSeparator();

        JMenuItem limparConfigItem = new JMenuItem("🗑️ Limpar Configuração");
        limparConfigItem.addActionListener(e -> limparConfiguracao());
        configMenu.add(limparConfigItem);

        configMenu.addSeparator();

        JMenuItem sairItem = new JMenuItem("Sair");
        sairItem.addActionListener(e -> System.exit(0));
        configMenu.add(sairItem);

        menuBar.add(configMenu);

        JMenu menuAjuda = new JMenu("Ajuda");
        JMenuItem sobreItem = new JMenuItem("Sobre");
        sobreItem.addActionListener(e -> mostrarSobre());
        menuAjuda.add(sobreItem);
        menuBar.add(menuAjuda);

        setJMenuBar(menuBar);
    }
*/
    private void criaPainelPrincipal() {
        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(new EmptyBorder(10, 10, 10, 10));

        painelModulo = new PainelModulo(arquivoService);
        painelArquivos = new PainelArquivos(arquivoService);
        painelSql = new PainelSql();
        criaPainel = new PainelMigration(arquivoService);

        painelModulo.addModuloSelecionadoListener(module -> {
            painelArquivos.carregarArquivos(module);
            atualizarStatus("Módulo selecionado: " + module);
        });

        painelArquivos.addSeletorArquivosListener(arquivo -> {
            painelSql.displayArquivo(arquivo);
            atualizarStatus("Arquivo: " + arquivo.getnome());
        });

        criaPainel.addArquivoCriadoListener(() -> {
            String moduloSelecionado = painelModulo.obterModuloSelecionado();
            if (moduloSelecionado != null) {
                painelArquivos.carregarArquivos(moduloSelecionado);
            }
            atualizarStatus("Novo arquivo de migração criado com sucesso!");
        });

        JPanel painelEsquerdo = new JPanel(new BorderLayout());
        painelEsquerdo.add(painelModulo, BorderLayout.CENTER);
        painelEsquerdo.setPreferredSize(new Dimension(250, 0));

        JPanel painelCentral = new JPanel(new BorderLayout(5, 5));
        JSplitPane divisaoCentral = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                painelArquivos, painelSql);
        divisaoCentral.setDividerLocation(300);
        painelCentral.add(divisaoCentral, BorderLayout.CENTER);

        JPanel painelDireito = new JPanel(new BorderLayout());
        painelDireito.add(criaPainel, BorderLayout.CENTER);
        painelDireito.setPreferredSize(new Dimension(400, 0));

        JSplitPane divisaoCentralEsquerda = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                painelEsquerdo, painelCentral);
        divisaoCentralEsquerda.setDividerLocation(250);

        JSplitPane divisaoPrincipal = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                divisaoCentralEsquerda, painelDireito);
        divisaoPrincipal.setDividerLocation(900);

        painelPrincipal.add(divisaoPrincipal, BorderLayout.CENTER);
        add(painelPrincipal);
    }

    private void criaStatusBarra() {
        statusBar = new JLabel("Selecione a pasta db/migration para começar");
        statusBar.setBorder(new EmptyBorder(5, 5, 5, 5));
        add(statusBar, BorderLayout.SOUTH);
    }

    public void MigrationSelecionadaDiretorio() {
        System.out.println("=== Iniciando seleção de pasta ===");

        JFileChooser diretorioDefinidoUsuario = new JFileChooser();
        diretorioDefinidoUsuario.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        diretorioDefinidoUsuario.setDialogTitle("Selecionar pasta db/migration");
        diretorioDefinidoUsuario.setApproveButtonText("Selecionar");

        File diretorioSelecionado = arquivoService.getpastaRaiz();
        if (diretorioSelecionado != null) {
            System.out.println("Pasta atual: " + diretorioSelecionado.getAbsolutePath());
            diretorioDefinidoUsuario.setCurrentDirectory(diretorioSelecionado.getParentFile());
        } else {
            String diretorioUsuario = System.getProperty("user.home");
            diretorioDefinidoUsuario.setCurrentDirectory(new File(diretorioUsuario));
            System.out.println("Iniciando em: " + diretorioUsuario);
        }

        System.out.println("Abrindo JFileChooser...");
        int opcao = diretorioDefinidoUsuario.showOpenDialog(this);

        if (opcao == JFileChooser.APPROVE_OPTION) {
            File selectedFolder = diretorioDefinidoUsuario.getSelectedFile();
            System.out.println("Pasta selecionada: " + selectedFolder.getAbsolutePath());

            arquivoService.setpastaRaiz(selectedFolder);

            GerenciamentoDiretorioPadrao.salvaPastaRaiz(selectedFolder.getAbsolutePath());

            painelModulo.atualizar();
            criaPainel.atualizar();

            atualizarStatus("Pasta selecionada: " + selectedFolder.getAbsolutePath());

            JOptionPane.showMessageDialog(this,
                    "Pasta configurada com sucesso!\n\n" +
                            "Raiz: " + selectedFolder.getAbsolutePath() + "\n" +
                            "Módulos encontrados: " + arquivoService.getModulos().size() + "\n\n" +
                            "Esta pasta será lembrada na próxima vez.",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        } else if (opcao == JFileChooser.CANCEL_OPTION) {
            System.out.println("Seleção cancelada pelo usuário");
        } else {
            System.out.println("Erro ao abrir o seletor: " + opcao);
        }

        System.out.println("=== Fim da seleção de pasta ===");
    }

    private void atualizarStatus(String message) {
        statusBar.setText(message);
    }

    private void carregarCaminhoPastaSalva() {
        File DiretorioSalvo = GerenciamentoDiretorioPadrao.obterPastaRaizComArquivo();

        if (DiretorioSalvo != null) {
            arquivoService.setpastaRaiz(DiretorioSalvo);
            painelModulo.atualizar();
            criaPainel.atualizar();
            atualizarStatus("Pasta carregada: " + DiretorioSalvo.getAbsolutePath());
            System.out.println("Pasta raiz carregada automaticamente");
        } else {
            // Não tem pasta configurada, mostrar diálogo
            SwingUtilities.invokeLater(() -> {
                int opcao = JOptionPane.showConfirmDialog(this,
                        "Nenhuma pasta db/migration configurada.\n\n" +
                                "Deseja selecionar a pasta agora?",
                        "Configuração Inicial",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if (opcao == JOptionPane.YES_OPTION) {
                    MigrationSelecionadaDiretorio();
                } else {
                    atualizarStatus("Selecione a pasta db/migration para começar");
                }
            });
        }
    }

    public void limparConfiguracao() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Deseja limpar a pasta configurada?\n\n" +
                        "Você precisará selecioná-la novamente na próxima vez.",
                "Limpar Configuração",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            GerenciamentoDiretorioPadrao.limparPastaRaiz();
            arquivoService.setpastaRaiz(null);
            painelModulo.atualizar();
            criaPainel.atualizar();
            atualizarStatus("Configuração limpa. Selecione a pasta db/migration novamente.");

            JOptionPane.showMessageDialog(this,
                    "Configuração limpa com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

}