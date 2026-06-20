package com.supergestao.Flyway.migration.assistant.ui.utilitario;

public enum TipoDialogo {
    CONFIRMACAO(1, "Confirmação"),
    ALERTA(2, "Alerta"),
    MENSAGEM(3,"Mensagem do Sistema"),
    ERRO(4, "Erro");

    private final int tipoDialogo;
    private final String nome;


    TipoDialogo(int tipoDialogo, String nome) {
        this.tipoDialogo = tipoDialogo;
        this.nome = nome;
    }

    public int getTipoDialogo() {
        return tipoDialogo;
    }

    public String getNome() {
        return nome;
    }

}
