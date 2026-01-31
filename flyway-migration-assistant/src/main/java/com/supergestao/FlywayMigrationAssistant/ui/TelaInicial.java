package com.supergestao.FlywayMigrationAssistant.ui;

import com.supergestao.FlywayMigrationAssistant.service.ArquivoService;
import com.supergestao.FlywayMigrationAssistant.service.DiretorioService;
import com.supergestao.FlywayMigrationAssistant.service.ModuloService;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class TelaInicial extends JFrame {
    private final ArquivoService arquivoService;
    private final ModuloService moduloService;
    private final DiretorioService diretorioService;
    private final GerenciadorLayout gerenciadorLayout;

    private PainelModulo painelModulo;
    private PainelArquivos painelArquivos;
    private PainelSql painelSql;
    private PainelMigration painelMigracao;

    public TelaInicial() {
        this.arquivoService = new ArquivoService();
        this.diretorioService = new DiretorioService();
        this.moduloService = new ModuloService();
        this.gerenciadorLayout = new GerenciadorLayout();

        configurarJanela();
        montarComponentes();

        SwingUtilities.invokeLater(this::validarConfiguracoes);
    }

    private void configurarJanela() {
        setTitle("Super Gestão - Flyway Migration Assistant");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 800));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
    }

    private void montarComponentes() {
        // Menu
        MenuOpcao menuOpcao = new MenuOpcao(this, diretorioService);
        setJMenuBar(menuOpcao.criarMenuBar(diretorioService));

        // Inicialização dos Painéis
        painelModulo = new PainelModulo(arquivoService, moduloService, diretorioService);
        painelArquivos = new PainelArquivos(arquivoService);
        painelSql = new PainelSql();
        painelMigracao = new PainelMigration(arquivoService);

        // Montagem do Workspace Delimitado (O Quadro)
        JPanel workspaceComQuadro = gerenciadorLayout.montarEstruturaCompleta(
                painelModulo, painelArquivos, painelSql, painelMigracao
        );

        // Barra de Status Independente (SOUTH do frame, fora do quadro)
        JPanel painelStatus = gerenciadorLayout.criarBarraStatusComPainel("Sistema pronto.");

        setLayout(new BorderLayout());
        add(workspaceComQuadro, BorderLayout.CENTER);
        add(painelStatus, BorderLayout.SOUTH);

        vincularEventos();
    }

    private void vincularEventos() {
        // Agora o 'modulo' recebido aqui é um objeto File
        painelModulo.addModuloSelecionadoListener(modulo -> {
            painelArquivos.carregarArquivos(modulo);
            gerenciadorLayout.atualizarStatus("Módulo selecionado: " + modulo.getName());
        });

        painelArquivos.addSeletorArquivosListener(arquivo -> {
            painelSql.displayArquivo(arquivo);
            gerenciadorLayout.atualizarStatus("Editando: " + arquivo.getnome());
        });

        painelMigracao.addArquivoCriadoListener(() -> {
            // Agora 'mod' é um File, acabando com o erro de compilação
            File mod = painelModulo.obterModuloSelecionado();
            if (mod != null){
                painelArquivos.carregarArquivos(mod);
            }
            gerenciadorLayout.atualizarStatus("Arquivo gerado com sucesso.");
        });
    }

    private void validarConfiguracoes() {
        if (!diretorioService.validaDiretorios()) {
            solicitarConfiguracao();
        } else {
            carregarDados();
        }
    }

    private void solicitarConfiguracao() {
        TelaConfiguracao telaConfig = new TelaConfiguracao(this, diretorioService);
        telaConfig.setVisible(true);
        if (diretorioService.validaDiretorios()){
            carregarDados();
        }
        else {
            System.exit(0);
        }
    }

    private void carregarDados() {
        String caminhoMigration = diretorioService.obterCaminhoRaizSalvo("Migration");
        String caminhoModulo= diretorioService.obterCaminhoRaizSalvo("Modulo");
        if (caminhoMigration != null && !caminhoMigration.isEmpty()) {
            File pastaRaiz = new File(caminhoMigration);
            arquivoService.setpastaRaiz(pastaRaiz);
        }

        if (caminhoModulo != null && !caminhoModulo.isEmpty()) {
            File pastaRaiz = new File(caminhoModulo);
            moduloService.setpastaRaizModulosNovos(pastaRaiz);
        }

        painelModulo.atualizar();
        painelMigracao.atualizar();
    }

    public void atualizarLookAndFeel() {
        gerenciadorLayout.sincronizarUI(this);
    }
}