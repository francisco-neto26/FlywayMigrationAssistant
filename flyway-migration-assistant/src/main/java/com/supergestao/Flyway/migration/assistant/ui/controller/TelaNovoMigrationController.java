package com.supergestao.Flyway.migration.assistant.ui.controller;

import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemSistema;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Funcao;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Modulo;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.AcaoBanco;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.ObjetoBanco;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.SubAcaoBanco;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.TipoMigration;
import com.supergestao.Flyway.migration.assistant.ui.estado.ContextoAplicacao;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.estilo.GerenciadorEstiloBotao;
import com.supergestao.Flyway.migration.assistant.ui.utilitario.janela.TipoDialogo;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

public class TelaNovoMigrationController implements ITelasModal {

    @FXML
    public Label lblModulo;
    @FXML
    public ComboBox<Modulo> comboModulo;
    @FXML
    public Label lblFuncao;
    @FXML
    public ComboBox<Funcao> comboFuncao;
    @FXML
    public Label lblTipo;
    @FXML
    public ComboBox<TipoMigration> comboTipo;
    @FXML
    public Label lblAcao;
    @FXML
    public ComboBox<AcaoBanco> comboAcao;
    @FXML
    public Label lblObjeto;
    @FXML
    public ComboBox<ObjetoBanco> comboObjeto;
    @FXML
    public Label lblSubAcao;
    @FXML
    public ComboBox<SubAcaoBanco> comboSubAcao;
    @FXML
    public Label lblBuscarMigration;
    @FXML
    public TextField txtBuscaUndo;
    @FXML
    public Button btnBuscarUndo;
    @FXML
    public Label lblNomeScript;
    @FXML
    public TextField txtNomeScript;
    @FXML
    public Label lblNomeCompleto;
    @FXML
    public TextField txtPreviewArquivo;
    @FXML
    private VBox painelRaiz;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnSalvarMigration;
    @FXML
    private VBox blocoBuscaUndo;

    private ContextoAplicacao contexto;

    public void setContextoAplicacao(ContextoAplicacao contextoAplicacao) {
        this.contexto = contextoAplicacao;
    }

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            GerenciadorEstiloBotao.gerenciadorEstiloBotao(painelRaiz);
            iniciarCombo();
            //defineNomeArquivo();
            monitoraAlteracaoCampos();
            controleCamposUndo(null);
            controleTeclaEsc();
        });
    }

    @FXML
    private void fechar() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void salvarMigration() {
        String caminhoArquivoNovo = defineDiretorioNomeArquivo().toString();
        String conteudoArquivoNovo = null;

        if (comboTipo.getValue() == TipoMigration.UNDO) {
            conteudoArquivoNovo = this.contexto.buscarConteudoArquivo(txtBuscaUndo.getText());
        } else {
            if (comboAcao.getValue() == null) {
                this.contexto.exibirDialogo(TipoDialogo.ERRO,
                        MensagemSistema.ALERTA.getMensagem(),
                        MensagemSistema.ATENCAO.getMensagem(),
                        MensagemSistema.CAMPO_OBRIGATORIO.MensagemComParametro(obterTextoLabelComboBox(comboAcao))
                );
                return;
            }
            if (comboObjeto.getValue() == null) {
                this.contexto.exibirDialogo(TipoDialogo.ERRO,
                        MensagemSistema.ALERTA.getMensagem(),
                        MensagemSistema.ATENCAO.getMensagem(),
                        MensagemSistema.CAMPO_OBRIGATORIO.MensagemComParametro(obterTextoLabelComboBox(comboObjeto))
                );
                return;
            }
        }

        if (txtNomeScript.getText() == null || txtNomeScript.getText().isBlank()) {
            this.contexto.exibirDialogo(TipoDialogo.ERRO,
                    MensagemSistema.ALERTA.getMensagem(),
                    MensagemSistema.ATENCAO.getMensagem(),
                    MensagemSistema.CAMPO_OBRIGATORIO.MensagemComParametro(obterTextoLabelText(txtNomeScript))
            );
            return;
        }

        this.contexto.salvarArquivo(caminhoArquivoNovo, conteudoArquivoNovo);
        fechar();
    }

    @FXML
    private void obterArquivosUndo() {
        FileChooser telaBuscarUndo = new FileChooser();
        telaBuscarUndo.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Arquivos .sql", "*.sql")
        );
        telaBuscarUndo.setTitle(MensagemSistema.SELECIONE_UNDO.getMensagem());

        File pastaOrigemUndo = defineDiretorioArquivo().toFile();

        if (pastaOrigemUndo.exists() && pastaOrigemUndo.isDirectory()) {
            telaBuscarUndo.setInitialDirectory(pastaOrigemUndo);
        }

        Stage stage = (Stage) btnBuscarUndo.getScene().getWindow();
        File arquivoSelecionado = telaBuscarUndo.showOpenDialog(stage);

        if (arquivoSelecionado != null) {
            txtBuscaUndo.setText(arquivoSelecionado.getAbsolutePath());
            txtNomeScript.setText(arquivoSelecionado.getName());
        }

    }

    private List<ComboBox<?>> obterCombos() {
        return List.of(comboModulo,
                comboFuncao,
                comboTipo,
                comboAcao,
                comboObjeto,
                comboSubAcao
        );
    }

    private void iniciarCombo() {

        List<ComboBox<?>> combos = obterCombos();

        for (ComboBox<?> combo : combos) {
            switch (combo.getId()) {
                case "comboTipo" -> carregaComboTipo();
                case "comboModulo" -> carregaComboModulo();
                case "comboAcao" -> carregaComboAcao();
                case "comboObjeto" -> carregaComboObjeto();
                case "comboSubAcao" -> carregaComboSubAcao();
            }
        }
    }

    private void carregaComboModulo() {
        definePromptText(comboModulo);
        comboModulo.getItems().addAll(this.contexto.obterModulosExistentes(this.contexto.getDiretorioArquivo()).values());
        comboModulo.valueProperty().addListener((observable, moduloAntigo, moduloSelecionado) -> {
            carregarFuncaoModulo(moduloSelecionado);
        });
        comboFuncao.setDisable(true);
    }

    private void carregaComboAcao() {
        comboAcao.getItems().addAll(List.of(AcaoBanco.values()));
    }

    private void carregaComboObjeto() {
        comboObjeto.getItems().addAll(List.of(ObjetoBanco.values()));
    }


    private void carregaComboSubAcao() {
        comboSubAcao.getItems().addAll(List.of(SubAcaoBanco.values()));
    }

    private void carregaComboTipo() {
        definePromptText(comboTipo);
        comboTipo.getItems().addAll(List.of(TipoMigration.values()));
        comboTipo.valueProperty().addListener((observable, tipoAntigo, tipoSelecionado) -> {
            controleCamposUndo(tipoSelecionado);
        });
    }

    private void monitoraAlteracaoCampos() {

        for (ComboBox<?> combo : obterCombos()) {
            combo.valueProperty().addListener((observable, valorAntigo, valorNovo) -> {

                controlaCampoNomeScript();

                if (valorAntigo == valorNovo) {
                    return;
                }

                if (combo == comboTipo && valorNovo == TipoMigration.UNDO) {
                    txtNomeScript.setText("");
                    return;
                }

                if (txtNomeScript.textProperty().getValue() != null && !txtNomeScript.textProperty().getValue().isBlank()) {
                    defineNomeArquivo();
                }
            });
        }

        txtNomeScript.textProperty().addListener((observable, textoAntigo, textoNovo) -> {
            defineNomeArquivo();
        });
    }

    private void defineNomeArquivo() {
        String nomeArquivoUndo = txtNomeScript.textProperty().getValue();
        try {
            if (comboTipo.getValue() == TipoMigration.UNDO) {
                if (txtBuscaUndo.getText() != null && !txtBuscaUndo.getText().isBlank()) {
                    txtPreviewArquivo.setText(this.contexto.gerarNomeArquivoMigrationUndo(nomeArquivoUndo));
                }
            } else {
                if (txtNomeScript.isDisable()) {
                    return;
                }

                for (ComboBox<?> combo : obterCombos()) {
                    if (combo != comboFuncao && combo != comboSubAcao) {
                        if (combo.getValue() == null) {
                            this.contexto.exibirDialogo(TipoDialogo.ALERTA,
                                    MensagemSistema.ATENCAO.getMensagem(),
                                    MensagemSistema.VERIFIQUE_PREENCHIMENTO.getMensagem(),
                                    MensagemSistema.CAMPO_OBRIGATORIO.MensagemComParametro(obterTextoLabelComboBox(combo)));
                            return;
                        }
                    }
                }

                txtPreviewArquivo.setText(this.contexto.gerarNomeArquivoMigration(
                        comboTipo.getValue(),
                        comboAcao.getValue(),
                        comboObjeto.getValue(),
                        comboFuncao.getValue() != null ? comboFuncao.getValue().getNome() : "",
                        nomeArquivoUndo
                ));
            }

        } catch (Exception e) {
            this.contexto.exibirDialogo(TipoDialogo.ERRO,
                    MensagemSistema.ALERTA.getMensagem(),
                    MensagemSistema.ATENCAO.getMensagem(),
                    e.getMessage()
            );
        }
    }

    private void carregarFuncaoModulo(Modulo modulo) {
        limparComboBox(comboFuncao);
        if (modulo != null && !modulo.getFuncoes().isEmpty()) {
            definePromptText(comboFuncao);
            comboFuncao.getItems().addAll(modulo.getFuncoes());
            comboFuncao.setDisable(false);
        } else {
            comboFuncao.getItems().clear();
            comboFuncao.setPromptText(null);
            comboFuncao.setDisable(true);
        }
    }

    private void controleCamposUndo(TipoMigration tipoSelecionado) {
        boolean tipoUndo = tipoSelecionado == TipoMigration.UNDO;
        btnBuscarUndo.setDisable(!tipoUndo);
        blocoBuscaUndo.setVisible(tipoUndo);
        blocoBuscaUndo.setManaged(tipoUndo);
        controleCamposAdicionais();
    }

    private void controleCamposAdicionais() {
        boolean tipoUndo = comboTipo.getValue() == TipoMigration.UNDO;
        boolean tipoUndoNull = comboTipo.getValue() == null;
        List<ComboBox<?>> combos = List.of(comboAcao,
                comboObjeto,
                comboSubAcao
        );

        if (tipoUndo || tipoUndoNull) {
            for (ComboBox<?> combo : combos) {
                combo.setDisable(true);
                combo.setPromptText(null);
                combo.getSelectionModel().clearSelection();
                combo.setValue(null);
            }
        } else {
            for (ComboBox<?> combo : combos) {
                combo.setDisable(false);
                definePromptText(combo);
            }
        }

    }

    private void controlaCampoNomeScript() {
        boolean comboComValor = true;
        for (ComboBox<?> comboValor : obterCombos()) {
            if (comboValor != comboFuncao && comboValor != comboSubAcao) {
                if (!comboBoxTemValor(comboValor) && (comboComValor)) {
                    comboComValor = false;
                }
            }
        }

        if (comboComValor) {
            txtNomeScript.setDisable(false);
        } else {
            txtNomeScript.setDisable(true);
            txtNomeScript.setText(null);
        }
    }

    private boolean comboBoxTemValor(ComboBox<?> comboBox) {
        return comboBox.getValue() != null;
    }

    private void controleTeclaEsc() {
        painelRaiz.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                Node campoSelecionado = painelRaiz.getScene().getFocusOwner();
                if (campoSelecionado instanceof ComboBox<?> comboBox) {
                    limparComboBox(comboBox);
                    controlaCampoNomeScript();
                    if (comboBox == comboFuncao) {
                        carregarFuncaoModulo(comboModulo.getValue());
                    }
                }
            }
        });
    }


    private <T> void limparComboBox(ComboBox<T> comboBox) {

        String texto = comboBox.getPromptText();
        comboBox.getSelectionModel().clearSelection();
        comboBox.setValue(null);

        comboBox.setButtonCell(new ListCell<T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(texto);
                } else {
                    setText(item.toString());
                }
            }
        });
        if (comboBox == comboFuncao) {
            comboBox.getItems().clear();
        }
    }

    private void definePromptText(ComboBox<?> comboBox) {
        comboBox.setPromptText(MensagemSistema.SELECIONE.MensagemComParametro(obterTextoLabelComboBox(comboBox)).replace(":", ""));
    }

    private Path defineDiretorioArquivo() {
        return Paths.get(this.contexto.getDiretorioArquivo(),
                (comboModulo.getSelectionModel().getSelectedItem() == null) ? "" : comboModulo.getValue().getNome(),
                (comboFuncao.getSelectionModel().getSelectedItem() == null) ? "" : comboFuncao.getValue().getNome()
        );
    }

    private Path defineDiretorioNomeArquivo() {
        return Paths.get(defineDiretorioArquivo().toString(), txtNomeScript.getText());
    }

    private String obterTextoLabelComboBox(ComboBox<?> comboBox) {
        if (comboBox == null || comboBox.getParent() == null) {
            return "";
        }

        Parent vboxComboBox = comboBox.getParent();
        for (Node elementoVbox : vboxComboBox.getChildrenUnmodifiable()) {
            if (elementoVbox instanceof Label textoLabel) {
                return textoLabel.getText().replace(":", "");
            }
        }
        return "";
    }

    private String obterTextoLabelText(TextField textField) {
        if (textField == null || textField.getParent() == null) {
            return "";
        }

        Parent vboxComboBox = textField.getParent();
        for (Node elementoVbox : vboxComboBox.getChildrenUnmodifiable()) {
            if (elementoVbox instanceof Label textoLabel) {
                return textoLabel.getText();
            }
        }
        return "";
    }

}



