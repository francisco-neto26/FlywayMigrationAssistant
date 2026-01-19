package com.supergestao.FlywayMigrationAssistant.ui;

import com.supergestao.FlywayMigrationAssistant.config.GerenciamentoDiretorioPadrao;
import com.supergestao.FlywayMigrationAssistant.service.ArquivoService;
import com.supergestao.FlywayMigrationAssistant.service.DiretorioService;
import com.supergestao.FlywayMigrationAssistant.service.ModuloService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

public class TelaInicial extends JFrame {
    private ArquivoService arquivoService;
    private ModuloService moduloService;
    private PainelModulo painelModulo;
    private PainelArquivos painelArquivos;
    private PainelSql painelSql;
    private PainelMigration criaPainel;
    private JLabel statusBar;
    private boolean configurando;
    private final GerenciadorLayout gerenciadorLayout;
    private DiretorioService diretorioService;

    public TelaInicial() {
        this.arquivoService = new ArquivoService();
        this.gerenciadorLayout = new GerenciadorLayout();
        this.diretorioService = new DiretorioService();
        inicializarTelaPrincipal();
        SwingUtilities.invokeLater(() -> validarDiretoriosLeitura());
    }

    private void inicializarTelaPrincipal() {
        setTitle("Super Gestão - Flyway Migration Assistant");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1024, 768));
        setSize(1280, 800);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        criaMenuFuncao();
        criaPainelPrincipal();
    }

    private void criaMenuFuncao() {
        MenuOpcao menuOpcao = new MenuOpcao(this, diretorioService);
        setJMenuBar(menuOpcao.criarMenuBar(diretorioService));
    }

    private void criaPainelPrincipal() {
        JPanel painelPrincipal = new JPanel(new BorderLayout(10, 10));
        painelPrincipal.setBorder(new EmptyBorder(10, 10, 10, 10));

        painelModulo = new PainelModulo(arquivoService);
        painelArquivos = new PainelArquivos(arquivoService);
        painelSql = new PainelSql();
        criaPainel = new PainelMigration(arquivoService);

        JPanel conteudo = gerenciadorLayout.montarEstruturaCompleta(
                painelModulo, painelArquivos, painelSql, criaPainel
        );
        add(conteudo, BorderLayout.CENTER);

        add(gerenciadorLayout.criarBarraStatus("Validando configurações iniciais"), BorderLayout.SOUTH);

        configurarEventos();
    }

    private void configurarEventos() {
        painelModulo.addModuloSelecionadoListener(modulo -> {
            painelArquivos.carregarArquivos(modulo);
            gerenciadorLayout.atualizarStatus("Módulo selecionado: " + modulo);
        });

        painelArquivos.addSeletorArquivosListener(arquivo -> {
            painelSql.displayArquivo(arquivo);
            gerenciadorLayout.atualizarStatus("Arquivo: " + arquivo.getnome());
        });

        criaPainel.addArquivoCriadoListener(() -> {
            String moduloSelecionado = painelModulo.obterModuloSelecionado();
            if (moduloSelecionado != null) {
                painelArquivos.carregarArquivos(moduloSelecionado);
            }
            gerenciadorLayout.atualizarStatus("Novo arquivo de migração criado com sucesso!");
        });
    }

    private void atualizarStatus(String message) {
        statusBar.setText(message);
    }

    private void validarDiretoriosLeitura() {
        configurando = true;
        Boolean existeDiretorios = diretorioService.validaDiretorios();

        if (!existeDiretorios){
            JOptionPane.showMessageDialog(this,
                    "Configurações iniciais não encontradas. Por favor, defina os diretórios de trabalho.",
                    "Configuração Inicial",
                    JOptionPane.WARNING_MESSAGE);
            TelaConfiguracao telaConfig = new TelaConfiguracao(this, this.diretorioService);
            telaConfig.setVisible(true);
            if (diretorioService.validaDiretorios()) {
                carregarDadosIniciais();
            } else {
                Object[] opcoesBotoes = {"Sair", "Configurar"};
                int opcao = JOptionPane.showOptionDialog(
                        this,
                        "Configurações iniciais não realizadas. O Sistema somente será executado caso configurado. " +
                                "\n Deseja fechar o sistema ou realizar a configuração.",
                        "Configuração Inicial",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        opcoesBotoes,
                        opcoesBotoes[0]);
                if (opcao == 1){
                    validarDiretoriosLeitura();
                }else{
                    System.exit(0);
                }
                atualizarStatus("Sistema sendo encerrado por falta de configurações.");
            }
        } else {
            carregarDadosIniciais();
        }
    }

    private void carregarDadosIniciais() {
        File diretorioMigration = new File(diretorioService.obterCaminhoRaizSalvo("Migration"));
        File diretorioModulo = new File(diretorioService.obterCaminhoRaizSalvo("Modulo"));
        arquivoService.setpastaRaiz(diretorioMigration);
        moduloService.setpastaRaiz(diretorioModulo);
        painelModulo.atualizar();
        criaPainel.atualizar();
        atualizarStatus("Diretório Migration: " + diretorioMigration.getAbsolutePath() +
                " Diretório Modulos: " + diretorioModulo.getAbsolutePath());
    }

    public void atualizarLookAndFeel() {
        gerenciadorLayout.sincronizarUI(this);
    }

}