package com.supergestao.Flyway.migration.assistant.ui.utilitario.arvore;

import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemSistema;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.*;
import com.supergestao.Flyway.migration.assistant.ui.estado.ContextoAplicacao;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.janela.TipoDialogo;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GerenciadorArvoreModulos {

    public static void buscarModulosFuncoes(ContextoAplicacao contexto, TreeView<String> treeArquivos) {
        Map<String, Modulo> moduloFuncao = contexto.moduloParaSincronizar(contexto.getDiretorioModulo(), contexto.getDiretorioArquivo());

        if (!moduloFuncao.isEmpty()) {
            boolean querCriar = contexto.exibirDialogo(TipoDialogo.CONFIRMACAO,
                    MensagemSistema.NOVO_MODULO.getMensagem(),
                    null,
                    MensagemSistema.EXISTE_MODULOS_CRIAR.getMensagem()
            );

            if (querCriar) {
                String modulosNovos = formatarLista(moduloFuncao.values(), Modulo::getNome);
                querCriar = contexto.exibirDialogo(TipoDialogo.CONFIRMACAO,
                        MensagemSistema.NOVO_MODULO.getMensagem(),
                        MensagemSistema.LISTA_MODULO.getMensagem(),
                        modulosNovos);

                if (querCriar) {
                    List<Resultado> resultado = contexto.criarNovoModulo(moduloFuncao, contexto.getDiretorioArquivo());
                    String listaResultado = formatarLista(resultado, retorno ->
                            "Módulo: " + retorno.valor() + " - " + (retorno.sucesso() ? "Criado com sucesso." : "Erro ao criar")
                    );

                    contexto.exibirDialogo(TipoDialogo.MENSAGEM,
                            MensagemSistema.NOVO_MODULO.getMensagem(),
                            MensagemSistema.LISTA_MODULO.getMensagem(),
                            listaResultado
                    );
                }
            }
        }
        desenharArvore(contexto, treeArquivos, null);
    }

    public static void buscarModulosFuncoesFiltrados(ContextoAplicacao contexto, TreeView<String> treeArquivos, String termoBusca) {
        Map<String, Map<String, Set<String>>> resultados = contexto.buscarArquivosPorTermo(termoBusca);
        desenharArvore(contexto, treeArquivos, resultados);
    }

    private static void desenharArvore(ContextoAplicacao contexto, TreeView<String> treeArquivos, Map<String, Map<String, Set<String>>> dadosFiltrados) {
        try {
            TreeItem<String> raiz = new TreeItem<>("Módulos");
            raiz.setExpanded(true);

            if (dadosFiltrados != null) {
                if (dadosFiltrados.isEmpty()) {
                    TreeItem<String> semResultados = new TreeItem<>("Nenhum arquivo encontrado");
                    raiz.getChildren().add(semResultados);
                    treeArquivos.setRoot(raiz);
                    treeArquivos.setShowRoot(false);
                    return;
                }

                for (Map.Entry<String, Map<String, Set<String>>> entradaModulo : dadosFiltrados.entrySet()) {
                    String nomeModulo = entradaModulo.getKey();
                    TreeItem<String> itemModulo = new TreeItem<>(nomeModulo);
                    itemModulo.setExpanded(true);

                    for (Map.Entry<String, Set<String>> entradaFuncao : entradaModulo.getValue().entrySet()) {
                        String nomeFuncao = entradaFuncao.getKey();
                        TreeItem<String> itemFuncao = null;

                        if (!nomeFuncao.isEmpty()) {
                            itemFuncao = new TreeItem<>(nomeFuncao);
                            itemFuncao.setExpanded(true);
                        }

                        for (String nomeArq : entradaFuncao.getValue()) {
                            TreeItem<String> itemArquivo = new TreeItem<>(nomeArq);
                            if (itemFuncao != null) {
                                itemFuncao.getChildren().add(itemArquivo);
                            } else {
                                itemModulo.getChildren().add(itemArquivo);
                            }
                        }

                        if (itemFuncao != null && !itemFuncao.getChildren().isEmpty()) {
                            itemModulo.getChildren().add(itemFuncao);
                        }
                    }

                    if (!itemModulo.getChildren().isEmpty()) {
                        raiz.getChildren().add(itemModulo);
                    }
                }
            } else {
                String diretorioArquivo = contexto.getDiretorioArquivo();
                Map<String, Modulo> modulosExistentes = contexto.obterModulosExistentes(diretorioArquivo);
                Set<TreeItem<String>> ItemsCarregados = new HashSet<>();

                for (Modulo modulo : modulosExistentes.values()) {
                    TreeItem<String> itemModulo = new TreeItem<>(modulo.getNome());

                    for (Funcao funcao : modulo.getFuncoes()) {
                        TreeItem<String> itemFuncao = new TreeItem<>(funcao.getNome());
                        boolean temfuncaoArquivo = contexto.temFuncaoArquivo(diretorioArquivo, modulo.getNome(), funcao.getNome());

                        if (temfuncaoArquivo) {
                            TreeItem<String> dummyNode = new TreeItem<>("Carregando...");
                            itemFuncao.getChildren().add(dummyNode);
                            carregarTreeItem(contexto, itemFuncao, modulo.getNome(), funcao.getNome(), dummyNode, ItemsCarregados);
                        }
                        itemModulo.getChildren().add(itemFuncao);
                    }

                    boolean temFuncoes = !modulo.getFuncoes().isEmpty();
                    boolean temfuncaoArquivo = contexto.temFuncaoArquivo(diretorioArquivo, modulo.getNome(), "");

                    if (temFuncoes || temfuncaoArquivo) {
                        if (temFuncoes) {
                            carregarTreeItem(contexto, itemModulo, modulo.getNome(), "", null, ItemsCarregados);
                        } else {
                            TreeItem<String> dummyNode = new TreeItem<>("Carregando...");
                            itemModulo.getChildren().add(dummyNode);
                            carregarTreeItem(contexto, itemModulo, modulo.getNome(), "", dummyNode, ItemsCarregados);
                        }
                    }
                    raiz.getChildren().add(itemModulo);
                }
            }

            treeArquivos.setRoot(raiz);
            treeArquivos.setShowRoot(false);
        } catch (Exception e) {
            contexto.exibirDialogo(TipoDialogo.ERRO,
                    MensagemSistema.ALERTA.getMensagem(),
                    MensagemSistema.ERRO_CRIAR_ARVORE_MODULOS.getMensagem(),
                    e.getMessage()
            );
        }
    }

    private static <T> String formatarLista(Collection<T> itens, Function<T, String> formatador) {
        return itens.stream()
                .map(formatador)
                .collect(Collectors.joining("\n"));
    }

    private static void carregarTreeItem(
            ContextoAplicacao contexto,
            TreeItem<String> treeItem,
            String nomeModulo,
            String nomeFuncao,
            TreeItem<String> dummyNode,
            Set<TreeItem<String>> carregados) {
        treeItem.expandedProperty().addListener((obs, valorAntigo, expandido) -> {
            if (expandido) {
                if (carregados.contains(treeItem)) {
                    return;
                }
                if (dummyNode != null && treeItem.getChildren().size() == 1 && treeItem.getChildren().getFirst() == dummyNode) {
                    treeItem.getChildren().clear();
                }
                carregarArquivos(contexto, treeItem, nomeModulo, nomeFuncao);
                carregados.add(treeItem);
            }
        });
    }

    private static void carregarArquivos(ContextoAplicacao contexto, TreeItem<String> treeItem, String nomeModulo, String nomeFuncao) {
        try {
            java.util.Collection<Arquivo> arquivos = contexto.carregarArquivos(contexto.getDiretorioArquivo(),
                    nomeModulo,
                    nomeFuncao
            );

            List<Arquivo> arquivosOrdenados = arquivos.stream()
                    .sorted(java.util.Comparator.comparing(arquivo -> arquivo.getNome().toLowerCase()))
                    .toList();

            for (Arquivo arquivo : arquivosOrdenados) {
                treeItem.getChildren().add(new TreeItem<>(arquivo.getNome()));
            }

        } catch (Exception e) {
            contexto.exibirDialogo(TipoDialogo.ERRO,
                    MensagemSistema.ALERTA.getMensagem(),
                    MensagemSistema.ERRO_CRIAR_ARVORE_ARQUIVOS.MensagemComParametro(nomeModulo),
                    e.getMessage()
            );
        }
    }
}
