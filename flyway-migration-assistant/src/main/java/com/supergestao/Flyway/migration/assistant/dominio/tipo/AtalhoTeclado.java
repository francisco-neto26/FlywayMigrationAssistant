package com.supergestao.Flyway.migration.assistant.dominio.tipo;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public enum AtalhoTeclado {
    SALVAR("Ctrl + S", "Salvar script SQL ativo no disco.", KeyCode.S, true, false),
    INDENTAR("Ctrl + I", "Formatar e indentar o código SQL automaticamente.", KeyCode.I, true, false),
    DESFAZER("Ctrl + Z", "Desfazer a última alteração feita no editor.", KeyCode.Z, true, false),
    REFAZER("Ctrl + Y", "Refazer a última alteração desfeita.", KeyCode.Y, true, false),
    AUMENTAR_FONTE("Ctrl + + / Ctrl + Scroll", "Aumentar o tamanho da fonte do código no editor.", null, true, false),
    DIMINUIR_FONTE("Ctrl + - / Ctrl + Scroll", "Diminuir o tamanho da fonte do código no editor.", null, true, false),
    PROXIMO_MARCADOR("F2", "Navegar para o próximo marcador de linha (Bookmark).", KeyCode.F2, false, false),
    MARCADOR_ANTERIOR("Shift + F2", "Navegar para o marcador de linha anterior (Bookmark).", KeyCode.F2, false, true);

    private final String combinacaoTeclas;
    private final String descricao;
    private final KeyCode keyCode;
    private final boolean ctrlRequerido;
    private final boolean shiftRequerido;

    AtalhoTeclado(String combinacaoTeclas, String descricao, KeyCode keyCode, boolean ctrlRequerido, boolean shiftRequerido) {
        this.combinacaoTeclas = combinacaoTeclas;
        this.descricao = descricao;
        this.keyCode = keyCode;
        this.ctrlRequerido = ctrlRequerido;
        this.shiftRequerido = shiftRequerido;
    }

    public String getCombinacaoTeclas() {
        return combinacaoTeclas;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean matches(KeyEvent event) {
        // Valida modificadores de forma estrita
        if (event.isControlDown() != ctrlRequerido) return false;
        if (event.isShiftDown() != shiftRequerido) return false;

        KeyCode code = event.getCode();

        // Tratamento especial para o Zoom devido aos múltiplos botões de mais e menos do teclado
        if (this == AUMENTAR_FONTE) {
            return code == KeyCode.EQUALS || code == KeyCode.ADD;
        }
        if (this == DIMINUIR_FONTE) {
            return code == KeyCode.MINUS || code == KeyCode.SUBTRACT;
        }

        return code == keyCode;
    }
}
