package com.supergestao.Flyway.migration.assistant.dominio.tipo;

public enum AtalhoTeclado {
    SALVAR("Ctrl + S", "Salvar script SQL ativo no disco."),
    INDENTAR("Ctrl + I", "Formatar e indentar o código SQL automaticamente."),
    DESFAZER("Ctrl + Z", "Desfazer a última alteração feita no editor."),
    REFAZER("Ctrl + Y", "Refazer a última alteração desfeita."),
    AUMENTAR_FONTE("Ctrl + + / Ctrl + Scroll Up", "Aumentar o tamanho da fonte do código no editor."),
    DIMINUIR_FONTE("Ctrl + - / Ctrl + Scroll Down", "Diminuir o tamanho da fonte do código no editor.");
    private final String combinacaoTeclas;
    private final String descricao;

    AtalhoTeclado(String combinacaoTeclas, String descricao) {
        this.combinacaoTeclas = combinacaoTeclas;
        this.descricao = descricao;
    }

    public String getCombinacaoTeclas() {
        return combinacaoTeclas;
    }

    public String getDescricao() {
        return descricao;
    }
}
