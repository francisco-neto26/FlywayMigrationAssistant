package com.supergestao.Flyway.migration.assistant.ui.utilitario.editorSql;

import javafx.scene.Node;
import javafx.scene.control.Label;
import org.fxmisc.richtext.CodeArea;

import java.util.TreeSet;

public class GerenciadorColunaNumeracao {

    private final GerenciadorEditorSql editor;
    private final CodeArea codeArea;
    private final TreeSet<Integer> linhasMarcadas = new TreeSet<>();

    public GerenciadorColunaNumeracao(GerenciadorEditorSql editor) {
        this.editor = editor;
        this.codeArea = editor.getCodeArea();

        // Escuta alterações na quantidade de linhas para redimensionar a barra dinamicamente
        this.codeArea.getParagraphs().sizeProperty().addListener((obs, antigo, novo) -> {
            atualizarFabricaNumeracao();
        });
    }

    public boolean isMarcada(int line) {
        return linhasMarcadas.contains(line);
    }

    public void toggleMarcador(int line) {
        if (linhasMarcadas.contains(line)) {
            linhasMarcadas.remove(line);
        } else {
            linhasMarcadas.add(line);
        }
        atualizarFabricaNumeracao(); // Redesenha a coluna
    }

    /**
     * Cria e monta a fábrica de numeração de linhas com tamanho dinâmico (com base no total de linhas),
     * números centralizados e destaque de fundo para marcadores.
     */
    public void atualizarFabricaNumeracao() {
        int tamanhoFonte = editor.getTamanhoFonte();
        String fonteSitema = editor.getFonteSitema();

        // 1. Calcula dinamicamente o número de dígitos necessários (mínimo 2 dígitos de espaço)
        int totalLinhas = codeArea.getParagraphs().size();
        int numeroDigitos = Math.max(2, String.valueOf(totalLinhas).length());

        // 2. Define o padding e a largura dinâmica proporcional ao tamanho da fonte e dígitos
        double paddingLateral = tamanhoFonte * 0.4;
        double larguraDinamica = (numeroDigitos * (tamanhoFonte * 0.6)) + (paddingLateral * 2);

        this.codeArea.setParagraphGraphicFactory(line -> {
            Label label = new Label();

            label.getStyleClass().add("lineno");

            // Centraliza o número da linha na caixa física
            label.setAlignment(javafx.geometry.Pos.CENTER);

            // Aplica a largura calculada dinamicamente para toda a coluna
            label.setMinWidth(larguraDinamica);
            label.setPrefWidth(larguraDinamica);
            label.setMaxWidth(larguraDinamica);

            boolean estaMarcada = isMarcada(line);

            label.setOnMouseClicked(event -> {
                if (event.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
                    toggleMarcador(line);
                }
            });

            // Ambos exibem apenas o número puro (sem a bolinha)
            label.setText(String.valueOf(line + 1));

            if (estaMarcada) {
                // Altera apenas o fundo (azul escuro) e a cor do texto para branco
                label.setStyle(
                        "-fx-font-family: '" + (fonteSitema != null ? fonteSitema : "monospace") + "'; " +
                                "-fx-font-size: " + tamanhoFonte + "px; " +
                                "-fx-padding: 0 " + paddingLateral + " 0 " + paddingLateral + "; " +
                                "-fx-text-overrun: clip; " +
                                "-fx-background-color: #005a9e; " +
                                "-fx-text-fill: #ffffff;"
                );
            } else {
                // Fundo normal herdado do CSS
                label.setStyle(
                        "-fx-font-family: '" + (fonteSitema != null ? fonteSitema : "monospace") + "'; " +
                                "-fx-font-size: " + tamanhoFonte + "px; " +
                                "-fx-padding: 0 " + paddingLateral + " 0 " + paddingLateral + "; " +
                                "-fx-text-overrun: clip;"
                );
            }

            return label;
        });
    }

    public void irParaProximoMarcador(boolean reverso) {
        if (linhasMarcadas.isEmpty()) {
            return;
        }

        int linhaAtual = codeArea.getCurrentParagraph();
        Integer destino = null;

        if (reverso) {
            for (Integer linha : linhasMarcadas) {
                if (linha < linhaAtual) {
                    destino = linha;
                }
            }
            if (destino == null) {
                destino = linhasMarcadas.last();
            }
        } else {
            for (Integer linha : linhasMarcadas) {
                if (linha > linhaAtual) {
                    destino = linha;
                    break;
                }
            }
            if (destino == null) {
                destino = linhasMarcadas.first();
            }
        }

        if (destino != null) {
            codeArea.moveTo(destino, 0);
            codeArea.showParagraphAtCenter(destino);
        }
    }

    public void limparMarcadores() {
        linhasMarcadas.clear();
        atualizarFabricaNumeracao();
    }
}
