package com.supergestao.Flyway.migration.assistant.ui.controller;

import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemSistema;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Resultado;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.indentar.IndentarSql;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.ValidacaoCompletaSql;
import com.supergestao.Flyway.migration.assistant.exception.SqlException;
import com.supergestao.Flyway.migration.assistant.ui.estado.ContextoAplicacao;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.arvore.GerenciadorArvoreArquivos;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.arvore.GerenciadorArvoreModulos;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.editorSql.GerenciadorEditorSql;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.estilo.GerenciadorEstiloBotao;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.estilo.GerenciadorVisual;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.janela.CaminhoTela;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.janela.ConstrutorJanelas;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.janela.TipoDialogo;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TelaPrincipalController implements ITelasModal {

    @FXML
    private TextField txtBuscarArquivo;
    @FXML
    private Button btnBuscar;
    @FXML
    private TreeView<String> treeArquivos;
    @FXML
    private Button btnIndentar;
    @FXML
    private Button btnValidarSql;
    @FXML
    private Button btnCancelarEdicao;
    @FXML
    private Button btnSalvarSql;
    @FXML
    private TabPane painelAbasSql;
    @FXML
    private TextArea txtAreaMensagens;
    @FXML
    private Button btnAtualizar;
    @FXML
    private Button btnNovoModulo;
    @FXML
    private Button btnNovaFuncao;
    @FXML
    private Button btnNovoMigration;
    @FXML
    private BorderPane painelRaiz;

    private ContextoAplicacao contexto;
    private GerenciadorArvoreArquivos gerenciadorArvoreArquivos;
    private final Map<String, Tab> abasAbertas = new HashMap<>();

    @Override
    public void setContextoAplicacao(ContextoAplicacao contextoAplicacao) {
        this.contexto = contextoAplicacao;

        this.painelAbasSql.getSelectionModel().selectedItemProperty().addListener((obs, antigaTab, novaTab) -> {
            atualizarEstadoBotoes();
        });
    }

    @FXML
    public void initialize() {
        java.net.URL cssUrl = getClass().getResource(CaminhoTela.EDITOR_SQL.getCaminho());
        if (cssUrl != null) {
            painelAbasSql.getStylesheets().add(cssUrl.toExternalForm());
        }

        buscaTempoReal();
        Platform.runLater(() -> {
            verifcaEcarregarModuloFuncao();
            this.gerenciadorArvoreArquivos = new GerenciadorArvoreArquivos(
                    this, this.contexto, treeArquivos,
                    btnSalvarSql, btnCancelarEdicao, btnIndentar, btnValidarSql
            );
            this.gerenciadorArvoreArquivos.selecaoArvore();

            GerenciadorEstiloBotao.gerenciadorEstiloBotao(painelRaiz);
            AtivaDesativaBotoesPrincipais();
            atualizarFontesCustomizadas();
            atualizarEstadoBotoes();
        });
    }

    public void atualizarFontesCustomizadas() {
        int tamanhoSql = contexto.getTamanhoFonteSql();
        String fonte = contexto.getChaveFonte();

        treeArquivos.setStyle("-fx-font-size: " + tamanhoSql + "px;");

        for (Tab aba : painelAbasSql.getTabs()) {
            GerenciadorEditorSql editor = (GerenciadorEditorSql) aba.getUserData();
            if (editor != null) {
                editor.atualizarConfiguracaoFonte(tamanhoSql, fonte);
            }
        }
    }

    public GerenciadorEditorSql obterEditorAtivo() {
        Tab abaAtiva = painelAbasSql.getSelectionModel().getSelectedItem();
        if (abaAtiva != null) {
            return (GerenciadorEditorSql) abaAtiva.getUserData();
        }
        return null;
    }

    public void atualizarEstadoBotoes() {
        GerenciadorEditorSql editorAtivo = obterEditorAtivo();
        if (editorAtivo == null || editorAtivo.getCaminhoArquivo() == null) {
            btnSalvarSql.setDisable(true);
            btnCancelarEdicao.setDisable(true);
            btnIndentar.setDisable(true);
            btnValidarSql.setDisable(true);
        } else {
            boolean modificado = editorAtivo.isModificado();
            btnSalvarSql.setDisable(!modificado);
            btnCancelarEdicao.setDisable(!modificado);
            btnIndentar.setDisable(false);
            btnValidarSql.setDisable(false);

            Tab aba = painelAbasSql.getSelectionModel().getSelectedItem();
            String nomeBase = Paths.get(editorAtivo.getCaminhoArquivo()).getFileName().toString();
            if (modificado) {
                aba.setText(nomeBase + " *");
            } else {
                aba.setText(nomeBase);
            }
        }
    }

    public void abrirArquivoEmAba(String caminho) {
        if (abasAbertas.containsKey(caminho)) {
            painelAbasSql.getSelectionModel().select(abasAbertas.get(caminho));
            return;
        }

        GerenciadorEditorSql novoEditor = new GerenciadorEditorSql(this.contexto);
        String conteudo = this.contexto.buscarConteudoArquivo(caminho);
        novoEditor.setCaminhoArquivo(caminho);
        novoEditor.setConteudoOriginal(conteudo);
        novoEditor.setTexto(conteudo);

        novoEditor.textProperty().addListener((obs, antigo, novo) -> {
            if (novoEditor == obterEditorAtivo()) {
                atualizarEstadoBotoes();
            }
        });

        novoEditor.getCodeArea().setOnMouseClicked(event -> {
            txtAreaMensagens.clear();
            novoEditor.limparErros();
        });

        String nomeArquivo = Paths.get(caminho).getFileName().toString();
        Tab aba = new Tab(nomeArquivo);
        aba.setId(caminho);
        aba.setUserData(novoEditor);
        aba.setContent(novoEditor.getScrollPane());

        aba.setOnCloseRequest(event -> {
            if (novoEditor.isModificado()) {
                boolean querDescartar = contexto.exibirDialogo(TipoDialogo.CONFIRMACAO,
                        MensagemSistema.ALTERACAO_NAO_SALVA.getMensagem(),
                        null,
                        MensagemSistema.DESCARTAR_ALTERACAO.getMensagem()
                );
                if (!querDescartar) {
                    event.consume();
                    return;
                }
            }
            abasAbertas.remove(caminho);
        });

        painelAbasSql.getTabs().add(aba);
        abasAbertas.put(caminho, aba);
        painelAbasSql.getSelectionModel().select(aba);

        atualizarEstadoBotoes();
    }

    @FXML
    private void telaConfiguracao() {
        ConstrutorJanelas.abrirJanelaSecundaria(
                CaminhoTela.TELA_CONFIGURACOES,
                this.contexto
        );
        GerenciadorVisual.aplicarTemaGlobal(contexto.getTema());
        GerenciadorVisual.aplicarVisualGlobal(contexto.getChaveFonte(), contexto.getTamanhoFonteSistema());
        atualizarFontesCustomizadas();
        verifcaEcarregarModuloFuncao();
    }

    @FXML
    private void telaNovoModulo() {
        ConstrutorJanelas.abrirJanelaSecundaria(
                CaminhoTela.TELA_NOVO_MODULO,
                this.contexto
        );
        verifcaEcarregarModuloFuncao();
    }

    @FXML
    private void telaNovaFuncao() {
        ConstrutorJanelas.abrirJanelaSecundaria(
                CaminhoTela.TELA_NOVA_FUNCAO,
                this.contexto
        );
        verifcaEcarregarModuloFuncao();
    }

    @FXML
    private void telaNovoMigration() {
        ConstrutorJanelas.abrirJanelaSecundaria(
                CaminhoTela.TELA_NOVO_MIGRATION,
                this.contexto
        );
        verifcaEcarregarModuloFuncao();
    }

    @FXML
    private void indentar() {
        GerenciadorEditorSql editorAtivo = obterEditorAtivo();
        if (editorAtivo != null && editorAtivo.getTexto() != null) {
            String sqlOriginal = editorAtivo.getTexto();
            Resultado resultado = IndentarSql.formatar(sqlOriginal);
            if (resultado.temErro()) {
                this.contexto.exibirDialogo(
                        TipoDialogo.ERRO,
                        MensagemSistema.ERRO.getMensagem(),
                        MensagemSistema.ERRO_INDENTAR_SQL.getMensagem(),
                        resultado.mensagemErro()
                );
            } else {
                editorAtivo.setTexto(resultado.valor());
            }
        }
    }

    @FXML
    private void validarSql() {
        GerenciadorEditorSql editorAtivo = obterEditorAtivo();
        if (editorAtivo == null) {
            return;
        }
        txtAreaMensagens.clear();
        editorAtivo.removeErroLinha();
        try {
            String sql = editorAtivo.getTexto();
            ValidacaoCompletaSql validador = new ValidacaoCompletaSql();
            validador.validarScriptCompleto(sql);
            this.contexto.exibirDialogo(TipoDialogo.MENSAGEM,
                    MensagemSistema.MENSAGEM_INFORMATIVA.getMensagem(),
                    null,
                    MensagemSistema.SQL_VALIDADO.getMensagem());
        } catch (SqlException e) {
            String mensagemErro = e.getMessage();
            txtAreaMensagens.setText(e.getMessage());
            Pattern pattern = Pattern.compile("linha:\\s*(\\d+)\\s*coluna:\\s*(\\d+)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(mensagemErro);

            if (matcher.find()) {
                int javaLinha = Integer.parseInt(matcher.group(1));
                editorAtivo.marcarLinhaErro(javaLinha);
            }
        } catch (Exception e) {
            txtAreaMensagens.setText(MensagemSistema.FALHA_VALIDAR_SQL.MensagemComParametro(e.getMessage()));
        }
    }

    @FXML
    private void cancelarEdicao() {
        GerenciadorEditorSql editorAtivo = obterEditorAtivo();
        if (editorAtivo != null) {
            editorAtivo.setTexto(editorAtivo.getConteudoOriginal());
            atualizarEstadoBotoes();
        }
    }

    @FXML
    private void salvarSql() {
        GerenciadorEditorSql editorAtivo = obterEditorAtivo();
        if (editorAtivo != null && editorAtivo.getCaminhoArquivo() != null) {
            try {
                String caminho = editorAtivo.getCaminhoArquivo();
                String conteudo = editorAtivo.getTexto();
                boolean querSalvar = this.contexto.exibirDialogo(TipoDialogo.CONFIRMACAO,
                        MensagemSistema.ALTERACAO_NAO_SALVA.getMensagem(),
                        null,
                        MensagemSistema.SALVAR_ALTERACAO.getMensagem());
                if (querSalvar) {
                    this.contexto.salvarArquivo(caminho, conteudo);
                    this.contexto.exibirDialogo(TipoDialogo.MENSAGEM,
                            MensagemSistema.CONFIRMACAO.getMensagem(),
                            null,
                            MensagemSistema.ARQUIVO_SALVO.getMensagem());
                    editorAtivo.setConteudoOriginal(conteudo);
                    atualizarEstadoBotoes();
                }
            } catch (Exception e) {
                this.contexto.exibirDialogo(TipoDialogo.ERRO,
                        MensagemSistema.ERRO.getMensagem(),
                        MensagemSistema.ERRO_SALVAR_ARQUIVO.getMensagem(),
                        e.getMessage());
            }
        }
    }

    @FXML
    private void verifcaEcarregarModuloFuncao() {
        if (!this.contexto.getDiretoriosConfigurados()) {
            boolean querConfigurar = this.contexto.exibirDialogo(TipoDialogo.CONFIRMACAO,
                    MensagemSistema.CADASTRAR_CONFIGURACAO.getMensagem(),
                    null,
                    MensagemSistema.IR_CONFIGURACAO.getMensagem());

            if (querConfigurar) {
                telaConfiguracao();
            }
        } else {
            GerenciadorArvoreModulos.buscarModulosFuncoes(this.contexto, treeArquivos);
        }
    }

    private void buscaTempoReal() {
        PauseTransition atrasoBusca = new PauseTransition(Duration.millis(500));
        atrasoBusca.setOnFinished(event -> {
            buscarArquivo(txtBuscarArquivo.getText());
        });
        txtBuscarArquivo.textProperty().addListener((observable, valorAntigo, valorNovo) -> {
            atrasoBusca.playFromStart();
        });
        btnBuscar.setOnAction(event -> buscarArquivo(txtBuscarArquivo.getText()));
    }

    private void buscarArquivo(String termo) {
        if (termo == null || termo.trim().isEmpty()) {
            GerenciadorArvoreModulos.buscarModulosFuncoes(this.contexto, treeArquivos);
        } else {
            GerenciadorArvoreModulos.buscarModulosFuncoesFiltrados(this.contexto, treeArquivos, termo);
        }
    }

    private void AtivaDesativaBotoesPrincipais() {
        boolean existeDiretorioConfigurado = !this.contexto.getDiretoriosConfigurados();
        btnAtualizar.setDisable(existeDiretorioConfigurado);
        btnNovoModulo.setDisable(existeDiretorioConfigurado);
        btnNovaFuncao.setDisable(existeDiretorioConfigurado);
        btnNovoMigration.setDisable(existeDiretorioConfigurado);
        btnBuscar.setDisable(existeDiretorioConfigurado);
        txtBuscarArquivo.setDisable(existeDiretorioConfigurado);
    }
}
