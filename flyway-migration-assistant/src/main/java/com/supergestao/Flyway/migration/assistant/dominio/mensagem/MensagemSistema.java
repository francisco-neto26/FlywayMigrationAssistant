package com.supergestao.Flyway.migration.assistant.dominio.mensagem;

public enum MensagemSistema {

    ATENCAO("Atenção!"),
    MENSAGEM_INFORMATIVA("Mensagem informativa"),
    CONFIRMACAO("Confirmação"),
    CONFIRMAR("Confirmar"),
    SAIR("Sair"),
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
    ERRO_CRIAR_ARVORE_ARQUIVOS("Não foi possível carregar os arquivo(s) do módulo: %s"),
    ARQUIVO_SALVO("Arquivo SQL salvo com sucesso!"),
    ALTERACAO_NAO_SALVA("Alterações não salvas"),
    DESCARTAR_ALTERACAO("O arquivo atual possui alterações não salvas. Deseja descartá-las para abrir o novo arquivo?"),
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
    ERRO_GENERICO("Erro inesperado: %s"),
    ERRO_CSS("Erro ao carregar o arquivo CSS do editor SQL."),
    ERRO_LINHA_ERRO("Erro ao marcar o erro no editor SQL."),
    ERRO_PERL("Interpretador Perl não encontrado no PATH nem nos caminhos do Git.\nInstale o Strawberry Perl (https://strawberryperl.com/) ou adicione o Perl do Git às variáveis de ambiente."),
    ERRO_RODAR_PGFORMATTER("Erro ao rodar pgFormatter local, código de saída:  %s: %s"),
    ERRO_IO_PGFORMATTER("Erro de E/S ao extrair ou rodar o pgFormatter, certifique-se de ter o Perl ou Git instalado: %s"),
    ERRO_INTERRUPCAO_PGFORMATTER("Processo do pgFormatter foi interrompido: %s"),
    ERRO_GERAL_PGFORMATTER("Falha geral ao formatar SQL localmente (certifique-se de que o Perl está no PATH): %s"),
    ERRO_INDENTAR_SQL("Falha ao indentar SQL"),
    SQL_VALIDADO("SQL validado com sucesso! Nenhum erro de sintaxe foi detectado."),
    FALHA_VALIDAR_SQL("Falha na validação do SQL: %s"),
    ERRO_LER_CLASS("Recurso não encontrado no classpath: %s"),
    ERRO_LINHA_SQL("Erro na linha: %s coluna: %s\nMensagem: %s"),
    SALVAR_ALTERACAO("Deseja salvar as alterações?"),
    CAMPO_DETALHES_VAZIO("O campo detalhes não pode ser vazio para o tipo de diálogo: %s"),
    ERRO_SALVAR_CONFIG("Erro ao salvar configurações");

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
