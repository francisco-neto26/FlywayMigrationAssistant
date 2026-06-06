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

            Map<String, Modulo> moduloFuncao = contexto.getSincronizarModulos()
                    .obterModulosExistentes(contexto.getIGerenciadorConfiguracao().getDiretorioArquivo());

            for (Modulo modulo : moduloFuncao.values()) {
                TreeItem<String> funcoes = new TreeItem<>(modulo.getNome());

                for (Funcao funcao : modulo.getFuncoes()) {
                    TreeItem<String> itemFuncao = new TreeItem<>(funcao.getNome());

                    for (Arquivo arquivo : funcao.getArquivos()) {
                        TreeItem<String> itemArquivo = new TreeItem<>("📄 " + arquivo.getNome());
                        itemFuncao.getChildren().add(itemArquivo);
                    }
                    funcoes.getChildren().add(itemFuncao);
                }
                modulos.getChildren().add(funcoes);
            }
            treeArquivos.setRoot(modulos);

        } catch (Exception e) {
            contexto.getIGerenciadorJanelas().exibirDialogo(TipoDialogo.ERRO,
                    MensagemSistema.ERRO_CRIAR_ARVORE_MODULOS.getMensagem(),
                    MensagemSistema.ERRO_CRIAR_ARVORE_MODULOS.getMensagem(),
                    e.getMessage());
        }
    }

    private static <T> String formatarLista(Collection<T> itens, Function<T, String> formatador) {
        return itens.stream()
                .map(formatador)
                .collect(Collectors.joining("\n"));
    }

}
