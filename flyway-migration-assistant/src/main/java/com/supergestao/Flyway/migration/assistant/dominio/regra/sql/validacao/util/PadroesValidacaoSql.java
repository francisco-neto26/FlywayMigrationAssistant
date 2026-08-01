package com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.util;

import java.util.regex.Pattern;

public final class PadroesValidacaoSql {

    private PadroesValidacaoSql() {}

    // =========================================================================
    // PADRÕES DE EXTRAÇÃO DE BLOCOS SQL
    // =========================================================================

    // para function ou procedure
    public static final Pattern FUNCTION = Pattern.compile(
            "(?i)\\bCREATE\\s+(?:OR\\s+REPLACE\\s+)?(?:FUNCTION|PROCEDURE)\\b",
            Pattern.CASE_INSENSITIVE
    );

    // para trigger
    public static final Pattern TRIGGER = Pattern.compile(
            "(?i)\\bCREATE\\s+(?:OR\\s+REPLACE\\s+)?TRIGGER\\s+([\\s\\S]*?);",
            Pattern.CASE_INSENSITIVE
    );

    // para view e view materializada
    public static final Pattern VIEW = Pattern.compile(
            "(?i)\\bCREATE\\s+(?:OR\\s+REPLACE\\s+)?(?:MATERIALIZED\\s+)?VIEW\\s+([\\s\\S]*?);",
            Pattern.CASE_INSENSITIVE
    );

    // para bloco anonimo
    public static final Pattern BLOCO_ANONIMO = Pattern.compile(
            "(?i)\\bDO\\s+\\$(?<tag>[A-Za-z0-9_]*)\\$([\\s\\S]*?)\\$\\k<tag>\\$\\s*;",
            Pattern.CASE_INSENSITIVE
    );

    // =========================================================================
    // PADRÕES DE CRIAÇÃO E ALTERAÇÃO DE ESTRUTURAS DDL
    // =========================================================================

    // para criar tabela
    public static final Pattern TABLE = Pattern.compile(
            "(?i)\\bCREATE\\s+(?:UNLOGGED\\s+|(?:TEMP|TEMPORARY)\\s+)?TABLE\\s+(?:IF\\s+NOT\\s+EXISTS\\s+)?([\\s\\S]*?);",
            Pattern.CASE_INSENSITIVE
    );

    // para criar índice
    public static final Pattern INDEX = Pattern.compile(
            "(?i)\\bCREATE\\s+(?:UNIQUE\\s+)?INDEX\\s+(?:CONCURRENTLY\\s+)?(?:IF\\s+NOT\\s+EXISTS\\s+)?([\\s\\S]*?);",
            Pattern.CASE_INSENSITIVE
    );

    // para criar sequence
    public static final Pattern SEQUENCE = Pattern.compile(
            "(?i)\\bCREATE\\s+(?:UNLOGGED\\s+|(?:TEMP|TEMPORARY)\\s+)?SEQUENCE\\s+(?:IF\\s+NOT\\s+EXISTS\\s+)?([\\s\\S]*?);",
            Pattern.CASE_INSENSITIVE
    );

    // para criar schema
    public static final Pattern SCHEMA = Pattern.compile(
            "(?i)\\bCREATE\\s+SCHEMA\\s+(?:IF\\s+NOT\\s+EXISTS\\s+)?([\\s\\S]*?);",
            Pattern.CASE_INSENSITIVE
    );

    // para criar tipo
    public static final Pattern TYPE = Pattern.compile(
            "(?i)\\bCREATE\\s+(?:TYPE|DOMAIN)\\s+([\\s\\S]*?);",
            Pattern.CASE_INSENSITIVE
    );

    // para criar extension
    public static final Pattern EXTENSION = Pattern.compile(
            "(?i)\\bCREATE\\s+EXTENSION\\s+(?:IF\\s+NOT\\s+EXISTS\\s+)?([\\s\\S]*?);",
            Pattern.CASE_INSENSITIVE
    );

    // para alterar tabela
    public static final Pattern ALTER_TABLE = Pattern.compile(
            "(?i)\\bALTER\\s+TABLE\\s+(?:IF\\s+EXISTS\\s+)?([\\s\\S]*?);",
            Pattern.CASE_INSENSITIVE
    );


    // =========================================================================
    // ESTRUTURAS DE CONTROLE E REGRAS PL/pgSQL
    // =========================================================================

    // para blocos procedurais ($BODY$, $func$, $$)
    public static final Pattern BLOCO_PROCEDURAL = Pattern.compile(
            "(\\$(\\w*)\\$)(.*?)\\1",
            Pattern.DOTALL | Pattern.CASE_INSENSITIVE
    );

    public static final Pattern BLOCO_DECLARE = Pattern.compile(
            "\\bDECLARE\\b(.*?)\\bBEGIN\\b",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    public static final Pattern DECLARACAO_CURSOR = Pattern.compile(
            "(?m)^[ \\t]*(\\w+)\\s*(?:\\([^)]*\\))?\\s*CURSOR\\s*(?:\\([^)]*\\))?\\s*(?:FOR|IS)\\s+([\\s\\S]*?);",
            Pattern.CASE_INSENSITIVE
    );

    /** Detecta palavras-chave de abertura e fechamento de blocos de controle. */
    public static final Pattern ESTRUTURAS_CONTROLE = Pattern.compile(
            "\\b(END\\s+IF|END\\s+LOOP|END\\s+CASE|END|BEGIN|IF|ELSIF|THEN|LOOP|CASE)\\b",
            Pattern.CASE_INSENSITIVE
    );

    /**
     * Detecta cabeçalhos PL/pgSQL no início de uma instrução
     * (IF...THEN, LOOP, BEGIN, etc.) para isolá-los do SQL puro.
     */
    public static final Pattern CABECALHO_PLPGSQL = Pattern.compile(
            "^\\s*(?:BEGIN|DECLARE|ELSE|EXCEPTION|" +
                    "IF\\b.*?\\bTHEN|ELSIF\\b.*?\\bTHEN|WHEN\\b.*?\\bTHEN|" +
                    "FOR\\b.*?\\bLOOP|WHILE\\b.*?\\bLOOP|FOREACH\\b.*?\\bLOOP|" +
                    "END\\s+IF|END\\s+LOOP|END\\s+CASE|END)\\b\\s*",
            Pattern.CASE_INSENSITIVE | Pattern.DOTALL
    );

    /** Detecta labels de bloco (<<nome_label>>). */
    public static final Pattern LABEL_BLOCO = Pattern.compile(
            "<<(\\w+)>>",
            Pattern.CASE_INSENSITIVE
    );


    // =========================================================================
    // VARIÁVEIS E IDENTIFICADORES
    // =========================================================================

    /** Detecta variáveis especiais de trigger (TG_OP, TG_TABLE_NAME, etc.). */
    public static final Pattern VARIAVEL_TRIGGER = Pattern.compile(
            "\\b(TG_\\w+)\\b",
            Pattern.CASE_INSENSITIVE
    );

    /** Detecta o início de uma declaração de variável na seção DECLARE. */
    public static final Pattern INICIO_DECLARACAO_VARIAVEL = Pattern.compile(
            "^[a-zA-Z_][a-zA-Z0-9_]*\\s+\\S"
    );

    /** Detecta chamadas de função: nome seguido de parêntese de abertura. */
    public static final Pattern CHAMADA_FUNCAO = Pattern.compile(
            "\\b(\\w+)\\s*\\(",
            Pattern.CASE_INSENSITIVE
    );
}
