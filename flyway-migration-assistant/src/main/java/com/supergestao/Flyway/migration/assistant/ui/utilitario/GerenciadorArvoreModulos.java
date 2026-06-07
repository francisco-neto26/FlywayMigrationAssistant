package com.supergestao.Flyway.migration.assistant.ui.utilitario;

import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemSistema;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Arquivo;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Funcao;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Modulo;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.RetornoSalvarDiretorio;
import com.supergestao.Flyway.migration.assistant.ui.estado.ContextoAplicacao;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

import java.util.Collection;
import java.util.List;
import java.util.Map;
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
            Map<String, Modulo> modulosExistentes = contexto.getSincronizarModulos()
                    .obterModulosExistentes(contexto.getIGerenciadorConfiguracao().getDiretorioArquivo());
            for (Modulo modulo : modulosExistentes.values()) {

                TreeItem<String> itemModulo = new TreeItem<>(modulo.getNome());
                final Modulo moduloFinal = modulo;

                itemModulo.expandedProperty().addListener((obs, antigo, expandido) -> {
                    if (expandido) {

                        boolean arquivosCarregados = itemModulo.getChildren().stream()
                                .anyMatch(child -> child.getValue().startsWith("• "));

                        if (!arquivosCarregados) {
                            try {
                                // Carrega arquivos diretos da raiz do módulo (passando "" como função)
                                java.util.Collection<Arquivo> arquivosDiretos = contexto.getIGerenciadorModulosArquivos()
                                        .carregarArquivos(
                                                contexto.getIGerenciadorConfiguracao().getDiretorioArquivo(),
                                                moduloFinal.getNome(),
                                                "" // Sem subpasta de função
                                        );
                                List<Arquivo> ordenados = arquivosDiretos.stream()
                                        .sorted(java.util.Comparator.comparing(a -> a.getNome().toLowerCase()))
                                        .toList();
                                // Adiciona os arquivos diretamente sob o nó do módulo
                                for (Arquivo arq : ordenados) {
                                    itemModulo.getChildren().add(new TreeItem<>("• " + arq.getNome()));
                                }
                            } catch (Exception ex) {
                                // Falha silenciosa ou log de depuração
                            }
                        }
                    }
                });
                // 2. Adiciona as funções (pastas internas do módulo)
                for (Funcao funcao : modulo.getFuncoes()) {
                    TreeItem<String> itemFuncao = new TreeItem<>(funcao.getNome());
                    final Funcao funcaoFinal = funcao;
                    // Força a setinha de expansão na função
                    TreeItem<String> dummyNode = new TreeItem<>("Carregando...");
                    itemFuncao.getChildren().add(dummyNode);
                    // Escuta expansão da função
                    itemFuncao.expandedProperty().addListener((obs, valorAntigo, foiExpandido) -> {
                        if (foiExpandido) {
                            if (itemFuncao.getChildren().size() == 1 && itemFuncao.getChildren().get(0) == dummyNode) {
                                itemFuncao.getChildren().clear();
                                try {
                                    java.util.Collection<Arquivo> arquivosFuncao = contexto.getIGerenciadorModulosArquivos()
                                            .carregarArquivos(
                                                    contexto.getIGerenciadorConfiguracao().getDiretorioArquivo(),
                                                    moduloFinal.getNome(),
                                                    funcaoFinal.getNome()
                                            );
                                    List<Arquivo> ordenados = arquivosFuncao.stream()
                                            .sorted(java.util.Comparator.comparing(a -> a.getNome().toLowerCase()))
                                            .toList();
                                    for (Arquivo arq : ordenados) {
                                        itemFuncao.getChildren().add(new TreeItem<>("📄 " + arq.getNome()));
                                    }
                                } catch (Exception ex) {
                                    contexto.getIGerenciadorJanelas().exibirDialogo(
                                            TipoDialogo.ERRO,
                                            "Erro ao ler arquivos",
                                            "Não foi possível carregar os arquivos da função: " + funcaoFinal.getNome(),
                                            ex.getMessage()
                                    );
                                }
                            }
                        }
                    });
                    itemModulo.getChildren().add(itemFuncao);
                }
                modulos.getChildren().add(itemModulo);
            }
            treeArquivos.setRoot(modulos);
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

}
