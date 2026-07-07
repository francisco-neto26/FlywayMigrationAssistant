package com.supergestao.Flyway.migration.assistant.ui.utilitario.editorSql;

import com.supergestao.Flyway.migration.assistant.dominio.tipo.AtalhoTeclado;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.ScrollEvent;
import org.fxmisc.richtext.CodeArea;

public class GerenciadorAtalhosEditor {

    public static void configurar(GerenciadorEditorSql editor) {
        CodeArea codeArea = editor.getCodeArea();

        // 1. Evento de Mouse: Zoom com Ctrl + Scroll
        codeArea.addEventFilter(ScrollEvent.SCROLL, event -> {
            if (event.isControlDown()) {
                double deltaY = event.getDeltaY();
                if (deltaY > 0) {
                    editor.alterarTamanhoFonte(1);
                } else if (deltaY < 0) {
                    editor.alterarTamanhoFonte(-1);
                }
                event.consume();
            }
        });

        // 2. Evento de Teclado: Dinâmico baseado no Enum
        codeArea.setOnKeyPressed(event -> {
            for (AtalhoTeclado atalho : AtalhoTeclado.values()) {
                if (atalho.matches(event)) {
                    executarAtalho(editor, atalho);
                    event.consume();
                    return; // Retorna imediatamente ao processar
                }
            }
        });
    }

    /**
     * Executa a regra lógica atrelada a cada atalho catalogado.
     */
    private static void executarAtalho(GerenciadorEditorSql editor, AtalhoTeclado atalho) {
        switch (atalho) {
            case SALVAR -> {
                if (editor.getAcaoSalvar() != null) {
                    editor.getAcaoSalvar().run();
                }
            }
            case INDENTAR -> {
                if (editor.getAcaoIndentar() != null) {
                    editor.getAcaoIndentar().run();
                }
            }
            case DESFAZER -> editor.getCodeArea().undo();
            case REFAZER -> editor.getCodeArea().redo();
            case AUMENTAR_FONTE -> editor.alterarTamanhoFonte(1);
            case DIMINUIR_FONTE -> editor.alterarTamanhoFonte(-1);
            case PROXIMO_MARCADOR -> editor.getGerenciadorColunaNumeracao().irParaProximoMarcador(false);
            case MARCADOR_ANTERIOR -> editor.getGerenciadorColunaNumeracao().irParaProximoMarcador(true);
        }
    }
}
