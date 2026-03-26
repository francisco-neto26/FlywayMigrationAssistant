package com.supergestao.FlywayMigrationAssistant.ui;

import com.supergestao.FlywayMigrationAssistant.model.*;
import com.supergestao.FlywayMigrationAssistant.service.ArquivoService;
import com.supergestao.FlywayMigrationAssistant.util.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.List;

public class PainelMigration extends JPanel {
    private final ArquivoService arquivoService;
    private JTextField descricaoCampo, previsualizacaoCampo;
    private JComboBox<File> comboBoxModulo;
    private JComboBox<Tipo> comboBoxTipo;
    private JComboBox<Acao> comboBoxAcao;
    private JComboBox<ObjetoBanco> comboBoxObjeto;
    private JComboBox<Arquivo> comboBoxArquivoOrigem;
    private JLabel labelAcao, labelObjeto, labelOrigem, labelDescricao;
    private JButton botaoCriar, botaoAlterar, botaoSalvar, botaoCancelar, botaoProsseguir;
    private java.util.function.BiConsumer<String, Boolean> templateListener;
    private boolean isCarregando = false;
    private boolean sqlEditavel = false;

    public PainelMigration(ArquivoService arquivoService) {
        this.arquivoService = arquivoService;
        inicializaPainel();
    }

    private void inicializaPainel() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("Gerenciador de Migration"));
        JPanel p = new JPanel(new GridBagLayout());
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0;
        p.add(new JLabel("Módulo:"), g);
        g.gridx = 1; g.weightx = 1.0;
        comboBoxModulo = new JComboBox<>();
        configurarRenderizador();
        comboBoxModulo.addActionListener(e -> { carregarOrigens(); atualizaPrevisualizacao(); });
        p.add(comboBoxModulo, g);

        g.gridx = 0; g.gridy = 1;
        p.add(new JLabel("Tipo:"), g);
        g.gridx = 1;
        comboBoxTipo = new JComboBox<>(Tipo.values());
        comboBoxTipo.addActionListener(e -> { gerenciarComponentes(); atualizaPrevisualizacao(); atualizarTemplate(); });
        p.add(comboBoxTipo, g);

        g.gridx = 0; g.gridy = 2;
        labelAcao = new JLabel("Ação:");
        p.add(labelAcao, g);
        g.gridx = 1;
        comboBoxAcao = new JComboBox<>(Acao.values());
        comboBoxAcao.addActionListener(e -> { filtrarObjetos(); atualizaPrevisualizacao(); atualizarTemplate(); });
        p.add(comboBoxAcao, g);

        g.gridx = 0; g.gridy = 3;
        labelObjeto = new JLabel("Objeto:");
        p.add(labelObjeto, g);
        g.gridx = 1;
        comboBoxObjeto = new JComboBox<>();
        comboBoxObjeto.addActionListener(e -> { atualizaPrevisualizacao(); atualizarTemplate(); });
        p.add(comboBoxObjeto, g);

        g.gridx = 0; g.gridy = 4;
        labelOrigem = new JLabel("Origem:");
        p.add(labelOrigem, g);
        g.gridx = 1;
        comboBoxArquivoOrigem = new JComboBox<>();
        comboBoxArquivoOrigem.addActionListener(e -> { vincularDescricao(); atualizaPrevisualizacao(); atualizarTemplate(); });
        p.add(comboBoxArquivoOrigem, g);

        g.gridx = 0; g.gridy = 5;
        labelDescricao = new JLabel("Descrição:");
        p.add(labelDescricao, g);
        g.gridx = 1;
        descricaoCampo = new JTextField();
        descricaoCampo.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { atualizaPrevisualizacao(); atualizarTemplate(); }
        });
        p.add(descricaoCampo, g);

        g.gridx = 0; g.gridy = 6;
        p.add(new JLabel("Nome Arquivo:"), g);
        g.gridx = 1;
        previsualizacaoCampo = new JTextField();
        previsualizacaoCampo.setEditable(false);
        previsualizacaoCampo.setBackground(new Color(230, 230, 230));
        p.add(previsualizacaoCampo, g);

        g.gridx = 0; g.gridy = 7; g.gridwidth = 2;
        JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botaoAlterar = new JButton("Alterar");
        botaoCriar = new JButton("Novo");
        botaoProsseguir = new JButton("Prosseguir para SQL");
        botaoSalvar = new JButton("Salvar");
        botaoCancelar = new JButton("Cancelar");

        bp.add(botaoAlterar); bp.add(botaoCriar); bp.add(botaoProsseguir); bp.add(botaoSalvar); bp.add(botaoCancelar);
        p.add(bp, g);

        add(p, BorderLayout.NORTH);

        // Ação interna do Prosseguir
        botaoProsseguir.addActionListener(e -> {
            if(validarCampos()) setEstadoInterface(false, true);
        });

        setEstadoInterface(false, false);
    }

    private boolean validarCampos() {
        if (descricaoCampo.getText().trim().isEmpty() && comboBoxTipo.getSelectedItem() != Tipo.UNDO) {
            JOptionPane.showMessageDialog(this, "Informe a descrição antes de prosseguir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (comboBoxModulo.getSelectedItem() == null) return false;
        return true;
    }

    public void setEstadoInterface(boolean camposEditaveis, boolean sqlEditavel) {
        this.sqlEditavel = sqlEditavel;

        // Controle de inputs
        comboBoxModulo.setEnabled(camposEditaveis);
        comboBoxTipo.setEnabled(camposEditaveis);
        comboBoxAcao.setEnabled(camposEditaveis);
        comboBoxObjeto.setEnabled(camposEditaveis);
        comboBoxArquivoOrigem.setEnabled(camposEditaveis);
        descricaoCampo.setEditable(camposEditaveis);
        labelDescricao.setVisible(camposEditaveis || !previsualizacaoCampo.getText().isEmpty());
        descricaoCampo.setVisible(camposEditaveis || !previsualizacaoCampo.getText().isEmpty());

        // Controle de Botões
        boolean modoVisualizacao = !camposEditaveis && !sqlEditavel;
        boolean modoDefinicao = camposEditaveis && !sqlEditavel;
        boolean modoEdicaoSql = !camposEditaveis && sqlEditavel;

        botaoCriar.setVisible(modoVisualizacao);
        Tipo t = (Tipo) comboBoxTipo.getSelectedItem();
        botaoAlterar.setVisible(modoVisualizacao && t != null && t != Tipo.VERSIONED);

        botaoProsseguir.setVisible(modoDefinicao);
        botaoSalvar.setVisible(modoEdicaoSql);
        botaoCancelar.setVisible(modoDefinicao || modoEdicaoSql);

        // Atualiza o editor SQL para refletir o novo estado de edição
        atualizarTemplate();

        revalidate();
        repaint();
    }

    public void preencherCamposPeloArquivo(Arquivo arq) {
        isCarregando = true;
        String nome = arq.getnome();
        comboBoxModulo.setSelectedItem(arq.getarquivo().getParentFile());

        if (nome.startsWith("V")) comboBoxTipo.setSelectedItem(Tipo.VERSIONED);
        else if (nome.startsWith("R")) comboBoxTipo.setSelectedItem(Tipo.REPEATABLE);
        else if (nome.startsWith("U")) comboBoxTipo.setSelectedItem(Tipo.UNDO);

        String[] partes = nome.split("__");
        if (partes.length > 1) {
            String miolo = partes[1].toLowerCase();
            for (Acao a : Acao.values()) {
                if (miolo.contains(a.getValorArquivo().toLowerCase())) {
                    comboBoxAcao.setSelectedItem(a);
                    break;
                }
            }
            filtrarObjetos();
            for (int i = 0; i < comboBoxObjeto.getItemCount(); i++) {
                ObjetoBanco obj = comboBoxObjeto.getItemAt(i);
                if (miolo.contains(obj.getValorArquivo().toLowerCase())) {
                    comboBoxObjeto.setSelectedItem(obj);
                    break;
                }
            }
        }
        previsualizacaoCampo.setText(nome);
        gerenciarComponentes();
        isCarregando = false;
        setEstadoInterface(false, false);
    }

    public void limpar(boolean editavel) {
        isCarregando = true;
        descricaoCampo.setText("");
        previsualizacaoCampo.setText("");
        comboBoxTipo.setSelectedIndex(0);
        gerenciarComponentes();
        isCarregando = false;
        setEstadoInterface(editavel, false); // Começa em modo de definição (Campos ON, SQL OFF)
    }

    private void atualizarTemplate() {
        if (isCarregando) return;
        Tipo t = (Tipo) comboBoxTipo.getSelectedItem();
        File d = (File) comboBoxModulo.getSelectedItem();
        String sql = "";

        // Se houver origem selecionada (UNDO/REPEATABLE existente)
        if (comboBoxArquivoOrigem.getSelectedItem() != null) {
            try {
                sql = arquivoService.lerConteudoArquivo(((Arquivo) comboBoxArquivoOrigem.getSelectedItem()).getarquivo());
            } catch (Exception e) {}
        } else if (t != null && d != null) {
            String mod = d.getAbsolutePath().replace(arquivoService.getpastaRaiz().getAbsolutePath(), arquivoService.getpastaRaiz().getName());
            sql = GeradorTemplateSql.gerar(descricaoCampo.getText(), t, (Acao) comboBoxAcao.getSelectedItem(), (ObjetoBanco) comboBoxObjeto.getSelectedItem(), mod);
        }

        if (templateListener != null) {
            templateListener.accept(sql, sqlEditavel);
        }
    }

    private void gerenciarComponentes() {
        Tipo t = (Tipo) comboBoxTipo.getSelectedItem();
        labelAcao.setVisible(t == Tipo.VERSIONED);
        comboBoxAcao.setVisible(t == Tipo.VERSIONED);
        labelObjeto.setVisible(t != Tipo.UNDO);
        comboBoxObjeto.setVisible(t != Tipo.UNDO);
        labelOrigem.setVisible(t == Tipo.UNDO || t == Tipo.REPEATABLE);
        comboBoxArquivoOrigem.setVisible(t == Tipo.UNDO || t == Tipo.REPEATABLE);
        filtrarObjetos();
        carregarOrigens();
    }

    private void vincularDescricao() {
        if (isCarregando) return;
        Arquivo o = (Arquivo) comboBoxArquivoOrigem.getSelectedItem();
        if (o != null && comboBoxTipo.getSelectedItem() == Tipo.UNDO && o.getnome().contains("__")) {
            descricaoCampo.setText(o.getnome().substring(o.getnome().indexOf("__") + 2).replace(".sql", ""));
        }
    }

    private void filtrarObjetos() {
        Acao a = (Acao) comboBoxAcao.getSelectedItem();
        Tipo t = (Tipo) comboBoxTipo.getSelectedItem();
        Object selecionadoObjeto = comboBoxObjeto.getSelectedItem();

        isCarregando = true;
        comboBoxObjeto.removeAllItems();

        if (t == Tipo.REPEATABLE) {
            Arrays.asList(ObjetoBanco.VIEW, ObjetoBanco.VIEW_MATERIALIZADA, ObjetoBanco.FUNCAO,
                            ObjetoBanco.PROCEDIMENTO, ObjetoBanco.GATILHO, ObjetoBanco.TIPO)
                    .forEach(comboBoxObjeto::addItem);
        } else if (t == Tipo.VERSIONED && a != null) {
            switch (a) {
                case CRIAR -> Arrays.asList(ObjetoBanco.TABELA, ObjetoBanco.SEQUENCIA, ObjetoBanco.ESQUEMA,
                        ObjetoBanco.EXTENSAO, ObjetoBanco.TIPO, ObjetoBanco.DOMINIO,
                        ObjetoBanco.INDICE, ObjetoBanco.POLITICA).forEach(comboBoxObjeto::addItem);
                case ALTERAR -> Arrays.asList(ObjetoBanco.TABELA, ObjetoBanco.COLUNA, ObjetoBanco.CONSTRAINT,
                        ObjetoBanco.SEQUENCIA).forEach(comboBoxObjeto::addItem);
                default -> Arrays.asList(ObjetoBanco.TABELA, ObjetoBanco.COLUNA, ObjetoBanco.CONSTRAINT,
                        ObjetoBanco.INDICE).forEach(comboBoxObjeto::addItem);
            }
        }

        if (selecionadoObjeto != null) comboBoxObjeto.setSelectedItem(selecionadoObjeto);
        isCarregando = false;
    }

    private void carregarOrigens() {
        comboBoxArquivoOrigem.removeAllItems();
        File d = (File) comboBoxModulo.getSelectedItem();
        Tipo t = (Tipo) comboBoxTipo.getSelectedItem();
        if (d != null && t != null) {
            arquivoService.obterArquivosModulo(d).forEach(arq -> {
                if (t == Tipo.UNDO && arq.getnome().startsWith("V")) comboBoxArquivoOrigem.addItem(arq);
                else if (t == Tipo.REPEATABLE && arq.getnome().startsWith("R")) comboBoxArquivoOrigem.addItem(arq);
            });
        }
    }

    public void setTemplateListener(java.util.function.BiConsumer<String, Boolean> l) { this.templateListener = l; }
    public void setAcaoCriar(ActionListener l) { botaoCriar.addActionListener(l); }
    public void setAcaoAlterar(ActionListener l) { botaoAlterar.addActionListener(l); }
    public void setAcaoSalvar(ActionListener l) { botaoSalvar.addActionListener(l); }
    public void setAcaoCancelar(ActionListener l) { botaoCancelar.addActionListener(l); }
    public File getModulo() { return (File) comboBoxModulo.getSelectedItem(); }
    public String getNome() { return previsualizacaoCampo.getText(); }

    public void atualizar() {
        comboBoxModulo.removeAllItems();
        File r = arquivoService.getpastaRaiz();
        if (r != null) {
            List<File> l = new ArrayList<>();
            l.add(r);
            addDirs(r, l);
            l.forEach(comboBoxModulo::addItem);
        }
    }

    private void addDirs(File p, List<File> r) {
        File[] f = p.listFiles(File::isDirectory);
        if (f != null) {
            Arrays.sort(f, Comparator.comparing(File::getName));
            for (File s : f) {
                r.add(s);
                addDirs(s, r);
            }
        }
    }

    private void configurarRenderizador() {
        comboBoxModulo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean s, boolean f) {
                super.getListCellRendererComponent(l, v, i, s, f);
                if (v instanceof File dir && arquivoService.getpastaRaiz() != null)
                    setText(dir.getAbsolutePath().replace(arquivoService.getpastaRaiz().getAbsolutePath(), arquivoService.getpastaRaiz().getName()));
                return this;
            }
        });
    }

    private void atualizaPrevisualizacao() {
        if (isCarregando || !comboBoxModulo.isEnabled()) return;
        File d = (File) comboBoxModulo.getSelectedItem();
        if (d == null) return;
        previsualizacaoCampo.setText(GeradorNomeArquivo.gerarNomeCompleto(descricaoCampo.getText(), (Acao) comboBoxAcao.getSelectedItem(), (ObjetoBanco) comboBoxObjeto.getSelectedItem(), d.getName(), (Tipo) comboBoxTipo.getSelectedItem(), (Arquivo) comboBoxArquivoOrigem.getSelectedItem()));
    }
}