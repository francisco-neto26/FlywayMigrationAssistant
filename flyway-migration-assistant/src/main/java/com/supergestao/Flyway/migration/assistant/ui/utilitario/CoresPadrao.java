package com.supergestao.Flyway.migration.assistant.ui.utilitario;

public enum CoresPadrao {
    ERRO("#DC2626"),                    // Vermelho intenso
    AVISO("#EA580C"),                   // Laranja profundo
    SUCESSO("#16A34A"),                 // Verde natural e vibrante
    INFO("#2563EB"),                    // Azul royal moderno
    PADRAO("#1E293B"),                  // tom de "cinza-azulado"
    BARRA_PRINCIPAL("#0F172A"),         //Cinza escuro azulado
    BOTAO_CONFIRMAR("#10B981"),         //Esmeralda
    BOTAO_CANCELAR("#EF4444"),          //Vermelho vivo
    BOTAO_PADRAO("#3B82F6"),            //Azul brilhante
    BOTAO_CONFIRMAR_HOVER("#059669"),   // Esmeralda fechado
    BOTAO_CANCELAR_HOVER("#DC2626"),    // Vermelho escuro/alerta
    BOTAO_PADRAO_HOVER("#2563EB");      // Azul royal profundo;

    private final String corTipoMensagem;

    CoresPadrao(String corTipoMensagem) {
        this.corTipoMensagem = corTipoMensagem;
    }

    public String getCorTipoMensagem() {
        return corTipoMensagem;
    }
}
