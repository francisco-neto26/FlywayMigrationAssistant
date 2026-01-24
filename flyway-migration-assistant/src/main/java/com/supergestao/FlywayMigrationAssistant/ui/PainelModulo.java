package com.supergestao.FlywayMigrationAssistant.ui;

import com.supergestao.FlywayMigrationAssistant.service.ArquivoService;
import com.supergestao.FlywayMigrationAssistant.service.DiretorioService;
import com.supergestao.FlywayMigrationAssistant.service.ModuloService;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class PainelModulo extends JPanel {
    private final ArquivoService arquivoService;
    private final ModuloService moduloService;
    private final DiretorioService diretorioService;
    private JTree arvoreModulos;
    private DefaultTreeModel arvoreModelo;
    private final List<SeletorModuloListener> moduloSelecionado;

    public PainelModulo(ArquivoService arquivoService, ModuloService moduloService, DiretorioService diretorioService) {
        this.arquivoService = arquivoService;
        this.moduloService = moduloService;
        this.diretorioService = diretorioService;
        this.moduloSelecionado = new ArrayList<>();
        inicializarPainelModulo();
    }

    private void inicializarPainelModulo() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("Módulos"));

        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Migration");
        arvoreModelo = new DefaultTreeModel(root);
        arvoreModulos = new JTree(arvoreModelo);
        arvoreModulos.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        arvoreModulos.addTreeSelectionListener(e -> moduloSelecionado());

        JScrollPane scrollPainel = new JScrollPane(arvoreModulos);
        add(scrollPainel, BorderLayout.CENTER);

        JPanel botaoNovoModulo = new JPanel(new GridLayout(2, 1, 5, 5));
        botaoNovoModulo.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JButton botaoAtualizar = new JButton("🔄 Atualizar");
        botaoAtualizar.addActionListener(e -> atualizar());
        botaoNovoModulo.add(botaoAtualizar);

        JButton newModuleButton = new JButton("➕ Novo Módulo");
        newModuleButton.addActionListener(e -> CriaNovoModulo());
        botaoNovoModulo.add(newModuleButton);

        add(botaoNovoModulo, BorderLayout.SOUTH);
    }

    public void atualizar() {
        validaExisteModuloASerCriado();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) arvoreModelo.getRoot();
        root.removeAllChildren();

        List<String> modulos = arquivoService.getModulos();
        for (String modulo : modulos) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(modulo);
            root.add(node);
        }
        arvoreModelo.reload();
        expandirTodos();
    }

    public void validaExisteModuloASerCriado() {
        Set<String> modulosExistente = new TreeSet<>(moduloService.obterModulos(diretorioService.obterCaminhoRaizSalvo("Migration")));
        Set<String> modulosCriar = new TreeSet<>(moduloService.obterModulosMainEnums(
                diretorioService.obterCaminhoRaizSalvo("Modulo") + File.separator + "ModuloEnum.java"));
        modulosCriar.removeAll(modulosExistente);
        for (String modulo : modulosCriar) {
            Object[] opcoesBotoes = {"Criar", "Sair"};
            int opcao = JOptionPane.showOptionDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "O Módulo " + modulo + " não existe. " +
                            "\n Deseja criar o módulo?.",
                    "Criação de Módulos",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    opcoesBotoes,
                    opcoesBotoes[0]);
            if (opcao == 0) {
                moduloService.criarModulo(modulo, diretorioService.obterCaminhoRaizSalvo("Migration"));
            }
        }
    }


    private void expandirTodos() {
        for (int i = 0; i < arvoreModulos.getRowCount(); i++) {
            arvoreModulos.expandRow(i);
        }
    }

    private void moduloSelecionado() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                arvoreModulos.getLastSelectedPathComponent();

        if (node != null && !node.isRoot()) {
            String modulonome = node.getUserObject().toString();
            notificaListeners(modulonome);
        }
    }

    public String obterModuloSelecionado() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                arvoreModulos.getLastSelectedPathComponent();

        if (node != null && !node.isRoot()) {
            return node.getUserObject().toString();
        }
        return null;
    }

    public void addModuloSelecionadoListener(SeletorModuloListener listener) {
        moduloSelecionado.add(listener);
    }

    private void notificaListeners(String module) {
        for (SeletorModuloListener listener : moduloSelecionado) {
            listener.onModuloSelecionado(module);
        }
    }

    private void CriaNovoModulo() {
        if (arquivoService.getpastaRaiz() == null) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, selecione primeiro a pasta db/migration",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JPanel painel = new JPanel(new GridLayout(3, 2, 5, 5));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextField nomeCampo = new JTextField(20);
        JTextField prefixoCampo = new JTextField(5);

        painel.add(new JLabel("Nome do Módulo:"));
        painel.add(nomeCampo);
        painel.add(new JLabel("Prefixo (A-Z):"));
        painel.add(prefixoCampo);
        painel.add(new JLabel("Exemplo:"));
        painel.add(new JLabel("pedidos, H"));

        int retornoCriarModulo = JOptionPane.showConfirmDialog(this, painel,
                "Criar Novo Módulo", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (retornoCriarModulo == JOptionPane.OK_OPTION) {
            String nomeModulo = nomeCampo.getText().trim().toLowerCase();
            String prefixo = prefixoCampo.getText().trim().toUpperCase();

            if (nomeModulo.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Nome do módulo não pode ser vazio",
                        "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (prefixo.isEmpty() || prefixo.length() != 3 || !prefixo.matches("[A-Z]")) {
                JOptionPane.showMessageDialog(this,
                        "Prefixo deve ser de 3 letras (A-Z)",
                        "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                boolean criado = arquivoService.criarModulo(nomeModulo, prefixo);
                if (criado) {
                    atualizar();
                    JOptionPane.showMessageDialog(this,
                            "Módulo '" + nomeModulo + "' criado com sucesso!\nPrefixo: " + prefixo,
                            "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Módulo '" + nomeModulo + "' já existe",
                            "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Erro ao criar módulo: " + ex.getMessage(),
                        "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @FunctionalInterface
    public interface SeletorModuloListener {
        void onModuloSelecionado(String modulo);
    }
}
