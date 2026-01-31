package com.supergestao.FlywayMigrationAssistant.ui;

import com.supergestao.FlywayMigrationAssistant.model.Arquivo;
import com.supergestao.FlywayMigrationAssistant.model.Cores;
import com.supergestao.FlywayMigrationAssistant.service.ArquivoService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PainelArquivos extends JPanel {
    private final ArquivoService arquivoService;
    private JTextField campoPesquisa;
    private JList<Arquivo> listaArquivo;
    private DefaultListModel<Arquivo> listaModulo;
    private List<Arquivo> todosArquivos;
    private final List<SeletorArquivoListener> arquivoSelecionado;

    public PainelArquivos(ArquivoService arquivoService) {
        this.arquivoService = arquivoService;
        this.todosArquivos = new ArrayList<>();
        this.arquivoSelecionado = new ArrayList<>();
        inicializarPainelArquivos();
    }

    private void inicializarPainelArquivos() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createTitledBorder("Arquivos do Flyway Migration"));

        JPanel painelPesquisa = new JPanel(new BorderLayout(5, 5));
        painelPesquisa.setBorder(new EmptyBorder(5, 5, 5, 5));

        campoPesquisa = new JTextField();
        campoPesquisa.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filtrarArquivos(campoPesquisa.getText());
            }
        });

        painelPesquisa.add(new JLabel("Filtrar: "), BorderLayout.WEST);
        painelPesquisa.add(campoPesquisa, BorderLayout.CENTER);

        add(painelPesquisa, BorderLayout.NORTH);

        listaModulo = new DefaultListModel<>();
        listaArquivo = new JList<>(listaModulo);
        listaArquivo.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaArquivo.setCellRenderer(new RenderizarArquivosMigration());
        listaArquivo.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onArquivoSelecionado();
            }
        });

        JScrollPane scrollPainel = new JScrollPane(listaArquivo);
        add(scrollPainel, BorderLayout.CENTER);
    }

    public void carregarArquivos(File moduloNome) {
        todosArquivos = arquivoService.obterArquivosModulo(moduloNome);
        atualizarLista(todosArquivos);
        campoPesquisa.setText("");
    }

    private void filtrarArquivos(String filter) {
        if (filter.trim().isEmpty()) {
            atualizarLista(todosArquivos);
        } else {
            String filtrarMinusculas = filter.toLowerCase();
            List<Arquivo> filtradas = todosArquivos.stream()
                    .filter(f -> f.getnome().toLowerCase().contains(filtrarMinusculas))
                    .collect(Collectors.toList());
            atualizarLista(filtradas);
        }
    }

    private void atualizarLista(List<Arquivo> files) {
        listaModulo.clear();
        for (Arquivo file : files) {
            listaModulo.addElement(file);
        }
    }

    private void onArquivoSelecionado() {
        Arquivo selecionado = listaArquivo.getSelectedValue();
        if (selecionado != null) {
            notificaListeners(selecionado);
        }
    }

    public void addSeletorArquivosListener(SeletorArquivoListener listener) {
        arquivoSelecionado.add(listener);
    }

    private void notificaListeners(Arquivo file) {
        for (SeletorArquivoListener listener : arquivoSelecionado) {
            listener.onArquivoSelecionado(file);
        }
    }

    @FunctionalInterface
    public interface SeletorArquivoListener {
        void onArquivoSelecionado(Arquivo file);
    }

    private static class RenderizarArquivosMigration extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean isSelected,
                                                      boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof Arquivo arquivos) {
                setText(arquivos.getnome());

                if (!isSelected) {
                    if (arquivos.getnome().startsWith("V")) {
                        setForeground(Cores.VERDE_SUCESSO.getCor());
                    } else if (arquivos.getnome().startsWith("R")) {
                        setForeground(Cores.VERMELHO_ALERTA.getCor());
                    } else {
                        setForeground(Cores.AZUL_PADRAO.getCor());
                    }
                }
            }

            return this;
        }
    }
}
