package com.supergestao.Flyway.migration.assistant.ui.utilitario;

import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemSistema;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.PalavraChaveSql;
import com.supergestao.Flyway.migration.assistant.ui.estado.ContextoAplicacao;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.singleton;

public class GerenciadorEditorSql {

    private final CodeArea codeArea;
    private final ContextMenu menuAutocomplete;
    private final List<String> palavrasChaveAutocomplete;

    private int tamanhoFonte = 14;
    private int linhaErro = -1;
    private int erroFim = -1;

    private Runnable acaoSalvar;
    private Runnable acaoIndentar;
    private final ContextoAplicacao contextoAplicacao;


    public GerenciadorEditorSql(StackPane containerSql, ContextoAplicacao contextoAplicacao) {
        this.contextoAplicacao = contextoAplicacao;
        this.codeArea = new CodeArea();


        atualizarEstiloFonte();
        this.codeArea.setParagraphGraphicFactory(LineNumberFactory.get(this.codeArea));

        this.palavrasChaveAutocomplete = new ArrayList<>(PalavraChaveSql.getListaPalavras());
        this.palavrasChaveAutocomplete.add("RETURNS TRIGGER");
        this.palavrasChaveAutocomplete.add("LANGUAGE 'PL/pgSQL'");
        this.menuAutocomplete = new ContextMenu();
        this.menuAutocomplete.setAutoHide(true);

        // Listener para realce de sintaxe e autocomplete
        this.codeArea.textProperty().addListener((obs, antigo, novo) -> {
            if (linhaErro >= 0) {
                try {
                    this.codeArea.setParagraphStyle(linhaErro, java.util.Collections.emptyList());
                } catch (Exception ignored) {
                }
                this.linhaErro = -1;
            }
            atualizarEstilos(novo);
            Platform.runLater(this::processarAutocomplete);
        });

        // Zoom com Ctrl + Scroll
        this.codeArea.addEventFilter(ScrollEvent.SCROLL, event -> {
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

        carregarCssEstilos(containerSql);

        VirtualizedScrollPane<CodeArea> scrollPane = new VirtualizedScrollPane<>(this.codeArea);
        containerSql.getChildren().add(scrollPane);
    }

    private void carregarCssEstilos(StackPane containerSql) {
        try {

            URL cssUrl = getClass().getResource(CaminhoTela.EDITOR_SQL.getCaminho());
            if (cssUrl != null) {
                containerSql.getStylesheets().add(cssUrl.toExternalForm());
            }
        } catch (Exception e) {
            throw new RuntimeException(MensagemSistema.ERRO_CSS.getMensagem() + e.getMessage(), e);
        }
    }

    public void atualizarEstilos(String texto) {
        this.codeArea.setStyleSpans(0, RealceSintaxeSql.atualizarEstilos(texto));
    }

    public void marcarLinhaErro(int linha) {
        try {
            if (linha > 0) {
                limparErros();
                int indiceLinha = Math.min(linha - 1, codeArea.getParagraphs().size() - 1);
                this.linhaErro = indiceLinha;
                codeArea.setParagraphStyle(indiceLinha, singleton("error-line"));
                // Move o cursor e foca a visualização no início da linha com erro
                codeArea.moveTo(indiceLinha, 0);
                codeArea.requestFollowCaret();
            }
        } catch (Exception e) {
            contextoAplicacao.exibirDialogo(TipoDialogo.ERRO,
                    MensagemSistema.ERRO_GENERICO.getMensagem(),
                    MensagemSistema.ERRO_LINHA_ERRO.getMensagem(),
                    e.getMessage());
        }
    }

    /**
     * Remove qualquer sublinhado vermelho de erro ativo.
     */
    public void limparErros() {
        if (this.linhaErro >= 0) {
            try {
                codeArea.setParagraphStyle(linhaErro, java.util.Collections.emptyList());
            } catch (Exception ignored) {
            }
            this.linhaErro = -1;
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
