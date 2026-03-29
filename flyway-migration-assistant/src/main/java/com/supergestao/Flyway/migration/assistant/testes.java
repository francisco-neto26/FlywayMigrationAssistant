package com.supergestao.Flyway.migration.assistant;

import com.supergestao.Flyway.migration.assistant.dominio.regra.tempo.GeradorDataHora;
import com.supergestao.Flyway.migration.assistant.exception.ValidacaoException;

import java.time.ZoneId;

public class testes {
    private static final ZoneId ZONA_PADRAO = ZoneId.systemDefault();

    public static void testarGeradorDataHora() {

        GeradorDataHora gerador = new GeradorDataHora();
        try {
            System.out.println("Data flyway: " +
                    gerador.gerarDataFlyway(ZONA_PADRAO));
            System.out.println("Data formatada: " +
                    gerador.gerarTimestampLocalidade("", ZONA_PADRAO));
        } catch (ValidacaoException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}
