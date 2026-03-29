package com.supergestao.Flyway.migration.assistant.dominio.mensagem;

public enum MensagemErro {

    CAMPO_OBRIGATORIO("O Campo %s tem preenchimento obrigatório."),
    FORMATO_DATA_INVALIDO("O Formato %s para data/hora é inválido."),
    FORMATO_DATA_NULL("Formato de data/hora não informado."),
    LOCALIDADE_DATA_INVALIDA("Localidade para data/hora não informada."),
    SQL_INVALIDO("SQL inválido."),
    ERRO_GENERICO("Erro inesperado.");

    private final String mensagem;

    MensagemErro(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getMensagem() {
        return mensagem;
    }

    public String MensagemComParametro(Object... parametros) {
        return String.format(this.mensagem, parametros);
    }

}
