package com.supergestao.Flyway.migration.assistant.dominio.modelo;

public record Resultado(boolean sucesso, String valor, String mensagemErro) {
    public boolean temErro() {
        return mensagemErro != null;
    }
}
