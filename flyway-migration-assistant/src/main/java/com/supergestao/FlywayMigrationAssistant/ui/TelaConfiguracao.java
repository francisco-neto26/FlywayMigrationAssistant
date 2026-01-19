package com.supergestao.FlywayMigrationAssistant.ui;

import com.supergestao.FlywayMigrationAssistant.service.DiretorioService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TelaConfiguracao extends JDialog {
    private JTextField diretorioMigration;
    private JTextField diretorioModulos;
    private PainelSql painelSql;
    private boolean alterado = false;
    private final GerenciadorLayout gerenciadorLayout;
    private final DiretorioService diretorioService;
    JButton btnSairCancelar;

    public TelaConfiguracao(TelaInicial pai, DiretorioService diretorioService) {
        super(pai, "Administração do Sistema", true);
        this.gerenciadorLayout = new GerenciadorLayout();
        this.diretorioService = diretorioService;
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setSize(800, 600);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());
        JPanel painelPrincipal = new JPanel(new BorderLayout());
        painelPrincipal.setBorder(new EmptyBorder(10, 10, 10, 10));
        painelPrincipal.add(criaAbas(), BorderLayout.CENTER);
        add(painelPrincipal, BorderLayout.CENTER);
        add(criarRodape(), BorderLayout.SOUTH);
    }

    private JTabbedPane criaAbas(){
        JTabbedPane abasPainel = new JTabbedPane();

        abasPainel.addTab("Diretórios", criaPainelDiretorio());

        painelSql = new PainelSql();
        abasPainel.addTab("Templates", gerenciadorLayout.montarEstruturaCompleta(null,null,painelSql,null));
        return abasPainel;
    }

    private JPanel criaPainelDiretorio() {
        JPanel painelDiretorio = new JPanel(new GridBagLayout());
        painelDiretorio.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        painelDiretorio.add(new JLabel("Diretório leitura arquivos Migration"), gbc);

        diretorioMigration = new JTextField(diretorioService.obterCaminhoRaizSalvo("Migration"));
        diretorioMigration.setEditable(false);
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 1.0;
        painelDiretorio.add(diretorioMigration, gbc);

        String textoBtnMig = diretorioService.obterCaminhoRaizSalvo("Migration").isEmpty() ? "Selecionar" : "Alterar";
        JButton btnSelecionarMigration = new JButton(textoBtnMig + " Diretorio");
        btnSelecionarMigration.addActionListener(e -> abrirSeletor("Migration"));

        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 0;
        painelDiretorio.add(btnSelecionarMigration, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        painelDiretorio.add(new JLabel("Diretório leitura Modulos"), gbc);

        diretorioModulos = new JTextField(diretorioService.obterCaminhoRaizSalvo("Modulo"));
        diretorioModulos.setEditable(false);
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 1.0;
        painelDiretorio.add(diretorioModulos, gbc);

        String textoBtnMod = diretorioService.obterCaminhoRaizSalvo("Modulo").isEmpty() ? "Selecionar" : "Alterar";
        JButton btnSelecionarModulo = new JButton(textoBtnMod + " Diretorio");
        btnSelecionarModulo.addActionListener(e -> abrirSeletor("Modulo"));
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 0;
        painelDiretorio.add(btnSelecionarModulo, gbc);

        return painelDiretorio;
    }

    private void abrirSeletor(String opcao) {
        JFileChooser seletor = new JFileChooser();
        seletor.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        if (seletor.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            alterado = true;
            if (btnSairCancelar != null) {
                btnSairCancelar.setText("Cancelar");
            }
            if(opcao.equalsIgnoreCase("Migration")){
                diretorioMigration.setText(seletor.getSelectedFile().getAbsolutePath());
            } else if (opcao.equalsIgnoreCase("Modulo")) {
                diretorioModulos.setText(seletor.getSelectedFile().getAbsolutePath());
            }
        }
    }

    private JPanel criarRodape() {
        JPanel painelBotoes = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.addActionListener(e -> {
            salvarDiretorioSelecionado();
            this.dispose();
        });
        btnSairCancelar = new JButton(alterado ? "Cancelar" : "Sair");
        btnSairCancelar.addActionListener(e -> fecharComVerificacao());
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                fecharComVerificacao();
            }
        });

        painelBotoes.add(btnSalvar);
        painelBotoes.add(btnSairCancelar);

        return painelBotoes;

    }

    private void salvarDiretorioSelecionado() {
        boolean sucesso = diretorioService.salvarConfiguracoes(diretorioMigration.getText(), diretorioModulos.getText());

        if (sucesso) {
            JOptionPane.showMessageDialog(this,
                    "Todas as configurações foram salvas com sucesso!",
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            alterado = false;
        } else {
            JOptionPane.showMessageDialog(this,
                    "Erro ao salvar: verifique se os diretórios são válidos.",
                    "Erro de Validação", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fecharComVerificacao() {
        if (alterado) {
            int opcao = JOptionPane.showConfirmDialog(
                    this,
                    "Existem alterações não salvas. Deseja realmente sair?",
                    "Confirmar Saída",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (opcao == JOptionPane.YES_OPTION) {
                this.dispose();
                alterado = false;
            }
        } else {
            this.dispose();
        }
    }
}