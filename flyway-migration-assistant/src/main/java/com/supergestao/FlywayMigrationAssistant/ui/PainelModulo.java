package com.supergestao.FlywayMigrationAssistant.ui;

import com.supergestao.FlywayMigrationAssistant.service.ArquivoService;
import com.supergestao.FlywayMigrationAssistant.service.DiretorioService;
import com.supergestao.FlywayMigrationAssistant.service.ModuloService;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
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

        configurarArvore();
        configurarBotoes();
    }

    private void configurarArvore() {
        String caminhoRaiz = diretorioService.obterCaminhoRaizSalvo("Migration");
        DiretorioTreeNode root = new DiretorioTreeNode(new File(caminhoRaiz));

        arvoreModelo = new DefaultTreeModel(root);
        arvoreModulos = new JTree(arvoreModelo);
        arvoreModulos.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        arvoreModulos.addTreeSelectionListener(e -> moduloSelecionado());
        arvoreModulos.addTreeWillExpandListener(new javax.swing.event.TreeWillExpandListener() {
            @Override
            public void treeWillExpand(javax.swing.event.TreeExpansionEvent event) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();
                if (node instanceof DiretorioTreeNode pastaNode && !pastaNode.isCarregado()) {
                    carregarSubPastas(pastaNode);
                }
            }
            @Override
            public void treeWillCollapse(javax.swing.event.TreeExpansionEvent event) {}
        });

        arvoreModulos.setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
                                                          boolean exp, boolean leaf, int row, boolean hasFocus) {
                super.getTreeCellRendererComponent(tree, value, sel, exp, leaf, row, hasFocus);
                if (value instanceof DiretorioTreeNode node && node.getFile().isDirectory()) {
                    setIcon(exp ? getOpenIcon() : getClosedIcon());
                }
                return this;
            }
        });

        add(new JScrollPane(arvoreModulos), BorderLayout.CENTER);
    }

    private void configurarBotoes() {
        JPanel painelBotoes = new JPanel(new GridLayout(2, 1, 5, 5));
        painelBotoes.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JButton btnAtualizar = new JButton("🔄 Atualizar");
        btnAtualizar.addActionListener(e -> atualizar());

        JButton btnNovoModulo = new JButton("➕ Novo Módulo");
        btnNovoModulo.addActionListener(e -> CriaNovoModulo());

        painelBotoes.add(btnAtualizar);
        painelBotoes.add(btnNovoModulo);
        add(painelBotoes, BorderLayout.SOUTH);
    }

    public void atualizar() {
        validaExisteModuloASerCriado();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) arvoreModelo.getRoot();
        root.removeAllChildren();

        String caminhoRaiz = diretorioService.obterCaminhoRaizSalvo("Migration");
        File pastaRaiz = new File(caminhoRaiz);

        if (pastaRaiz.exists() && pastaRaiz.isDirectory()) {
            File[] modulos = pastaRaiz.listFiles(File::isDirectory);
            if (modulos != null) {
                Arrays.sort(modulos, Comparator.comparing(File::getName));
                for (File m : modulos) {
                    root.add(new DiretorioTreeNode(m));
                }
            }
        }
        arvoreModelo.reload();
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

    private void carregarSubPastas(DiretorioTreeNode pai) {
        pai.removeAllChildren();
        File diretorio = pai.getFile();
        File[] arquivos = diretorio.listFiles(File::isDirectory);

        if (arquivos != null) {
            Arrays.sort(arquivos, Comparator.comparing(File::getName));
            for (File f : arquivos) {
                pai.add(new DiretorioTreeNode(f));
            }
        }
        pai.setCarregado(true);
        arvoreModelo.nodeStructureChanged(pai);
    }


    private void moduloSelecionado() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)
                arvoreModulos.getLastSelectedPathComponent();

        if (node != null && !node.isRoot()) {
            String moduloNome = node.getUserObject().toString();
            notificaListeners(moduloNome);
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
