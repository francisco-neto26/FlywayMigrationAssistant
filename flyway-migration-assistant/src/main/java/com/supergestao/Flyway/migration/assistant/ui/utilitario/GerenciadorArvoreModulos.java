package com.supergestao.Flyway.migration.assistant.ui.utilitario;

import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemSistema;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Arquivo;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Funcao;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Modulo;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.RetornoSalvarDiretorio;
import com.supergestao.Flyway.migration.assistant.ui.estado.ContextoAplicacao;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GerenciadorArvoreModulos {

    public static void buscarModulosFuncoes(ContextoAplicacao contexto, TreeView<String> treeArquivos) {
        Map<String, Modulo> moduloFuncao = contexto.getSincronizarModulos().
                moduloParaSincronizar(contexto.getIGerenciadorConfiguracao().getDiretorioModulo(),
                        contexto.getIGerenciadorConfiguracao().getDiretorioArquivo());

        if (!moduloFuncao.isEmpty()) {

            boolean querCriar = contexto.getIGerenciadorJanelas().exibirDialogo(TipoDialogo.CONFIRMACAO,
                    MensagemSistema.NOVO_MODULO.getMensagem(),
                    null,
                    MensagemSistema.EXISTE_MODULOS_CRIAR.getMensagem()
            );


            if (querCriar) {
                String modulosNovos = formatarLista(moduloFuncao.values(), Modulo::getNome);

                querCriar = contexto.getIGerenciadorJanelas().exibirDialogo(TipoDialogo.CONFIRMACAO,
                        MensagemSistema.NOVO_MODULO.getMensagem(),
                        MensagemSistema.LISTA_MODULO.getMensagem(),
                        modulosNovos);

                if (querCriar) {

                    List<RetornoSalvarDiretorio> resultado = contexto.getSincronizarModulos().
                            criarNovoModulo(moduloFuncao, contexto.getIGerenciadorConfiguracao().getDiretorioArquivo());
                    String listaResultado = formatarLista(resultado, retorno ->
                            "Módulo: " + retorno.nome() + " - " + (retorno.criado() ? "Criado com sucesso." : "Erro ao criar")
                    );

                    contexto.getIGerenciadorJanelas().exibirDialogo(TipoDialogo.MENSAGEM,
                            MensagemSistema.NOVO_MODULO.getMensagem(),
                            MensagemSistema.LISTA_MODULO.getMensagem(),
                            listaResultado
                    );
                }
            }
        }
        desenharArvore(contexto, treeArquivos);
    }

    private static void desenharArvore(ContextoAplicacao contexto, TreeView<String> treeArquivos) {
        try {
            TreeItem<String> modulos = new TreeItem<>("Módulos");
            modulos.setExpanded(true);
            String diretorioArquivo = contexto.getDiretorioArquivo();
            Map<String, Modulo> modulosExistentes = contexto.getSincronizarModulos()
                    .obterModulosExistentes(diretorioArquivo);

            Set<TreeItem<String>> ItemsCarregados = new HashSet<>();

            for (Modulo modulo : modulosExistentes.values()) {
                TreeItem<String> itemModulo = new TreeItem<>(modulo.getNome());

                for (Funcao funcao : modulo.getFuncoes()) {
                    TreeItem<String> itemFuncao = new TreeItem<>(funcao.getNome());

                    boolean temfuncaoArquivo = contexto.getSincronizarModulos().temFuncaoArquivo(diretorioArquivo, modulo.getNome(), funcao.getNome());

                    if (temfuncaoArquivo) {
                        TreeItem<String> dummyNode = new TreeItem<>("Carregando...");
                        itemFuncao.getChildren().add(dummyNode);
                        carregarTreeItem(contexto, itemFuncao, modulo.getNome(), funcao.getNome(), dummyNode, ItemsCarregados);
                    }

                    itemModulo.getChildren().add(itemFuncao);
                }

                boolean temFuncoes = !modulo.getFuncoes().isEmpty();
                boolean temfuncaoArquivo = contexto.getSincronizarModulos().temFuncaoArquivo(diretorioArquivo, modulo.getNome(), "");

                if (temFuncoes || temfuncaoArquivo) {
                    if (temFuncoes) {
                        carregarTreeItem(contexto, itemModulo, modulo.getNome(), "", null, ItemsCarregados);
                    } else {
                        TreeItem<String> dummyNode = new TreeItem<>("Carregando...");
                        itemModulo.getChildren().add(dummyNode);
                        carregarTreeItem(contexto, itemModulo, modulo.getNome(), "", dummyNode, ItemsCarregados);
                    }
                }

                modulos.getChildren().add(itemModulo);
            }
            treeArquivos.setRoot(modulos);
            treeArquivos.setShowRoot(false);
        } catch (Exception e) {
            contexto.getIGerenciadorJanelas().exibirDialogo(
                    TipoDialogo.ERRO,
                    MensagemSistema.ERRO_CRIAR_ARVORE_MODULOS.getMensagem(),
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

            java.util.Collection<Arquivo> arquivos = contexto.getSincronizarModulos()
                    .carregarArquivos(
                            contexto.getIGerenciadorConfiguracao().getDiretorioArquivo(),
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
            contexto.getIGerenciadorJanelas().exibirDialogo(TipoDialogo.ERRO,
                    MensagemSistema.ERRO_CRIAR_ARVORE_MODULOS_ARQUIVOS.getMensagem(),
                    MensagemSistema.ERRO_CRIAR_ARVORE_ARQUIVOS.MensagemComParametro(nomeModulo),
                    e.getMessage()
            );
        }
    }
}
