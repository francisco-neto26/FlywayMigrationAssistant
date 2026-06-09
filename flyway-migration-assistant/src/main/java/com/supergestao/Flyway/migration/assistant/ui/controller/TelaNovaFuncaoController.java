package com.supergestao.Flyway.migration.assistant.ui.controller;

import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemSistema;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Modulo;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.RetornoSalvarDiretorio;
import com.supergestao.Flyway.migration.assistant.exception.TelaException;
import com.supergestao.Flyway.migration.assistant.ui.estado.ContextoAplicacao;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.GerenciadorEstiloBotao;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.TipoDialogo;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TelaNovaFuncaoController implements ITelasModal {

    @FXML
    public Label lblNomeFuncao;
    @FXML
    public Label lblModulo;
    @FXML
    private ComboBox<Modulo> comboModulo;
    @FXML
    private TextField txtNomeFuncao;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnSalvarFuncao;
    @FXML
    private VBox painelRaiz;

    private ContextoAplicacao contexto;

    public void setContextoAplicacao(ContextoAplicacao contextoAplicacao) {
        this.contexto = contextoAplicacao;
    }

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            comboModulo.getItems().addAll(this.contexto.obterModulosExistentes(this.contexto.getDiretorioArquivo()).values());
            GerenciadorEstiloBotao.gerenciadorEstiloBotao(painelRaiz);
        });
    }

    @FXML
    private void fechar() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void salvarFuncao() {
        String nomeFuncao = txtNomeFuncao.getText().trim();
        String nomeModulo = comboModulo.getValue().getNome();

        if (nomeFuncao.isBlank()) {
            this.contexto.exibirDialogo(TipoDialogo.ALERTA,
                    MensagemSistema.ATENCAO.getMensagem(),
                    null,
                    MensagemSistema.CAMPO_OBRIGATORIO.MensagemComParametro(lblModulo.getText())
            );
            return;
        }

        if (nomeModulo.isBlank()) {
            this.contexto.exibirDialogo(TipoDialogo.ALERTA,
                    MensagemSistema.ATENCAO.getMensagem(),
                    null,
                    MensagemSistema.CAMPO_OBRIGATORIO.MensagemComParametro(lblNomeFuncao.getText())
            );
            return;
        }

        String caminhoCompleto = Paths.get(this.contexto.getDiretorioArquivo(), nomeModulo, nomeFuncao).toAbsolutePath().toString();

        boolean confirmacao = this.contexto.exibirDialogo(TipoDialogo.CONFIRMACAO,
                MensagemSistema.NOVA_FUNCAO.getMensagem(),
                null,
                MensagemSistema.SALVAR_FUNCAO.MensagemComParametro(nomeFuncao, caminhoCompleto)
        );

        if (confirmacao) {
            try {
                List<RetornoSalvarDiretorio> resultado = this.contexto.criarModuloFuncao(nomeModulo, nomeFuncao, caminhoCompleto);

                String listaResultado = formatarLista(resultado, retorno ->
                        "Função: " + retorno.nome() + " - " + (retorno.criado() ? "Criada com sucesso." : "Erro ao criar")
                );

                this.contexto.exibirDialogo(TipoDialogo.MENSAGEM,
                        MensagemSistema.NOVA_FUNCAO.getMensagem(),
                        MensagemSistema.LISTA_FUNCAO.getMensagem(),
                        listaResultado
                );

                Stage stage = (Stage) btnSalvarFuncao.getScene().getWindow();
                stage.close();

            } catch (TelaException e) {
                this.contexto.exibirDialogo(TipoDialogo.ERRO,
                        MensagemSistema.ERRO_SALVAR_REGISTRO.getMensagem(),
                        MensagemSistema.ERRO_SALVAR_FUNCAO.MensagemComParametro(nomeFuncao),
                        e.getMessage()
                );
            }
        }
    }

    private static <T> String formatarLista(Collection<T> itens, Function<T, String> formatador) {
        return itens.stream()
                .map(formatador)
                .collect(Collectors.joining("\n"));
    }

}
