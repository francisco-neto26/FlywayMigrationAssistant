package com.supergestao.Flyway.migration.assistant.ui.utilitario;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GerenciadorEditorSql {

    private final CodeArea codeArea;
    private final ContextMenu menuAutocomplete;
    private final List<String> palavrasChaveAutocomplete;

    private int tamanhoFonte = 14;
    private int erroInicio = -1;
    private int erroFim = -1;

    private Runnable acaoSalvar;
    private Runnable acaoIndentar;

    public GerenciadorEditorSql(StackPane containerSql) {
        this.codeArea = new CodeArea();

        atualizarEstiloFonte();
        this.codeArea.setParagraphGraphicFactory(LineNumberFactory.get(this.codeArea));

        this.palavrasChaveAutocomplete = Arrays.asList(
                "SELECT", "FROM", "WHERE", "INSERT", "UPDATE", "DELETE", "CREATE", "DROP", "ALTER",
                "TABLE", "VIEW", "FUNCTION", "TRIGGER", "INDEX", "RETURNS", "DECLARE", "BEGIN", "END",
                "LOOP", "IF", "THEN", "ELSE", "ELSIF", "COALESCE", "SUBSTRING", "UPPER", "LOWER",
                "RETURNS TRIGGER", "LANGUAGE 'plpgsql'"
        );
        this.menuAutocomplete = new ContextMenu();
        this.menuAutocomplete.setAutoHide(true);

        // Listener para realce de sintaxe e autocomplete
        this.codeArea.textProperty().addListener((obs, antigo, novo) -> {
            // Se o usuário digitar, remove a marcação vermelha de erro anterior
            if (erroInicio >= 0) {
                this.erroInicio = -1;
                this.erroFim = -1;
            }
            recalcularEstilos(novo);
            Platform.runLater(this::processarAutocomplete);
        });

        // Zoom com Ctrl + Scroll
        this.codeArea.setOnScroll(event -> {
            if (event.isControlDown()) {
                double deltaY = event.getDeltaY();
                if (deltaY > 0) {
                    aumentarFonte();
                } else if (deltaY < 0) {
                    diminuirFonte();
                }
                event.consume();
            }
        });

        // Atalhos de Teclado
        this.codeArea.setOnKeyPressed(event -> {
            if (event.isControlDown()) {
                if (event.getCode() == KeyCode.S) {
                    if (acaoSalvar != null) {
                        acaoSalvar.run();
                        event.consume();
                    }
                } else if (event.getCode() == KeyCode.I) {
                    if (acaoIndentar != null) {
                        acaoIndentar.run();
                        event.consume();
                    }
                } else if (event.getCode() == KeyCode.Z) {
                    this.codeArea.undo();
                    event.consume();
                } else if (event.getCode() == KeyCode.Y) {
                    this.codeArea.redo();
                    event.consume();
                } else if (event.getCode() == KeyCode.EQUALS || event.getCode() == KeyCode.ADD) {
                    aumentarFonte();
                    event.consume();
                } else if (event.getCode() == KeyCode.MINUS || event.getCode() == KeyCode.SUBTRACT) {
                    diminuirFonte();
                    event.consume();
                }
            }
        });

        // Navegação do Autocomplete
        this.codeArea.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (menuAutocomplete.isShowing()) {
                if (event.getCode() == KeyCode.DOWN) {
                    menuAutocomplete.getSkin().getNode().requestFocus();
                    event.consume();
                } else if (event.getCode() == KeyCode.ESCAPE) {
                    menuAutocomplete.hide();
                    event.consume();
                }
            }
        });

        // Auto-fechamento
        this.codeArea.setOnKeyTyped(event -> {
            String caractere = event.getCharacter();
            int posicaoCursor = this.codeArea.getCaretPosition();
            switch (caractere) {
                case "(":
                    this.codeArea.insertText(posicaoCursor, ")");
                    this.codeArea.moveTo(posicaoCursor);
                    break;
                case "'":
                    this.codeArea.insertText(posicaoCursor, "'");
                    this.codeArea.moveTo(posicaoCursor);
                    break;
                case "\"":
                    this.codeArea.insertText(posicaoCursor, "\"");
                    this.codeArea.moveTo(posicaoCursor);
                    break;
                case "[":
                    this.codeArea.insertText(posicaoCursor, "]");
                    this.codeArea.moveTo(posicaoCursor);
                    break;
            }
        });

        carregarFolhaEstilos(containerSql);

        VirtualizedScrollPane<CodeArea> scrollPane = new VirtualizedScrollPane<>(this.codeArea);
        containerSql.getChildren().add(scrollPane);
    }

    private void carregarFolhaEstilos(StackPane containerSql) {
        try {
            java.net.URL cssUrl = getClass().getResource("editor-sql.css");
            if (cssUrl == null) {
                cssUrl = getClass().getResource("/com/supergestao/Flyway/migration/assistant/ui/controller/editor-sql.css");
            }
            if (cssUrl != null) {
                containerSql.getStylesheets().add(cssUrl.toExternalForm());
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar o arquivo CSS do editor SQL: " + e.getMessage());
        }
    }

    public void recalcularEstilos(String texto) {
        this.codeArea.setStyleSpans(0, SqlSyntaxHighlighting.calcularStyles(texto, erroInicio, erroFim));
    }

    /**
     * Aplica o sublinhado ondulado vermelho no trecho com erro e rola a tela até ele.
     */
    public void marcarErro(int linha, int coluna, String mensagem) {
        try {
            if (linha > 0) {
                int indiceLinha = Math.min(linha - 1, codeArea.getParagraphs().size() - 1);
                int comprimentoParagrafo = codeArea.getParagraphLength(indiceLinha);
                int col = Math.min(coluna, comprimentoParagrafo);

                int posicaoAbsoluta = codeArea.getAbsolutePosition(indiceLinha, col);
                this.erroInicio = posicaoAbsoluta;

                // Calcula o fim da palavra com erro para limitar o sublinhado
                int fim = posicaoAbsoluta + 5;
                String texto = codeArea.getText();
                if (posicaoAbsoluta < texto.length()) {
                    int j = posicaoAbsoluta;
                    while (j < texto.length() && Character.isLetterOrDigit(texto.charAt(j))) {
                        j++;
                    }
                    fim = j > posicaoAbsoluta ? j : posicaoAbsoluta + 1;
                }
                this.erroFim = Math.min(fim, texto.length());

                recalcularEstilos(texto);

                // Move o cursor e foca a visualização na linha com erro
                codeArea.moveTo(indiceLinha, col);
                codeArea.requestFollowCaret();
            }
        } catch (Exception e) {
            System.err.println("Erro ao marcar o erro no editor SQL: " + e.getMessage());
        }
    }

    /**
     * Remove qualquer sublinhado vermelho de erro ativo.
     */
    public void limparErros() {
        if (this.erroInicio >= 0) {
            this.erroInicio = -1;
            this.erroFim = -1;
            recalcularEstilos(codeArea.getText());
        }
    }

    private void processarAutocomplete() {
        int caretPos = codeArea.getCaretPosition();
        String texto = codeArea.getText(0, caretPos);
        String palavraAtual = obterPalavraAtual(texto);

        if (palavraAtual.length() < 2) {
            menuAutocomplete.hide();
            return;
        }

        String busca = palavraAtual.toUpperCase();
        List<String> correspondentes = palavrasChaveAutocomplete.stream()
                .filter(palavra -> palavra.startsWith(busca) && !palavra.equals(busca))
                .limit(6)
                .collect(Collectors.toList());

        if (correspondentes.isEmpty()) {
            menuAutocomplete.hide();
            return;
        }

        menuAutocomplete.getItems().clear();
        for (String sugestao : correspondentes) {
            MenuItem item = new MenuItem(sugestao);
            item.setOnAction(e -> aplicarSugestao(palavraAtual, sugestao));
            menuAutocomplete.getItems().add(item);
        }

        codeArea.getCaretBounds().ifPresent(bounds -> {
            menuAutocomplete.show(codeArea, bounds.getMaxX(), bounds.getMaxY() + 2);
        });
    }

    private String obterPalavraAtual(String texto) {
        if (texto.isEmpty()) return "";
        int i = texto.length() - 1;
        while (i >= 0 && (Character.isLetterOrDigit(texto.charAt(i)) || texto.charAt(i) == '_')) {
            i--;
        }
        return texto.substring(i + 1);
    }

    private void aplicarSugestao(String palavraOriginal, String sugestao) {
        int caretPos = codeArea.getCaretPosition();
        int inicio = caretPos - palavraOriginal.length();
        codeArea.replaceText(inicio, caretPos, sugestao);
        codeArea.requestFocus();
    }

    public void aumentarFonte() {
        if (tamanhoFonte < 30) {
            tamanhoFonte += 1;
            atualizarEstiloFonte();
        }
    }

    public void diminuirFonte() {
        if (tamanhoFonte > 10) {
            tamanhoFonte -= 1;
            atualizarEstiloFonte();
        }
    }

    private void atualizarEstiloFonte() {
        this.codeArea.setStyle("-fx-font-size: " + tamanhoFonte + "px;");
    }

    public CodeArea getCodeArea() {
        return codeArea;
    }

    public String getTexto() {
        return codeArea.getText();
    }

    public void setTexto(String texto) {
        codeArea.replaceText(texto != null ? texto : "");
    }

    public void limpar() {
        codeArea.clear();
    }

    public ObservableValue<String> textProperty() {
        return codeArea.textProperty();
    }

    public void setAcaoSalvar(Runnable acaoSalvar) {
        this.acaoSalvar = acaoSalvar;
    }

    public void setAcaoIndentar(Runnable acaoIndentar) {
        this.acaoIndentar = acaoIndentar;
    }
}
