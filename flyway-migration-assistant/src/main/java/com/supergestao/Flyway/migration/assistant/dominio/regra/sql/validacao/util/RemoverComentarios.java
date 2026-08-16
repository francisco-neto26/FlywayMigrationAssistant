package com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.util;

public final class RemoverComentarios {

    //remove comentários de linha (--), comentários de bloco /* */ e literais de string ('...')
    public static String removerComentarios(String sql, boolean removerStringLiteral) {

        StringBuilder sqlBuilder = new StringBuilder(sql);
        int total = sqlBuilder.length();

        boolean comentarioLinha = false; //ativado após --
        boolean comentarioBloco = false; //ativado entre /*  */
        boolean comentarioStringLiteral = false; //ativado entre aspas simples ''

        for (int posicao = 0; posicao < total; posicao++) {
            char caracterAtual = sqlBuilder.charAt(posicao);

            //comentário de linha desconsidera tudo até o \n
            if (comentarioLinha) {
                if (caracterAtual == '\n') {
                    //fim da linha, mantém a quebra
                    comentarioLinha = false;
                } else if (caracterAtual != '\r') {
                    // apaga conteúdo do comentário
                    sqlBuilder.setCharAt(posicao, ' ');
                }
                continue;
            }

            // comentário de bloco desconsidera tudo até o */
            if (comentarioBloco) {
                if (caracterAtual == '*' && posicao + 1 < total && sqlBuilder.charAt(posicao + 1) == '/') {
                    sqlBuilder.setCharAt(posicao, ' ');
                    // apaga o */
                    sqlBuilder.setCharAt(posicao + 1, ' ');
                    //fim do bloco pula o '/'
                    comentarioBloco = false;
                    posicao++;
                } else if (caracterAtual != '\n' && caracterAtual != '\r') {
                    // apaga conteúdo interno do bloco
                    sqlBuilder.setCharAt(posicao, ' ');
                }
                continue;
            }

            // comentário string literal desconsidera até o fechamento da aspas
            if (comentarioStringLiteral && removerStringLiteral) {
                if (caracterAtual == '\'') {
                    if (posicao + 1 < total && sqlBuilder.charAt(posicao + 1) == '\'') {
                        sqlBuilder.setCharAt(posicao, ' ');
                        //apaga os dois e permanece na string
                        sqlBuilder.setCharAt(posicao + 1, ' ');
                        posicao++;
                    } else {
                        //aspas de fechamento, sai da string
                        comentarioStringLiteral = false;
                    }
                } else if (caracterAtual != '\n' && caracterAtual != '\r') {
                    //apaga conteúdo da string
                    sqlBuilder.setCharAt(posicao, ' ');
                }
                continue;
            }

            //SQL normal detecta comentario linha
            if (caracterAtual == '-' && posicao + 1 < total && sqlBuilder.charAt(posicao + 1) == '-') {
                // remove o --
                sqlBuilder.setCharAt(posicao, ' ');
                sqlBuilder.setCharAt(posicao + 1, ' ');
                comentarioLinha = true;
                posicao++;
                continue;
            }

            //SQL normal detecta comentario bloco
            if (caracterAtual == '/' && posicao + 1 < total && sqlBuilder.charAt(posicao + 1) == '*') {
                sqlBuilder.setCharAt(posicao, ' ');
                sqlBuilder.setCharAt(posicao + 1, ' ');  // apaga o /*
                comentarioBloco = true;
                posicao++;
                continue;
            }

            //SQL normal string literal, aspas simples
            if (caracterAtual == '\'') {
                comentarioStringLiteral = true;
            }
        }
        return sqlBuilder.toString();
    }

}
