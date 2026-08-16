package com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// responsável por capturar os erros de validação
public class CapturarErro {

    private final List<String> erros = new ArrayList<>();

    public void registrarErro(int linha, String mensagem) {
        String erroFormatado = String.format("Linha %d: %s", linha, mensagem);
        erros.add(erroFormatado);
    }

    public void registrarErro(String mensagem) {
        erros.add(mensagem);
    }

    public boolean temErros() {
        return !erros.isEmpty();
    }

    // retorna uma lista imutável
    public List<String> getErros() {
        return Collections.unmodifiableList(erros);
    }

    // retorna uma string formatada
    public String getMensagemFormatada() {
        return String.join("\n", erros);
    }

    public void limpar() {
        erros.clear();
    }
}

