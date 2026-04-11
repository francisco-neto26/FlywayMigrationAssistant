package com.supergestao.Flyway.migration.assistant.dominio.modelo;

import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemErro;
import com.supergestao.Flyway.migration.assistant.exception.ValidacaoException;

import java.util.ArrayList;
import java.util.List;

public class Funcao {

    private final String nome;
    private final List<Arquivo> arquivos = new ArrayList<>();

    public Funcao(String nome) {

        if (nome == null || nome.isBlank()) {
            throw new ValidacaoException(MensagemErro.CAMPO_OBRIGATORIO.MensagemComParametro("Nome do Função"));
        }
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public List<Arquivo> getArquivos() {
        return arquivos;
    }

    public void adicionarArquivo(Arquivo arquivo) {
        if (arquivo != null) {
            this.arquivos.add(arquivo);
        }
    }

    public boolean pertenceAFuncao(String nomeArquivo) {
        return nomeArquivo != null && nomeArquivo.startsWith(nome);
    }

    @Override
    public String toString() {
        return nome;
    }

}
