package com.supergestao.Flyway.migration.assistant.ui.estilo.sql;

import java.awt.*;

public enum CorSql {
    //revisar as cores num momento mais adequado.
    COMANDO(new Color(0, 0, 180)),
    TIPO_DADO(new Color(102, 0, 153)),
    IDENTIFICADOR(new Color(33, 33, 33)),
    STRING(new Color(163, 21, 21)),
    NUMERO(new Color(128, 0, 128)),
    COMENTARIO(new Color(0, 128, 0)),
    FUNCAO(new Color(0, 102, 153)),
    OPERADOR(new Color(0, 0, 0)),
    PONTUACAO(new Color(120, 120, 120)),
    ERRO(new Color(211, 47, 47));

    private final Color cor;

    CorSql(Color cor) {
        this.cor = cor;
    }

    public Color getCor() {
        return cor;
    }
}
