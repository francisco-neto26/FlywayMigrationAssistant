package com.supergestao.Flyway.migration.assistant.ui.utilitario.arvore;

import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemSistema;
import com.supergestao.Flyway.migration.assistant.ui.controller.TelaPrincipalController;
import com.supergestao.Flyway.migration.assistant.ui.estado.ContextoAplicacao;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.editorSql.GerenciadorEditorSql;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.janela.TipoDialogo;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.nio.file.Paths;

public class GerenciadorArvoreArquivos {
    private final ContextoAplicacao contexto;
    private final TreeView<String> treeArquivos;
    private final GerenciadorEditorSql editorSql;
    private final Button btnSalvarSql;
    private final Button btnCancelarEdicao;
    private final Button btnIndentar;
    private final Button btnValidarSql;
    private String caminhoArquivoSelecionado;
    private String conteudoOriginal;
    private boolean revertendoSelecao = false;

    public GerenciadorArvoreArquivos(ContextoAplicacao contexto, TreeView<String> treeArquivos, GerenciadorEditorSql editorSql,
                                     Button btnSalvarSql, Button btnCancelarEdicao, Button btnIndentar, Button btnValidarSql) {
        this.contexto = contexto;
        this.treeArquivos = treeArquivos;
        this.editorSql = editorSql;
        this.btnSalvarSql = btnSalvarSql;
        this.btnCancelarEdicao = btnCancelarEdicao;
        this.btnIndentar = btnIndentar;
        this.btnValidarSql = btnValidarSql;
    }

    public void selecaoArvore() {
        treeArquivos.getSelectionModel().selectedItemProperty().addListener((observable, itemAntigo, itemNovo) -> {
            if (revertendoSelecao) {
                return;
            }

            if (textoModificado()) {
                boolean querDescartar = contexto.exibirDialogo(TipoDialogo.CONFIRMACAO,
                        MensagemSistema.ALTERACAO_NAO_SALVA.getMensagem(),
                        null,
                        MensagemSistema.DESCARTAR_ALTERACAO.getMensagem()
                );

                if (!querDescartar) {
                    revertendoSelecao = true;
                    btnSalvarSql.fire();
                    if(!textoModificado()){
                        carregarConteudoArquivo(itemNovo);
                        revertendoSelecao = false;
                    }else {
                        Platform.runLater(() -> {
                            treeArquivos.getSelectionModel().select(itemAntigo);
                            revertendoSelecao = false;
                        });
                    }
                    return;
                }
            }

            if (itemNovo != null && itemNovo.getValue() != null && itemNovo.getValue().toLowerCase().endsWith(".sql")) {
                carregarConteudoArquivo(itemNovo);
            } else {
                limparEdicaoSql();
            }
        });

        editorSql.textProperty().addListener((observable, textoAntigo, textoNovo) -> {
            if (caminhoArquivoSelecionado != null) {
                boolean modificado = textoModificado();
                btnSalvarSql.setDisable(!modificado);
                btnCancelarEdicao.setDisable(!modificado);
            }
        });
    }

    private void carregarConteudoArquivo(TreeItem<String> itemArquivo) {
        try {
            String nomeArquivo = itemArquivo.getValue();
            TreeItem<String> pai = itemArquivo.getParent();
            if (pai == null) {
                return;
            }
            String nomeModulo;
            String nomeFuncao = "";

            if (pai.getParent() == treeArquivos.getRoot()) {
                nomeModulo = pai.getValue();
            } else {
                nomeFuncao = pai.getValue();
                nomeModulo = pai.getParent() != null ? pai.getParent().getValue() : "";
            }
            this.caminhoArquivoSelecionado = Paths.get(
                    this.contexto.getDiretorioArquivo(),
                    nomeModulo,
                    nomeFuncao,
                    nomeArquivo
            ).toAbsolutePath().toString();
            String conteudo = this.contexto.buscarConteudoArquivo(this.caminhoArquivoSelecionado);
            this.conteudoOriginal = conteudo;

            editorSql.setTexto(conteudo);

            btnSalvarSql.setDisable(true);
            btnCancelarEdicao.setDisable(true);
            btnIndentar.setDisable(false);
            btnValidarSql.setDisable(false);

        } catch (Exception e) {
            this.contexto.exibirDialogo(TipoDialogo.ERRO,
                    MensagemSistema.ERRO.getMensagem(),
                    MensagemSistema.ERRO_CRIAR_ARVORE_MODULOS_ARQUIVOS.MensagemComParametro(itemArquivo.getValue()),
                    e.getMessage()
            );
            limparEdicaoSql();
        }
    }

    public void limparEdicaoSql() {
        editorSql.limpar();
        this.caminhoArquivoSelecionado = null;
        this.conteudoOriginal = null;

        btnSalvarSql.setDisable(true);
        btnCancelarEdicao.setDisable(true);
        btnIndentar.setDisable(true);
        btnValidarSql.setDisable(true);
    }

    public void reverterEdicaoSql() {
        if (caminhoArquivoSelecionado != null) {
            editorSql.setTexto(conteudoOriginal != null ? conteudoOriginal : "");
            btnSalvarSql.setDisable(true);
            btnCancelarEdicao.setDisable(true);
        }
    }

    public boolean textoModificado() {
        if (caminhoArquivoSelecionado == null) {
            return false;
        }
        String atual = editorSql.getTexto() != null ? editorSql.getTexto() : "";
        String original = conteudoOriginal != null ? conteudoOriginal : "";
        return !atual.equals(original);

    }

    public void atualizarConteudoOriginalArquivo(String novoConteudo) {
        this.conteudoOriginal = novoConteudo;
        boolean modificado = textoModificado();
        btnSalvarSql.setDisable(!modificado);
        btnCancelarEdicao.setDisable(!modificado);
    }

    public String getCaminhoArquivoSelecionado() {
        return caminhoArquivoSelecionado;
    }
}
