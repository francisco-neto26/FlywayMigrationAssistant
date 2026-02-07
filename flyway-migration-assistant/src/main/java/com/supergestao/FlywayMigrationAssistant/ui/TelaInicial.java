package com.supergestao.FlywayMigrationAssistant.ui;

import com.supergestao.FlywayMigrationAssistant.service.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.*;

public class TelaInicial extends JFrame {
    private final ArquivoService arquivoService = new ArquivoService();
    private final DiretorioService diretorioService = new DiretorioService();
    private final ModuloService moduloService = new ModuloService();
    private final GerenciadorLayout gerenciadorLayout = new GerenciadorLayout();

    private PainelModulo explorador;
    private PainelSql painelSql;
    private PainelMigration painelCriacao;

    public TelaInicial() {
        configurarJanela();
        montarComponentes();
        SwingUtilities.invokeLater(this::validarConfiguracoes);
    }

    private void configurarJanela() {
        setTitle("Super Gestão - Flyway Migration Assistant");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1100, 800));
        setLocationRelativeTo(null);
    }

    private void montarComponentes() {
        MenuOpcao menu = new MenuOpcao(this, diretorioService);
        setJMenuBar(menu.criarMenuBar(diretorioService));

        explorador = new PainelModulo(arquivoService, moduloService, diretorioService);
        painelSql = new PainelSql();
        painelCriacao = new PainelMigration(arquivoService);

        JPanel workspace = gerenciadorLayout.montarEstruturaCompleta(explorador, painelSql, painelCriacao);
        JPanel status = gerenciadorLayout.criarBarraStatusComPainel("Pronto.");

        setLayout(new BorderLayout());
        add(workspace, BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);

        vincularEventos();
    }

    private void vincularEventos() {
        explorador.addArquivoSelecionadoListener(arq -> {
            painelSql.displayArquivo(arq);
            painelCriacao.preencherCamposPeloArquivo(arq);
            gerenciadorLayout.atualizarStatus("Visualizando arquivo: " + arq.getnome());
        });

        painelCriacao.setTemplateListener((sql, editavel) -> {
            painelSql.setConteudo(sql, "Preview", editavel, false);
        });

        painelCriacao.setAcaoCriar(e -> {
            painelCriacao.limpar(true);
            painelSql.setEditable(true);
        });

        painelCriacao.setAcaoAlterar(e -> {
            painelCriacao.setEstadoInterface(false, true);
            painelSql.setEditable(true);
        });

        painelCriacao.setAcaoCancelar(e -> {
            painelCriacao.setEstadoInterface(false, false);
            painelSql.limpar("Visualizador SQL");
        });

        painelCriacao.setAcaoSalvar(e -> salvarProcesso());
        painelSql.setOnSave(sql -> salvarProcesso());
        explorador.setStatusListener(gerenciadorLayout::atualizarStatus);
    }

    private void salvarProcesso() {
        String nome = painelCriacao.getNome();
        File dir = painelCriacao.getModulo();
        if (nome.isEmpty() || dir == null) {
            JOptionPane.showMessageDialog(this, "Nome do arquivo ou módulo não definidos.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            File arquivoFinal = new File(dir, nome);
            if (arquivoFinal.exists()) {
                File pastaBackup = new File(dir, ".backup");
                if (!pastaBackup.exists()){
                    pastaBackup.mkdirs();
                }
                Files.copy(arquivoFinal.toPath(), new File(pastaBackup, arquivoFinal.getName() + "." + System.currentTimeMillis() + ".bak").toPath(), StandardCopyOption.REPLACE_EXISTING);
            }

            String relativo = arquivoService.getpastaRaiz().toPath().relativize(dir.toPath()).toString();
            arquivoService.criarArquivo(relativo, nome, painelSql.getTexto());

            JOptionPane.showMessageDialog(this, "Arquivo salvo com sucesso!");
            explorador.atualizar();
            painelCriacao.limpar(false);
            painelSql.limpar("Visualizador SQL");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void atualizarLookAndFeel() {
        gerenciadorLayout.sincronizarUI(this);
    }

    private void validarConfiguracoes() {
        if (!diretorioService.validaDiretorios()) {
            new TelaConfiguracao(this, diretorioService).setVisible(true);
            if (diretorioService.validaDiretorios()) carregarDados();
            else System.exit(0);
        } else carregarDados();
    }

    private void carregarDados() {
        String caminho = diretorioService.obterCaminhoRaizSalvo("Migration");
        if (caminho != null) arquivoService.setpastaRaiz(new File(caminho));
        explorador.atualizar();
        painelCriacao.atualizar();
    }
}