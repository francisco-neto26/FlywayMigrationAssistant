package com.supergestao.FlywayMigrationAssistant.ui;

import com.supergestao.FlywayMigrationAssistant.model.Tipo;
import com.supergestao.FlywayMigrationAssistant.service.ArquivoService;
import com.supergestao.FlywayMigrationAssistant.util.GeradorNomeArquivo;
import com.supergestao.FlywayMigrationAssistant.util.GeradorTemplateSql;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PainelMigration extends JPanel {
    private ArquivoService arquivoService;
    private JTextField descricaoCampo;
    private JComboBox<String> comboBoxModulo;
    private JRadioButton BotaoVersioned;
    private JRadioButton BotaoRepeatable;
    private JRadioButton BotaoUndo;
    private JTextField previsualizacaoCampo;
    private JTextArea areaTemplate;
    private List<ArquivoCriadoListener> listeners;

    public PainelMigration(ArquivoService arquivoService) {
        this.arquivoService = arquivoService;
        this.listeners = new ArrayList<>();
        inicializaPainelMigration();
    }

    private void inicializaPainelMigration() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("Criar Nova Migração"));

        JPanel PainelMigration = new JPanel(new GridBagLayout());
        PainelMigration.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        PainelMigration.add(new JLabel("Descrição:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        descricaoCampo = new JTextField(25);
        descricaoCampo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                atualizaPrevisualizacao();
            }
        });
        PainelMigration.add(descricaoCampo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        PainelMigration.add(new JLabel("Módulo:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        comboBoxModulo = new JComboBox<>();
        comboBoxModulo.addActionListener(e -> atualizaPrevisualizacao());
        PainelMigration.add(comboBoxModulo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        PainelMigration.add(new JLabel("Tipo:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JPanel PainelTipo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ButtonGroup GrupoBotaoTipo = new ButtonGroup();
        BotaoVersioned = new JRadioButton("V - Imutável (Tabelas/Colunas)", true);
        BotaoRepeatable = new JRadioButton("R - Mutável (Funções/Views)");
        GrupoBotaoTipo.add(BotaoVersioned);
        GrupoBotaoTipo.add(BotaoRepeatable);
        PainelTipo.add(BotaoVersioned);
        PainelTipo.add(BotaoRepeatable);

        BotaoVersioned.addActionListener(e -> {
            atualizaPrevisualizacao();
            atualizarTemplate();
        });
        BotaoRepeatable.addActionListener(e -> {
            atualizaPrevisualizacao();
            atualizarTemplate();
        });

        PainelMigration.add(PainelTipo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        PainelMigration.add(new JLabel("Pré-Visualização:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        previsualizacaoCampo = new JTextField();
        previsualizacaoCampo.setEditable(false);
        previsualizacaoCampo.setBackground(new Color(240, 240, 240));
        previsualizacaoCampo.setFont(new Font("Monospaced", Font.PLAIN, 11));
        PainelMigration.add(previsualizacaoCampo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JPanel botoesPainel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton botaoLimpar = new JButton("Limpar");
        botaoLimpar.addActionListener(e -> limparFormulario());

        JButton botaoCriarArquivo = new JButton("Criar Arquivo");
        botaoCriarArquivo.addActionListener(e -> criaArquivo());

        botoesPainel.add(botaoLimpar);
        botoesPainel.add(botaoCriarArquivo);
        PainelMigration.add(botoesPainel, gbc);

        add(PainelMigration, BorderLayout.NORTH);

        JPanel PainelTemplate = new JPanel(new BorderLayout(5, 5));
        PainelTemplate.setBorder(BorderFactory.createTitledBorder("Template SQL"));

        areaTemplate = new JTextArea();
        areaTemplate.setFont(new Font("Monospaced", Font.PLAIN, 11));
        areaTemplate.setLineWrap(false);
        areaTemplate.setTabSize(4);

        JScrollPane scrollPainel = new JScrollPane(areaTemplate);
        PainelTemplate.add(scrollPainel, BorderLayout.CENTER);

        add(PainelTemplate, BorderLayout.CENTER);

        atualizarTemplate();
    }

    public void atualizar() {
        comboBoxModulo.removeAllItems();
        List<String> modulos = arquivoService.getModulos();
        for (String modulo : modulos) {
            comboBoxModulo.addItem(modulo);
        }
    }

    private void atualizaPrevisualizacao() {
        String descricao = descricaoCampo.getText().trim();
        String modulo = (String) comboBoxModulo.getSelectedItem();

        if (descricao.isEmpty() || modulo == null) {
            previsualizacaoCampo.setText("");
            return;
        }

        Tipo tipo = BotaoVersioned.isSelected() ?
                Tipo.VERSIONED : Tipo.REPEATABLE;

        String nomeArquivo = GeradorNomeArquivo.geradorNomeArquivo(descricao, modulo, tipo);
        previsualizacaoCampo.setText(nomeArquivo);
    }

    private void atualizarTemplate() {
        String descricao = descricaoCampo.getText().trim();
        if (descricao.isEmpty()) {
            descricao = "Nova migração";
        }

        Tipo tipo = BotaoVersioned.isSelected() ?
                Tipo.VERSIONED : Tipo.REPEATABLE;

        String template = GeradorTemplateSql.geradorTemplateSql(descricao, tipo);
        areaTemplate.setText(template);
        areaTemplate.setCaretPosition(0);
    }

    private void criaArquivo() {
        String descricao = descricaoCampo.getText().trim();
        String modulo = (String) comboBoxModulo.getSelectedItem();
        String nomeArquivo = previsualizacaoCampo.getText();

        if (descricao.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, preencha a descrição",
                    "Validação", JOptionPane.WARNING_MESSAGE);
            descricaoCampo.requestFocus();
            return;
        }

        if (modulo == null) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, selecione um módulo",
                    "Validação", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (arquivoService.getpastaRaiz() == null) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, selecione a pasta db/migration primeiro",
                    "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (arquivoService.existeArquivo(modulo, nomeArquivo)) {
            int result = JOptionPane.showConfirmDialog(this,
                    "O arquivo já existe. Deseja sobrescrever?",
                    "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (result != JOptionPane.YES_OPTION) {
                return;
            }
        }

        try {
            String conteudo = areaTemplate.getText();
            arquivoService.criarArquivo(modulo, nomeArquivo, conteudo);

            JOptionPane.showMessageDialog(this,
                    "Arquivo criado com sucesso!\n\n" +
                            "Módulo: " + modulo + "\n" +
                            "Arquivo: " + nomeArquivo,
                    "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            limparFormulario();
            notificaListeners();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao criar arquivo:\n\n" + e.getMessage(),
                    "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparFormulario() {
        descricaoCampo.setText("");
        previsualizacaoCampo.setText("");
        atualizarTemplate();
        descricaoCampo.requestFocus();
    }

    public void addArquivoCriadoListener(ArquivoCriadoListener listener) {
        listeners.add(listener);
    }

    private void notificaListeners() {
        for (ArquivoCriadoListener listener : listeners) {
            listener.onArquivoCriado();
        }
    }

    @FunctionalInterface
    public interface ArquivoCriadoListener {
        void onArquivoCriado();
    }
}
