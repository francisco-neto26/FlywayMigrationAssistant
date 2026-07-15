package com.supergestao.Flyway.migration.assistant.ui.utilitario.editorSql;

import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemSistema;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.PalavraChaveSql;
import com.supergestao.Flyway.migration.assistant.ui.estado.ContextoAplicacao;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.janela.CaminhoTela;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.janela.TipoDialogo;
import javafx.beans.value.ObservableValue;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;

public class GerenciadorEditorSql {

    private final CodeArea codeArea;
    private final List<String> palavrasChaveAutocomplete;
    private int tamanhoFonte;
    private int linhaErro = -1;
    private Runnable acaoSalvar;
    private Runnable acaoIndentar;
    private final ContextoAplicacao contextoAplicacao;
    private String fonteSitema;
    private final VirtualizedScrollPane<CodeArea> scrollPane;
    private final GerenciadorColunaNumeracao gerenciadorColunaNumeracao;
    private String caminhoArquivo;
    private String conteudoOriginal;

    public GerenciadorEditorSql(ContextoAplicacao contextoAplicacao) {
        this.contextoAplicacao = contextoAplicacao;
        this.codeArea = new CodeArea();
        this.fonteSitema = contextoAplicacao.getChaveFonte();
        this.tamanhoFonte = contextoAplicacao.getTamanhoFonteSql();

        this.scrollPane = new VirtualizedScrollPane<>(this.codeArea);
        carregarCssEstilos(this.scrollPane);

        this.gerenciadorColunaNumeracao = new GerenciadorColunaNumeracao(this);
        alterarTamanhoFonte(0);

        this.palavrasChaveAutocomplete = new ArrayList<>(PalavraChaveSql.getListaPalavras());
        this.palavrasChaveAutocomplete.add("RETURNS TRIGGER");
        this.palavrasChaveAutocomplete.add("LANGUAGE 'PL/pgSQL'");

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

        GerenciadorAtalhosEditor.configurar(this);
        new GerenciadorAutocomplete(this);
    }

    private void carregarCssEstilos(VirtualizedScrollPane<CodeArea> scroll) {
        try {
            URL cssUrl = getClass().getResource(CaminhoTela.EDITOR_SQL.getCaminho());
            if (cssUrl != null) {
                scroll.getStylesheets().add(cssUrl.toExternalForm());
            }
        } catch (Exception e) {
            throw new RuntimeException(MensagemSistema.ERRO_CSS.getMensagem() + e.getMessage(), e);
        }
    }

    public VirtualizedScrollPane<CodeArea> getScrollPane() {
        return scrollPane;
    }

    public String getCaminhoArquivo() {
        return caminhoArquivo;
    }

    public void setCaminhoArquivo(String caminhoArquivo) {
        this.caminhoArquivo = caminhoArquivo;
    }

    public String getConteudoOriginal() {
        return conteudoOriginal;
    }

    public void setConteudoOriginal(String conteudoOriginal) {
        this.conteudoOriginal = conteudoOriginal;
    }

    public boolean isModificado() {
        if (caminhoArquivo == null) {
            return false;
        }
        String atual = getTexto();
        String original = conteudoOriginal != null ? conteudoOriginal : "";
        return !atual.equals(original);
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

    public void limparErros() {
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
            this.gerenciadorColunaNumeracao.atualizarFabricaNumeracao();
        }
    }

    public void atualizarConfiguracaoFonte(int tamanho, String fonte) {
        this.tamanhoFonte = tamanho;
        this.fonteSitema = fonte;
        this.codeArea.setStyle(
                "-fx-font-size: " + tamanhoFonte + "px; " +
                        "-fx-font-family: '" + fonteSitema  + "';"
        );
        this.gerenciadorColunaNumeracao.atualizarFabricaNumeracao();
    }

    public CodeArea getCodeArea() {
        return codeArea;
    }

    public String getTexto() {
        return codeArea.getText();
    }

    public void setTexto(String texto) {
        this.gerenciadorColunaNumeracao.limparMarcadores();
        codeArea.replaceText(texto != null ? texto : "");
    }

    public void limpar() {
        this.gerenciadorColunaNumeracao.limparMarcadores();
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

    public GerenciadorColunaNumeracao getGerenciadorColunaNumeracao() {
        return gerenciadorColunaNumeracao;
    }
}
