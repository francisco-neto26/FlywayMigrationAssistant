package com.supergestao.Flyway.migration.assistant.dominio.regra.tempo;

import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemErro;
import com.supergestao.Flyway.migration.assistant.exception.ValidacaoException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class GeradorDataHora {

    /** * Gera timestamp no padrão Flyway Ex: 20260328153045 */
    public String gerarDataFlyway(ZoneId localidade) {
        return gerarTimestampLocalidade("yyyyMMddHHmmss", localidade);
    }

    public String gerarTimestampLocalidade(String formato, ZoneId zona) {

        if (formato == null || formato.isBlank()) {
            throw new ValidacaoException(MensagemErro.FORMATO_DATA_NULL.getMensagem());
        }

        if (zona == null) {
            throw new ValidacaoException(MensagemErro.LOCALIDADE_DATA_INVALIDA.getMensagem());
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formato);
            return LocalDateTime.now(zona).format(formatter);
        } catch (IllegalArgumentException e) {
            throw new ValidacaoException(MensagemErro.FORMATO_DATA_INVALIDO.MensagemComParametro(formato), e);
        }
    }

    public static String formatarTimestampLocalidade(LocalDateTime datahora, String formato) {

        if (datahora == null) {
            throw new ValidacaoException(MensagemErro.CAMPO_OBRIGATORIO.MensagemComParametro("Data/Hora para formatação"));
        }

        if (formato == null || formato.isBlank()) {
            throw new ValidacaoException(MensagemErro.FORMATO_DATA_NULL.getMensagem());
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formato);
            return datahora.format(formatter);
        } catch (IllegalArgumentException e) {
            throw new ValidacaoException(MensagemErro.FORMATO_DATA_INVALIDO.MensagemComParametro(formato), e);
        }
    }

}
