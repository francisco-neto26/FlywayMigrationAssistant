package com.supergestao.Flyway.migration.assistant.ui.controller;

import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemSistema;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.indentar.IndentarSql;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.indentar.IndentarSqlApiPgFormatter;
import com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.ValidacaoCompletaSql;
import com.supergestao.Flyway.migration.assistant.ui.estado.ContextoAplicacao;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.*;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

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
    private StackPane containerSql;
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
    private Button btnConfiguracoes;
    @FXML
    private BorderPane painelRaiz;

    private ContextoAplicacao contexto;
    private GerenciadorArvoreArquivos gerenciadorArvoreArquivos;
    private GerenciadorEditorSql editorSql;

    public void setContextoAplicacao(ContextoAplicacao contextoAplicacao) {
        this.contexto = contextoAplicacao;
    }

    @FXML
    public void initialize() {

        this.editorSql = new GerenciadorEditorSql(containerSql);

        // Registra as ações para as teclas de atalho do editor
        this.editorSql.setAcaoSalvar(this::salvarSql);
        this.editorSql.setAcaoIndentar(this::indentar);

        buscaTempoReal();
        Platform.runLater(() -> {
            verifcaEcarregarModuloFuncao();
            this.gerenciadorArvoreArquivos = new GerenciadorArvoreArquivos(
                    this.contexto, treeArquivos, editorSql,
                    btnSalvarSql, btnCancelarEdicao, btnIndentar, btnValidarSql
            );
            this.gerenciadorArvoreArquivos.selecaoArvore();
            this.gerenciadorArvoreArquivos.limparEdicaoSql();
            GerenciadorEstiloBotao.gerenciadorEstiloBotao(painelRaiz);
            AtivaDesativaBotoesPrincipais();
        });
    }

    @FXML
    private void telaConfiguracao() {
        ConstrutorJanelas.abrirJanelaSecundaria(
                CaminhoTela.TELA_CONFIGURACOES,
                this.contexto
        );
        GerenciadorVisual.aplicarTemaGlobal(contexto.getTema());
        GerenciadorVisual.aplicarFonteGlobal(contexto.getChaveFonte());
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
        if (this.gerenciadorArvoreArquivos != null && editorSql.getTexto() != null) {
            String sqlOriginal = editorSql.getTexto();
            String sqlFormatado = IndentarSql.formatar(sqlOriginal);
            editorSql.setTexto(sqlFormatado);
        }
    }

    @FXML
    private void validarSql() {
        if (this.editorSql == null) {
            return;
        }
        // Limpa erros visuais e mensagens anteriores
        txtAreaMensagens.clear();
        this.editorSql.limparErros();
        try {
            String sql = this.editorSql.getTexto();

            // Instancia o validador que roda as regras do ANTLR4
            ValidacaoCompletaSql validador = new ValidacaoCompletaSql();
            validador.validarScriptCompleto(sql);
            // Se validou com sucesso
            this.contexto.exibirDialogo(TipoDialogo.MENSAGEM,
                    MensagemSistema.MENSAGEM_INFORMATIVA.getMensagem(),
                    null,
                    "SQL validado com sucesso! Nenhum erro de sintaxe foi detectado.");
        } catch (com.supergestao.Flyway.migration.assistant.exception.SqlException e) {
            // Captura o erro gerado pelo Parser do ANTLR e o exibe no painel de mensagens
            String mensagemErro = e.getMessage();
            txtAreaMensagens.setText(mensagemErro);
            // Faz o parse do erro (Exemplo de string: "Erro na linha: 5 coluna: 12")
            Pattern pattern = Pattern.compile("linha:\\s*(\\d+)\\s*coluna:\\s*(\\d+)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(mensagemErro);

            if (matcher.find()) {
                int linha = Integer.parseInt(matcher.group(1));
                int coluna = Integer.parseInt(matcher.group(2));

                // Marca o erro visualmente no editor SQL
                this.editorSql.marcarErro(linha, coluna, mensagemErro);
            }
        } catch (Exception e) {
            txtAreaMensagens.setText("Falha na validação do SQL: " + e.getMessage());
        }
    }

    @FXML
    private void cancelarEdicao() {
        if (this.gerenciadorArvoreArquivos != null) {
            this.gerenciadorArvoreArquivos.reverterEdicaoSql();
        }
    }

    @FXML
    private void salvarSql() {
        if (this.gerenciadorArvoreArquivos != null && this.gerenciadorArvoreArquivos.getCaminhoArquivoSelecionado() != null) {
            try {
                String caminho = this.gerenciadorArvoreArquivos.getCaminhoArquivoSelecionado();
                String conteudo = editorSql.getTexto();
                this.contexto.salvarArquivo(caminho, conteudo);
                this.contexto.exibirDialogo(TipoDialogo.MENSAGEM,
                        MensagemSistema.MENSAGEM_INFORMATIVA.getMensagem(),
                        null,
                        MensagemSistema.ARQUIVO_SALVO.getMensagem());
                this.gerenciadorArvoreArquivos.atualizarConteudoOriginalArquivo(conteudo);
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

    private void buscarArquivo(String nomeArquivo) {

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
