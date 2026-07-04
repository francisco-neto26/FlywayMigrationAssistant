package com.supergestao.Flyway.migration.assistant.ui.utilitario.editorSql;

import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemSistema;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.PalavraChaveSql;
import com.supergestao.Flyway.migration.assistant.ui.estado.ContextoAplicacao;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.janela.TipoDialogo;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.janela.CaminhoTela;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.StackPane;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;

public class GerenciadorEditorSql {

    private final CodeArea codeArea;
    private final List<String> palavrasChaveAutocomplete;
    private int tamanhoFonte = 8;
    private int linhaErro = -1;
    private Runnable acaoSalvar;
    private Runnable acaoIndentar;
    private final ContextoAplicacao contextoAplicacao;
    private String fonteSitema;


    public GerenciadorEditorSql(StackPane containerSql, ContextoAplicacao contextoAplicacao) {
        this.contextoAplicacao = contextoAplicacao;
        this.codeArea = new CodeArea();
        this.fonteSitema = contextoAplicacao.getChaveFonte();


        alterarTamanhoFonte(tamanhoFonte);
        this.codeArea.setParagraphGraphicFactory(LineNumberFactory.get(this.codeArea));

        this.palavrasChaveAutocomplete = new ArrayList<>(PalavraChaveSql.getListaPalavras());
        this.palavrasChaveAutocomplete.add("RETURNS TRIGGER");
        this.palavrasChaveAutocomplete.add("LANGUAGE 'PL/pgSQL'");

        // Listener para realce de sintaxe e autocomplete
        this.codeArea.textProperty().addListener((obs, antigo, novo) -> {
            if (linhaErro >= 0) {
                try {
                    this.codeArea.setParagraphStyle(linhaErro, emptyList());
                } catch (Exception ignored) {
                }
                this.linhaErro = -1;
            }
            atualizarEstilos(novo);
        });

        //Atalhos e scroll mouse
        GerenciadorAtalhosEditor.configurar(this);

        carregarCssEstilos(containerSql);

        VirtualizedScrollPane<CodeArea> scrollPane = new VirtualizedScrollPane<>(this.codeArea);
        containerSql.getChildren().add(scrollPane);

        new GerenciadorAutocomplete(this);
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
                removeErroLinha();
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


    public void removeErroLinha() {
        if (this.linhaErro >= 0) {
            try {
                codeArea.setParagraphStyle(linhaErro, emptyList());
            } catch (Exception ignored) {
            }
            this.linhaErro = -1;
        }
    }

    public void alterarTamanhoFonte(int delta) {
        int novoTamanho = tamanhoFonte + delta;
        if (novoTamanho >= 10 && novoTamanho <= 30) {
            tamanhoFonte = novoTamanho;
            this.codeArea.setStyle("-fx-font-size: " + tamanhoFonte + "px;" +
                    "-fx-font-family: '" + fonteSitema  + "';");
        }
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

    public Runnable getAcaoSalvar() {
        return acaoSalvar;
    }

    public Runnable getAcaoIndentar() {
        return acaoIndentar;
    }

    public int getTamanhoFonte() {
        return tamanhoFonte;
    }
    public List<String> getPalavrasChaveAutocomplete() {
        return palavrasChaveAutocomplete;
    }

    public String getFonteSitema() {
        return fonteSitema;
    }
}
