package com.supergestao.Flyway.migration.assistant.dominio.modelo;

import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemErro;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.TipoMigration;
import com.supergestao.Flyway.migration.assistant.exception.ValidacaoException;

import java.time.LocalDateTime;

public class Arquivo {

    private final String nome;
    private final String modulo;
    private final String caminho;
    private final TipoMigration tipo;
    private final LocalDateTime dataCriacao;

    public Arquivo(String nome, String modulo, String caminho, TipoMigration tipo) {

        if (nome == null || nome.isBlank()) {
            throw new ValidacaoException(MensagemErro.CAMPO_OBRIGATORIO.MensagemComParametro("Nome do arquivo"));
        }

        if (modulo == null || modulo.isBlank()) {
            throw new ValidacaoException(MensagemErro.CAMPO_OBRIGATORIO.MensagemComParametro("Módulo"));
        }

        if (caminho == null || caminho.isBlank()) {
            throw new ValidacaoException(MensagemErro.CAMPO_OBRIGATORIO.MensagemComParametro("Caminho do arquivo"));
        }

        if (tipo == null) {
            throw new ValidacaoException(MensagemErro.CAMPO_OBRIGATORIO.MensagemComParametro("Tipo de migração"));
        }

        this.nome = nome;
        this.modulo = modulo;
        this.caminho = caminho;
        this.tipo = tipo;
        this.dataCriacao = LocalDateTime.now();
    }

    public String getNome() {
        return nome;
    }

    public String getModulo() {
        return modulo;
    }

    public String getCaminho() {
        return caminho;
    }

    public TipoMigration getTipo() {
        return tipo;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public boolean isMigration() {
        return nome.endsWith(".sql");
    }

    @Override
    public String toString() {
        return nome;
    }
}
