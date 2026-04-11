package com.supergestao.Flyway.migration.assistant.dominio.modelo;

import com.supergestao.Flyway.migration.assistant.dominio.tipo.TipoMigration;

import java.time.LocalDateTime;

public class Arquivo {

    private final String nome;
    private final TipoMigration tipo;
    private final LocalDateTime dataCriacao;
    private final LocalDateTime dataAlteracao;

    public Arquivo(String nome, TipoMigration tipo, LocalDateTime dataCriacao, LocalDateTime dataAlteracao) {
        this.nome = nome;
        this.tipo = tipo;
        this.dataCriacao = dataCriacao;
        this.dataAlteracao = dataAlteracao;
    }

    public String getNome() {
        return nome;
    }

    public TipoMigration getTipo() {
        return tipo;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
    public LocalDateTime getDataAlteracao() {
        return dataAlteracao;
    }

    public boolean isMigration() {
        return nome.endsWith(".sql");
    }

    @Override
    public String toString() {
        return nome;
    }
}
