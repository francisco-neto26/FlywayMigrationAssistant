package com.supergestao.Flyway.migration.assistant.dominio.modelo;

import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemErro;
import com.supergestao.Flyway.migration.assistant.exception.ValidacaoException;

public class Funcao {

    private final Modulo modulo;
    private final String nome;

    public Funcao(Modulo modulo, String nome) {

        if (modulo.getNome() == null || modulo.getNome().isBlank()) {
            throw new ValidacaoException(MensagemErro.CAMPO_OBRIGATORIO.MensagemComParametro("Id do módulo"));
        }

        if (nome == null || nome.isBlank()) {
            throw new ValidacaoException(MensagemErro.CAMPO_OBRIGATORIO.MensagemComParametro("Nome do módulo"));
        }

        this.modulo = modulo;
        this.nome = nome;
    }

    public Modulo getModulo() {
        return modulo;
    }

    public String getNome() {
        return nome;
    }

    public boolean pertenceAFuncao(String nomeArquivo) {
        return nomeArquivo != null && nomeArquivo.startsWith(nome);
    }

    @Override
    public String toString() {
        return nome;
    }

}
