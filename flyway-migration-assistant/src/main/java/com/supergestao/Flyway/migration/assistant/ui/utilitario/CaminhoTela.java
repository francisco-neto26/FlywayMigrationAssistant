package com.supergestao.Flyway.migration.assistant.ui.utilitario;

public enum CaminhoTela {
    JANELA_BASE("/com/supergestao/Flyway/migration/assistant/ui/controller/JanelaBase.fxml", ""),
    TELA_PRINCIPAL("/com/supergestao/Flyway/migration/assistant/ui/controller/TelaPrincipal.fxml", "Flyway Migration Assistant"),
    TELA_DIALOGO("/com/supergestao/Flyway/migration/assistant/ui/controller/TelaDialogo.fxml", ""),
    TELA_CONFIGURACOES("/com/supergestao/Flyway/migration/assistant/ui/controller/TelaConfiguracoes.fxml", "Configurações"),
    TELA_NOVO_MODULO("/com/supergestao/Flyway/migration/assistant/ui/controller/TelaNovoModulo.fxml", "Novo Módulo"),
    TELA_NOVA_FUNCAO("/com/supergestao/Flyway/migration/assistant/ui/controller/TelaNovaFuncao.fxml", "Nova Função"),
    TELA_NOVO_MIGRATION("/com/supergestao/Flyway/migration/assistant/ui/controller/TelaNovoMigration.fxml", "Novo Migration");

    private final String caminho;
    private final String nome;

    CaminhoTela(String caminho, String nome) {
        this.caminho = caminho;
        this.nome = nome;
    }

    public String getCaminho() {
        return caminho;
    }

    public String getNome() {
        return nome;
    }
}

