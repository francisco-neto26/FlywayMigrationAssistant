package com.supergestao.Flyway.migration.assistant.ui.utilitario.editorSql;

import com.supergestao.Flyway.migration.assistant.dominio.tipo.SnippetSql;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.stage.Popup;
import org.fxmisc.richtext.CodeArea;

import java.util.List;

public class GerenciadorAutocomplete {

    private final GerenciadorEditorSql editor;
    private final CodeArea codeArea;
    private final Popup popup;
    private final Label labelSugestao;
    private String palavraDigitada = "";
    private String sugestaoEncontrada = "";

    public GerenciadorAutocomplete(GerenciadorEditorSql editor) {
        this.editor = editor;
        this.codeArea = editor.getCodeArea();
        this.popup = new Popup();
        this.labelSugestao = new Label();
        // Define que cliques fora do popup não consomen o foco do editor
        this.popup.setAutoHide(true);
        this.popup.getContent().add(labelSugestao);

        configurarListeners();
    }

    private void configurarListeners() {
        //Monitora as alterações no texto para carregar a sugestão
        this.codeArea.textProperty().addListener((obs, antigo, novo) -> {
            Platform.runLater(this::atualizarSugestao);
        });

        //Identifica a tecla TAB e ESCAPE antes do editor processar
        this.codeArea.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (popup.isShowing()) {
                if (event.getCode() == KeyCode.TAB) {
                    completarPalavra();
                    event.consume();
                } else if (event.getCode() == KeyCode.ESCAPE) {
                    popup.hide();
                    event.consume();
                }
            }
        });

        //Oculta o popup se o editor perder o foco
        this.codeArea.focusedProperty().addListener((obs, antigo, novo) -> {
            if (!novo) {
                popup.hide();
            }
        });

        //Oculta o popup ao rolar a tela (evita que o texto flutue solto na tela)
        this.codeArea.addEventFilter(ScrollEvent.SCROLL, event -> {
            popup.hide();
        });

        this.codeArea.setOnKeyTyped(event -> {
            String caractere = event.getCharacter();
            int posicaoCursor = this.codeArea.getCaretPosition();

            switch (caractere) {
                case "(" -> {
                    this.codeArea.insertText(posicaoCursor, ")");
                    this.codeArea.moveTo(posicaoCursor);
                }
                case "'" -> {
                    this.codeArea.insertText(posicaoCursor, "'");
                    this.codeArea.moveTo(posicaoCursor);
                }
                case "\"" -> {
                    this.codeArea.insertText(posicaoCursor, "\"");
                    this.codeArea.moveTo(posicaoCursor);
                }
                case "[" -> {
                    this.codeArea.insertText(posicaoCursor, "]");
                    this.codeArea.moveTo(posicaoCursor);
                }
                case "{" -> {
                    this.codeArea.insertText(posicaoCursor, "}");
                    this.codeArea.moveTo(posicaoCursor);
                }
            }
        });


    }

    private void atualizarSugestao() {
        int caretPos = codeArea.getCaretPosition();
        if (caretPos < 0 || !codeArea.isFocused()) {
            popup.hide();
            return;
        }

        String texto = codeArea.getText(0, caretPos);
        this.palavraDigitada = obterPalavraAtual(texto);

        if (palavraDigitada.length() < 2) {
            popup.hide();
            return;
        }

        String busca = palavraDigitada.toUpperCase();

        // Filtra a lista de palavras-chave do editor
        List<String> listaAutocomplete = editor.getPalavrasChaveAutocomplete();
        this.sugestaoEncontrada = listaAutocomplete.stream()
                .filter(palavra -> palavra.startsWith(busca))
                .findFirst()
                .orElse(null);

        if (sugestaoEncontrada == null) {
            popup.hide();
            return;
        }
        //Busca se a sugestão possui um template inteligente registrado no Enum SnippetSql
        SnippetSql snippet = SnippetSql.buscarPorPalavra(sugestaoEncontrada);
        String template = snippet != null ? snippet.getTemplate() : sugestaoEncontrada + " ";

        //Remove temporariamente o caractere '|' para exibir o fantasma na tela de forma limpa
        String templateLimpo = template.replace("|", "");
        String sufixo = templateLimpo.substring(palavraDigitada.length());

        // Estiliza o rótulo fantasma combinando a mesma fonte e tamanho do editor
        labelSugestao.setText(sufixo);
        labelSugestao.setStyle(
                "-fx-font-family:" + editor.getFonteSitema() + "; " +
                        "-fx-font-size: " + editor.getTamanhoFonte() + "px; " +
                        "-fx-text-fill: rgba(128, 128, 128, 0.65); " + // Cor cinza transparente
                        "-fx-background-color: transparent; " +
                        "-fx-padding: 0;" +
                        "-fx-background-insets: 0;"
        );

        // Posiciona o popup exatamente na ponta direita do cursor
        codeArea.getCaretBounds().ifPresent(bounds -> {
            Platform.runLater(() -> {
                if (codeArea.isFocused() && !sufixo.isEmpty()) {
                    //posiciona logo após a última letra digitada
                    double desvioY = editor.getTamanhoFonte() * 0.15;
                    popup.show(codeArea.getScene().getWindow(), bounds.getMaxX(), bounds.getMinY() + desvioY);
                } else {
                    popup.hide();
                }
            });
        });
    }

    private void completarPalavra() {
        if (sugestaoEncontrada == null || palavraDigitada.isEmpty()) {
            return;
        }
        SnippetSql snippet = SnippetSql.buscarPorPalavra(sugestaoEncontrada);
        String template = snippet != null ? snippet.getTemplate() : sugestaoEncontrada + " ";
        autocompletarFuncoes(template);

        popup.hide();
    }

    private String obterPalavraAtual(String texto) {
        if (texto.isEmpty()) return "";
        int i = texto.length() - 1;
        while (i >= 0 && (Character.isLetterOrDigit(texto.charAt(i)) || texto.charAt(i) == '_')) {
            i--;
        }
        return texto.substring(i + 1);
    }

    private void autocompletarFuncoes(String template) {
        int caretPos = codeArea.getCaretPosition();
        int inicio = caretPos - palavraDigitada.length();

        // Identifica onde o cursor deve piscar (posição do caractere '|')
        int posicaoRelativaCursor = template.indexOf("|");

        // Remove o caractere de controle '|' antes de aplicar o texto
        String textoAInserir = template.replace("|", "");

        // Realiza a inserção física no RichTextFX
        codeArea.replaceText(inicio, caretPos, textoAInserir);

        // Reposiciona o cursor piscante de acordo com o delimitador
        if (posicaoRelativaCursor != -1) {
            codeArea.moveTo(inicio + posicaoRelativaCursor);
        } else {
            codeArea.moveTo(inicio + textoAInserir.length());
        }
    }

}
