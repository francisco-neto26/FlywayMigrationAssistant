package com.supergestao.FlywayMigrationAssistant.model;

public class Modulo {

    private Long id;
    private String nome;
    private String constante;
    private String prefixo;
    private String descricao;

    public Modulo(Long id, String nome, String constante, String prefixo, String descricao) {
        this.id = id;
        this.nome = nome;
        this.constante = constante;
        this.prefixo = prefixo;
        this.descricao = descricao;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getconstante() {
        return constante;
    }

    public void setconstante(String constante) {
        this.constante = constante;
    }

    public String getPrefixo() {
        return prefixo;
    }

    public void setPrefixo(String prefixo) {
        this.prefixo = prefixo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
