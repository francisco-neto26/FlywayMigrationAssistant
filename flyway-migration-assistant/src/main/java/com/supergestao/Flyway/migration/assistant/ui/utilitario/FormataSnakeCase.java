package com.supergestao.Flyway.migration.assistant.ui.utilitario;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class FormataSnakeCase {

    private static final Pattern ACENTOS = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    /**
     * Converte qualquer texto (com acentos, espaços, hífens ou CamelCase)
     * no padrão estrito snake_case (letras minúsculas separadas por _).
     *
     * Exemplos de retorno:
     * - "Criar Tabela Usuário"  -> "criar_tabela_usuario"
     * - "alterTable"           -> "alter_table"
     * - "Módulo - Vendas!"     -> "modulo_vendas"
     * - "  Função Teste   "    -> "funcao_teste"
     */
    public static String formatarSnakeCase(String texto) {
        if (texto == null || texto.isBlank()) {
            return "";
        }
        // 1. Remove acentos (ex: "Usuário" -> "Usuario", "Função" -> "Funcao")
        String semAcentos = Normalizer.normalize(texto, Normalizer.Form.NFD);
        semAcentos = ACENTOS.matcher(semAcentos).replaceAll("");

        // 2. Insere underline entre palavras em CamelCase (ex: "alterTable" -> "alter_Table")
        String semDuplicados = getSemDuplicados(semAcentos);
        // 7. Remove underlines que ficaram no início ou no fim do texto
        return semDuplicados.replaceAll("^_+|_+$", "");
    }

    private static @NonNull String getSemDuplicados(String semAcentos) {
        String comUnderlineCamel = semAcentos.replaceAll("([a-z])([A-Z])", "$1_$2");

        // 3. Converte todas as letras para minúsculas
        String minusculo = comUnderlineCamel.toLowerCase();

        // 4. Substitui espaços, hífens e traços por um único underline
        String comUnderline = minusculo.replaceAll("[\\s\\-–—]+", "_");

        // 5. Remove todos os caracteres especiais (mantém apenas letras a-z, números 0-9 e _)
        String limpo = comUnderline.replaceAll("[^a-z0-9_]", "");

        // 6. Reduz underlines duplicados consecutivos (ex: "__" -> "_")
        return limpo.replaceAll("_{2,}", "_");
    }
}

