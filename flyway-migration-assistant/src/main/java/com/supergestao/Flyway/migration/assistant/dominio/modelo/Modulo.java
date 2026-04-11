package com.supergestao.Flyway.migration.assistant.dominio.modelo;

import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemErro;
import com.supergestao.Flyway.migration.assistant.exception.ValidacaoException;

import java.util.ArrayList;
import java.util.List;

public class Modulo {

    private final Long id;
    private final String nome;
    private String constante;
    private final String prefixo;
    private String descricao;
    private final List<Funcao> funcoes = new ArrayList<>();

    public Modulo(Long id, String nome, String constante, String prefixo, String descricao) {

        if (id == null) {
            throw new ValidacaoException(MensagemErro.CAMPO_OBRIGATORIO.MensagemComParametro("Id do módulo"));
        }

        if (nome == null || nome.isBlank()) {
            throw new ValidacaoException(MensagemErro.CAMPO_OBRIGATORIO.MensagemComParametro("Nome do módulo"));
        }

        if (constante == null || constante.isBlank()) {
            throw new ValidacaoException(MensagemErro.CAMPO_OBRIGATORIO.MensagemComParametro("Constante do módulo"));
        }

        if (prefixo == null || prefixo.isBlank()) {
            throw new ValidacaoException(MensagemErro.CAMPO_OBRIGATORIO.MensagemComParametro("Prefixo do módulo"));
        }

        this.id = id;
        this.nome = nome;
        this.constante = constante;
        this.prefixo = prefixo;
        this.descricao = descricao;
    }

    public Modulo(Long id, String nome, String prefixo) {

        if (id == null) {
            throw new ValidacaoException(MensagemErro.CAMPO_OBRIGATORIO.MensagemComParametro("Id do módulo"));
        }

        if (nome == null || nome.isBlank()) {
            throw new ValidacaoException(MensagemErro.CAMPO_OBRIGATORIO.MensagemComParametro("Nome do módulo"));
        }

        if (prefixo == null || prefixo.isBlank()) {
            throw new ValidacaoException(MensagemErro.CAMPO_OBRIGATORIO.MensagemComParametro("Prefixo do módulo"));
        }

        this.id = id;
        this.nome = nome;
        this.prefixo = prefixo;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getConstante() {
        return constante;
    }

    public String getPrefixo() {
        return prefixo;
    }

    public String getDescricao() {
        return descricao;
    }

    public List<Funcao> getFuncoes() {
        return funcoes;
    }

    public void adicionarFuncao(Funcao funcao) {
        if (funcao != null) {
            this.funcoes.add(funcao);
        }
    }

    public boolean pertenceAoModulo(String nomeArquivo) {
        return nomeArquivo != null && nomeArquivo.startsWith(prefixo);
    }

    @Override
    public String toString() {
        return nome;
    }
}