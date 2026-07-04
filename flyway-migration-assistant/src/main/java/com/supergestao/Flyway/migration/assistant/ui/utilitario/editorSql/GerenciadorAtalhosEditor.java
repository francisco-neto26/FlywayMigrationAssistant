package com.supergestao.Flyway.migration.assistant.ui.utilitario.editorSql;

import javafx.scene.input.KeyCode;
import javafx.scene.input.ScrollEvent;
import org.fxmisc.richtext.CodeArea;

public class GerenciadorAtalhosEditor {

    /*Eventos de teclado e mouse(scroll) na área de texto do editor*/
    public static void configurar(GerenciadorEditorSql editor) {
        CodeArea codeArea = editor.getCodeArea();

        //Evento mouse: Zoom com Ctrl + scroll
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

        //Evento Teclado: Atalhos com Ctrl
        codeArea.setOnKeyPressed(event -> {
            if (event.isControlDown()) {
                KeyCode key = event.getCode();

                switch (key) {
                    case S -> {
                        if (editor.getAcaoSalvar() != null) {
                            editor.getAcaoSalvar().run();
                            event.consume();
                        }
                    }
                    case I -> {
                        if (editor.getAcaoIndentar() != null) {
                            editor.getAcaoIndentar().run();
                            event.consume();
                        }
                    }
                    case Z -> {
                        codeArea.undo();
                        event.consume();
                    }
                    case Y -> {
                        codeArea.redo();
                        event.consume();
                    }
                    case EQUALS, ADD -> {
                        editor.alterarTamanhoFonte(1);
                        event.consume();
                    }
                    case MINUS, SUBTRACT -> {
                        editor.alterarTamanhoFonte(-1);
                        event.consume();
                    }
                    default -> {}
                }
            }
        });
    }
}
