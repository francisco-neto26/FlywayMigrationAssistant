package com.supergestao.Flyway.migration.assistant.dominio.regra.tempo;

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
            throw new ValidacaoException("Formato de data/hora não informado.");
        }

        if (zona == null) {
            throw new ValidacaoException("Zona de data/hora não informada.");
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formato);
            return LocalDateTime.now(zona).format(formatter);
        } catch (IllegalArgumentException e) {
            throw new ValidacaoException("Formato de data/hora inválido: " + formato, e);
        }
    }
}
