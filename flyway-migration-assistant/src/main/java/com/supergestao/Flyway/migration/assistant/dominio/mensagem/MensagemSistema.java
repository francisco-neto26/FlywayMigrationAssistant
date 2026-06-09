package com.supergestao.Flyway.migration.assistant.dominio.mensagem;

public enum MensagemSistema {

    ATENCAO("Atenção!"),
    MENSAGEM_INFORMATIVA("Mensagem informativa"),
    CONFIRMACAO("Confirmação"),
    ERRO("Erro"),
    ALERTA("Alerta"),
    NOVA_FUNCAO("Nova Função"),
    NOVO_MODULO("Novo Módulo"),
    CADASTRAR_CONFIGURACAO("Cadastrar configurações"),
    SELECIONE_PASTA_ORIGEM("Selecione a pasta de origem"),
    CRIAR_MODULO("Deseja criar o módulo %s no diretorio %s?"),
    IR_CONFIGURACAO("Cadastrar configurações"),
    DESEJA_SALVAR_ALTERACAO("Deseja salvar as alterações?"),
    GRAVAR_REGEDIT("Não foi possível gravar no RegEdit do Windows"),
    EXISTE_MODULOS_CRIAR("Existe novos módulos a serem criados. Deseja criar agora?"),
    SALVAR_FUNCAO("Deseja criar a função %s no diretório: %s?"),
    LISTA_FUNCAO("Lista de funções."),
    LISTA_MODULO("Lista de módulos."),
    ERRO_SALVAR_REGISTRO("Erro ao salvar registro"),
    ERRO_CRIAR_ARVORE_MODULOS("Erro montar Arvore de Módulos/Funções."),
    ERRO_CRIAR_ARVORE_MODULOS_ARQUIVOS("Erro montar Arvore de Módulos/Funções com arquivos."),
    ERRO_CRIAR_ARVORE_ARQUIVOS("Não foi possível carregar os arquivos do módulo: %s"),
    CAMPO_OBRIGATORIO("O Campo %s tem preenchimento obrigatório."),
    FORMATO_DATA_INVALIDO("O Formato %s para data/hora é inválido."),
    FORMATO_DATA_NULL("Formato de data/hora não informado."),
    LOCALIDADE_DATA_INVALIDA("Localidade para data/hora não informada."),
    ARQUIVO_NAO_JAVA("O arquivo de entrada não é um arquivo .java. \nVerifique o caminho do arquivo de entrada dos módulos, caminho usado: \n%s"),
    ARQUIVO_JAVA("Selecione o arquivo .java com os módulos"),
    ERRO_PROCESSAR_ARQ_MODULO("Erro ao processar o arquivo de entrada dos módulos."),
    ERRO_PROCESSAR_MOD_EXISTENTE("Erro ao processar o módulos existente."),
    ERRO_SALVAR_MODULO("Erro ao salvar o módulo: %s"),
    ERRO_PROCESSAR_FUN_EXISTENTE("Erro ao processar as funções existentes."),
    ERRO_PROCESSAR_ARQ_EXISTENTE("Erro ao processar o arquivo migration: %s"),
    ERRO_ACESSAR_ARQ_EXISTENTE("Erro ao acessar os arquivos da função %s"),
    ERRO_SALVAR_FUNCAO("Erro ao salvar a função: %s"),
    ERRO_SALVAR_ARQUIVO("Erro ao salvar o arquivo: %s"),
    ERRO_OBTER_ACAO_BANCO("Erro ao converte Ação Banco: %s"),
    ERRO_CONVERTER_NOME_UNDO("Erro ao converte nome do arquivo Undo: %s"),
    ERRO_ARQUIVO_NAO_VERSIONED("O Arquivo não é do tipo Versioned Migration"),
    NAO_ARQUIVO_MIGRATION("O arquivo não é uma migration válida do Flyway!"),
    SCRIPT_VAZIO("O script SQL está completamente vazio e não tem efeito."),
    SCRIPT_NAO_PERMITIDO("No script SQL existe comandos não permitidos. %s"),
    SCRIPT_NAO_SEM_CONDICIONAL("No script SQL existe comandos que deveriam conter condicional. %s"),
    SCRIPT_ERRO_SINTAXE("O script SQL contem erro de sintaxe: %s"),
    SCRIPT_SEM_DICIONARIO("Ausência de Dicionário de Dados: Faltou a instrução 'COMMENT ON' para documentar a criação: %s"),
    SCRIPT_SEM_PADRAO_SQL("Não segue o padrão SQL: %s"),
    ERRO_ABERTURA_TELA("Erro ao tentar abrir a tela de: %s"),
    ERRO_GENERICO("Erro inesperado: %s");

    private final String mensagem;

    MensagemSistema(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getMensagem() {
        return mensagem;
    }

    public String MensagemComParametro(Object... parametros) {
        return String.format(this.mensagem, parametros);
    }

}
