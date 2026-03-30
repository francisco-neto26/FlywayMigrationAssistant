package com.supergestao.Flyway.migration.assistant.dominio.mensagem;

public enum MensagemErro {

    CAMPO_OBRIGATORIO("O Campo %s tem preenchimento obrigatório."),
    FORMATO_DATA_INVALIDO("O Formato %s para data/hora é inválido."),
    FORMATO_DATA_NULL("Formato de data/hora não informado."),
    LOCALIDADE_DATA_INVALIDA("Localidade para data/hora não informada."),
    SQL_INVALIDO("SQL inválido."),
    ERRO_GENERICO("Erro inesperado."),
    ARQUIVO_NAO_JAVA("O arquivo de entrada não é um arquivo .java. \nVerifique o caminho do arquivo de entrada dos módulos, caminho usado: \n%s"),
    ERRO_PROCESSAR_ARQ_MODULO("Erro ao processar o arquivo de entrada dos módulos:"),
    ERRO_PROCESSAR_MOD_EXISTENTE("Erro ao processar o módulos existente:");

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
