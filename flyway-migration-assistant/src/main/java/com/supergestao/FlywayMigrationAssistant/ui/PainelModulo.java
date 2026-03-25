package com.supergestao.FlywayMigrationAssistant.ui;

import com.supergestao.FlywayMigrationAssistant.model.Arquivo;
import com.supergestao.FlywayMigrationAssistant.model.Cores;
import com.supergestao.FlywayMigrationAssistant.service.ArquivoService;
import com.supergestao.FlywayMigrationAssistant.service.DiretorioService;
import com.supergestao.FlywayMigrationAssistant.service.ModuloService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.*;
import java.util.List;

public class PainelModulo extends JPanel {
    private final ArquivoService arquivoService;
    private final ModuloService moduloService;
    private final DiretorioService diretorioService;
    private JTree arvoreExplorador;
    private DefaultTreeModel modeloArvore;
    private JTextField campoPesquisa;
    private final List<SeletorArquivoListener> arquivoSelecionadoListeners = new ArrayList<>();
    private java.util.function.Consumer<String> statusListener;
    private Runnable acaoPosCriarModulo;

    public PainelModulo(ArquivoService arquivoService, ModuloService moduloService, DiretorioService diretorioService) {
        this.arquivoService = arquivoService;
        this.moduloService = moduloService;
        this.diretorioService = diretorioService;
        inicializarExplorador();
    }

    private void inicializarExplorador() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("Explorador de Arquivos"));

        JPanel painelBusca = new JPanel(new BorderLayout(5, 5));
        painelBusca.setBorder(new EmptyBorder(5, 5, 5, 5));
        campoPesquisa = new JTextField();
        campoPesquisa.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filtrarArquivos(campoPesquisa.getText());
            }
        });

        painelBusca.add(new JLabel("Filtrar: "), BorderLayout.WEST);
        painelBusca.add(campoPesquisa, BorderLayout.CENTER);
        add(painelBusca, BorderLayout.NORTH);

        configurarArvore();
        configurarBotoes();
    }

    private void configurarArvore() {
        String raizCaminho = diretorioService.obterCaminhoRaizSalvo("Migration");
        DiretorioTreeNode raiz = new DiretorioTreeNode(new File(raizCaminho));
        modeloArvore = new DefaultTreeModel(raiz);
        arvoreExplorador = new JTree(modeloArvore);
        arvoreExplorador.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        arvoreExplorador.addTreeSelectionListener(e -> tratarSelecao());
        arvoreExplorador.addTreeWillExpandListener(new javax.swing.event.TreeWillExpandListener() {
            @Override
            public void treeWillExpand(javax.swing.event.TreeExpansionEvent event) {
                DefaultMutableTreeNode no = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();
                if (no instanceof DiretorioTreeNode pasta && !pasta.isCarregado()) carregarConteudo(pasta);
            }

            @Override
            public void treeWillCollapse(javax.swing.event.TreeExpansionEvent event) {
            }
        });

        arvoreExplorador.setCellRenderer(new RenderizadorExplorador());
        add(new JScrollPane(arvoreExplorador), BorderLayout.CENTER);
    }

    private void carregarConteudo(DiretorioTreeNode pai) {
        pai.removeAllChildren();
        File dir = pai.getFile();
        File[] subs = obterSubPastas(dir);
        if (subs != null) {
            Arrays.sort(subs, Comparator.comparing(File::getName));
            for (File s : subs) pai.add(new DiretorioTreeNode(s));
        }
        List<Arquivo> sql = arquivoService.obterArquivosModulo(dir);
        for (Arquivo arq : sql) pai.add(new DefaultMutableTreeNode(arq, false));
        pai.setCarregado(true);
        modeloArvore.nodeStructureChanged(pai);
    }

    private void filtrarArquivos(String textoFiltro) {
        if (textoFiltro == null || textoFiltro.trim().isEmpty()) {
            atualizar();
            enviarMensagemStatus("Pronto");
            return;
        }
        String termoBusca = textoFiltro.toLowerCase().trim();
        enviarMensagemStatus("Pesquisando por: " + termoBusca);
        String caminhoRaiz = diretorioService.obterCaminhoRaizSalvo("Migration");
        DiretorioTreeNode raizResultados = new DiretorioTreeNode(new File(caminhoRaiz));
        if (montarArvoreFiltrada(raizResultados, termoBusca)) {
            modeloArvore.setRoot(raizResultados);
            for (int i = 0; i < arvoreExplorador.getRowCount(); i++) arvoreExplorador.expandRow(i);
        } else {
            modeloArvore.setRoot(new DefaultMutableTreeNode("Nenhum resultado encontrado"));
        }
    }

    private boolean montarArvoreFiltrada(DiretorioTreeNode pai, String filtro) {
        File diretorio = pai.getFile();
        boolean temConteudoUtil = false;
        File[] subPastas = obterSubPastas(diretorio);
        if (subPastas != null) {
            for (File sub : subPastas) {
                DiretorioTreeNode noSub = new DiretorioTreeNode(sub);
                if (montarArvoreFiltrada(noSub, filtro)) {
                    pai.add(noSub);
                    temConteudoUtil = true;
                }
            }
        }
        List<Arquivo> arquivosNestaPasta = arquivoService.obterArquivosModulo(diretorio);
        for (Arquivo arq : arquivosNestaPasta) {
            if (arq.getnome().toLowerCase().contains(filtro)) {
                pai.add(new DefaultMutableTreeNode(arq, false));
                temConteudoUtil = true;
            }
        }
        pai.setCarregado(true);
        return temConteudoUtil;
    }

    private File[] obterSubPastas(File diretorio){
        return diretorio.listFiles(f -> f.isDirectory() && !f.getName().equals(".backup"));
    }

    private void tratarSelecao() {
        DefaultMutableTreeNode no = (DefaultMutableTreeNode) arvoreExplorador.getLastSelectedPathComponent();
        if (no != null && no.getUserObject() instanceof Arquivo arq) notificarListeners(arq);
    }

    public void atualizar() {
        Object raizObjeto = modeloArvore.getRoot();
        if (raizObjeto instanceof DiretorioTreeNode raiz) {
            raiz.setCarregado(false);
            carregarConteudo(raiz);
        } else {
            String raizCaminho = diretorioService.obterCaminhoRaizSalvo("Migration");
            DiretorioTreeNode novaRaiz = new DiretorioTreeNode(new File(raizCaminho));
            modeloArvore.setRoot(novaRaiz);
            carregarConteudo(novaRaiz);
        }
        limparFormulario();
        modeloArvore.reload();
    }

    private void limparFormulario() {
        campoPesquisa.setText("");
        campoPesquisa.requestFocus();
    }

    private void configurarBotoes() {
        JPanel painelBotoes = new JPanel(new GridLayout(2, 1, 5, 5));
        painelBotoes.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JButton btnAtualizar = new JButton("Atualizar");
        btnAtualizar.addActionListener(e -> atualizar());
        JButton btnNovoModulo = new JButton("Novo Módulo");
        btnNovoModulo.addActionListener(e -> criaNovoModulo());
        painelBotoes.add(btnAtualizar);
        painelBotoes.add(btnNovoModulo);
        add(painelBotoes, BorderLayout.SOUTH);
    }

    public void addArquivoSelecionadoListener(SeletorArquivoListener l) {
        arquivoSelecionadoListeners.add(l);
    }

    private void notificarListeners(Arquivo a) {
        for (SeletorArquivoListener l : arquivoSelecionadoListeners) l.onArquivoSelecionado(a);
    }

    public File obterModuloSelecionado() {
        DefaultMutableTreeNode no = (DefaultMutableTreeNode) arvoreExplorador.getLastSelectedPathComponent();
        if (no instanceof DiretorioTreeNode dir) return dir.getFile();
        if (no != null && no.getParent() instanceof DiretorioTreeNode pai) return pai.getFile();
        return null;
    }

    private static class RenderizadorExplorador extends DefaultTreeCellRenderer {
        @Override
        public Component getTreeCellRendererComponent(JTree t, Object v, boolean s, boolean e, boolean l, int r, boolean h) {
            super.getTreeCellRendererComponent(t, v, s, e, l, r, h);
            if (v instanceof DefaultMutableTreeNode no) {
                Object obj = no.getUserObject();
                if (obj instanceof Arquivo arq) {
                    setText(arq.getnome());
                    setIcon(UIManager.getIcon("FileView.fileIcon"));
                    if (!s) {
                        if (arq.getnome().startsWith("V")) setForeground(Cores.VERDE_SUCESSO.getCor());
                        else if (arq.getnome().startsWith("R")) setForeground(Cores.VERMELHO_ALERTA.getCor());
                    }
                } else if (obj instanceof File f && f.isDirectory()) {
                    setIcon(e ? getOpenIcon() : getClosedIcon());
                }
            }
            return this;
        }
    }

    @FunctionalInterface
    public interface SeletorArquivoListener {
        void onArquivoSelecionado(Arquivo a);
    }

    private void criaNovoModulo() {
        String raizCaminho = diretorioService.obterCaminhoRaizSalvo("Migration");
        File raizFisica = new File(raizCaminho);
        List<File> todosDiretorios = new ArrayList<>();
        todosDiretorios.add(raizFisica);
        listarDiretoriosRecursivo(raizFisica, todosDiretorios);

        JComboBox<File> seletorLocal = new JComboBox<>(new Vector<>(todosDiretorios));
        seletorLocal.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof File f) {
                    String relativo = f.getAbsolutePath().replace(raizFisica.getAbsolutePath(), raizFisica.getName());
                    setText(relativo);
                }
                return this;
            }
        });
        File selecionadoNaArvore = obterModuloSelecionado();
        if (selecionadoNaArvore != null) seletorLocal.setSelectedItem(selecionadoNaArvore);

        JPanel painel = new JPanel(new GridLayout(2, 2, 5, 8));
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JTextField nomeCampo = new JTextField(20);

        painel.add(new JLabel("Modulo superior:"));
        painel.add(seletorLocal);
        painel.add(new JLabel("Nome do Módulo:"));
        painel.add(nomeCampo);

        int retorno = JOptionPane.showConfirmDialog(SwingUtilities.getWindowAncestor(this), painel, "Nova Pasta de Módulo", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (retorno == JOptionPane.OK_OPTION) {
            String nomeModulo = nomeCampo.getText().trim();
            File paiSelecionado = (File) seletorLocal.getSelectedItem();

            if (nomeModulo.isEmpty() || paiSelecionado == null) {
                JOptionPane.showMessageDialog(this, "Preencha todos os campos.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            File novaPasta = new File(paiSelecionado, nomeModulo);
            if (novaPasta.mkdirs()) {
                atualizar();
                if (acaoPosCriarModulo != null) {
                    acaoPosCriarModulo.run();
                }
                enviarMensagemStatus("Módulo '" + nomeModulo + "' criado com sucesso.");
            } else {
                JOptionPane.showMessageDialog(this, "Não foi possível criar a pasta.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void listarDiretoriosRecursivo(File pastaPai, List<File> listaResultado) {
        File[] filhos = obterSubPastas(pastaPai);
        if (filhos != null) {
            Arrays.sort(filhos, Comparator.comparing(File::getName));
            for (File f : filhos) {
                listaResultado.add(f);
                listarDiretoriosRecursivo(f, listaResultado);
            }
        }
    }

    public void setStatusListener(java.util.function.Consumer<String> listener) {

        this.statusListener = listener;
    }

    private void enviarMensagemStatus(String msg) {

        if (statusListener != null){
            statusListener.accept(msg);
        }
    }

    public void setAcaoPosCriarModulo(Runnable r) {
        this.acaoPosCriarModulo = r;
    }
}