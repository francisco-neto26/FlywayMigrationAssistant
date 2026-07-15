package com.supergestao.Flyway.migration.assistant.ui.utilitario.arvore;

import com.supergestao.Flyway.migration.assistant.ui.controller.TelaPrincipalController;
import com.supergestao.Flyway.migration.assistant.ui.estado.ContextoAplicacao;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.nio.file.Paths;

public class GerenciadorArvoreArquivos {

    private final TelaPrincipalController controller;
    private final ContextoAplicacao contexto;
    private final TreeView<String> treeArquivos;

    public GerenciadorArvoreArquivos(TelaPrincipalController controller, ContextoAplicacao contexto, TreeView<String> treeArquivos,
                                     Button btnSalvarSql, Button btnCancelarEdicao, Button btnIndentar, Button btnValidarSql) {
        this.controller = controller;
        this.contexto = contexto;
        this.treeArquivos = treeArquivos;
    }

    public void selecaoArvore() {
        treeArquivos.getSelectionModel().selectedItemProperty().addListener((observable, itemAntigo, itemNovo) -> {
            if (itemNovo != null && itemNovo.getValue() != null && itemNovo.getValue().toLowerCase().endsWith(".sql")) {
                String caminho = obterCaminhoDoItem(itemNovo);
                if (caminho != null) {
                    controller.abrirArquivoEmAba(caminho);
                }
            }
        });
    }

    private String obterCaminhoDoItem(TreeItem<String> itemArquivo) {
        String nomeArquivo = itemArquivo.getValue();
        TreeItem<String> pai = itemArquivo.getParent();
        if (pai == null) {
            return null;
        }
        String nomeModulo;
        String nomeFuncao = "";

        if (pai.getParent() == treeArquivos.getRoot()) {
            nomeModulo = pai.getValue();
        } else {
            nomeFuncao = pai.getValue();
            nomeModulo = pai.getParent() != null ? pai.getParent().getValue() : "";
        }

        return Paths.get(
                this.contexto.getDiretorioArquivo(),
                nomeModulo,
                nomeFuncao,
                nomeArquivo
        ).toAbsolutePath().toString();
    }
}
