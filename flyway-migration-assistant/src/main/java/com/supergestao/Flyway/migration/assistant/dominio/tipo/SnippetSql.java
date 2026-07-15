package com.supergestao.Flyway.migration.assistant.dominio.tipo;

public enum SnippetSql {
    // --- Operadores de Filtragem ---
    LIKE("LIKE", "LIKE '%|%'", "Operador de comparação textual com padrão (sensível a caso)."),
    ILIKE("ILIKE", "ILIKE '%|%'", "Operador de comparação de texto insensível a caso."),
    BETWEEN("BETWEEN", "BETWEEN | AND ", "Filtra registros dentro de um intervalo inclusivo."),
    IN("IN", "IN (|)", "Filtra registros que coincidem com itens de uma lista."),
    EXISTS("EXISTS", "EXISTS (SELECT 1 FROM |)", "Verifica se uma subconsulta retorna dados."),

    // --- Funções de Agregação ---
    COUNT("COUNT", "COUNT(|)", "Função que conta a quantidade de registros."),
    MAX("MAX", "MAX(|)", "Retorna o valor máximo do conjunto."),
    MIN("MIN", "MIN(|)", "Retorna o valor mínimo do conjunto."),
    SUM("SUM", "SUM(|)", "Calcula a soma total de uma coluna numérica."),
    AVG("AVG", "AVG(|)", "Calcula a média aritmética dos valores."),

    // --- Funções de Manipulação de String ---
    SUBSTRING("SUBSTRING", "SUBSTRING(| FROM  FOR )", "Extrai um pedaço de texto de acordo com posições."),
    COALESCE("COALESCE", "COALESCE(|, )", "Retorna o primeiro argumento não nulo informado."),
    REPLACE("REPLACE", "REPLACE(|, '', '')", "Substitui uma substring por outra dentro de um texto."),
    UPPER("UPPER", "UPPER(|)", "Converte o texto todo para maiúsculas."),
    LOWER("LOWER", "LOWER(|)", "Converte o texto todo para minúsculas."),
    CONCAT("CONCAT", "CONCAT(|, )", "Agrupa e concatena múltiplos textos."),
    POSITION("POSITION", "POSITION(| IN )", "Retorna a posição de uma substring dentro do texto."),
    NULLIF("NULLIF", "NULLIF(|, )", "Retorna nulo se os dois parâmetros forem iguais."),

    // --- Conversão e Datas ---
    TO_CHAR("TO_CHAR", "TO_CHAR(|, 'YYYY-MM-DD HH24:MI:SS')", "Converte e formata um valor para texto."),
    TO_DATE("TO_DATE", "TO_DATE(|, 'YYYY-MM-DD')", "Converte texto para formato de data."),
    TO_NUMBER("TO_NUMBER", "TO_NUMBER(|, '999G999D99')", "Converte texto para formato numérico."),

    // --- Controle de Fluxo PL/pgSQL ---
    IF("IF", "IF(| )THEN\n    \nEND IF;", "Estrutura condicional do PL/pgSQL."),
    ELSIF("ELSIF", "ELSIF(| ) THEN", "Estrutura condicional alternativa (ELSE IF) do PL/pgSQL."),
    ELSE("ELSE", "ELSE\n    |", "Bloco condicional de fallback padrão do PL/pgSQL."),
    EXCEPTION("EXCEPTION", "EXCEPTION\n    WHEN OTHERS THEN\n        |", "Bloco de captura de todos os erros (OTHERS) no PL/pgSQL."),
    OPEN("OPEN", "OPEN |;\nLOOP\n    FETCH  INTO ;\n    EXIT WHEN NOT FOUND;\n    \nEND LOOP;", "Abre o cursor e monta toda a estrutura do laço LOOP e FETCH do PL/pgSQL."),
    LOOP("LOOP", "LOOP\n    |\nEND LOOP;", "Estrutura de repetição do PL/pgSQL.");

    private final String palavra;
    private final String template;
    private final String descricao;

    SnippetSql(String palavra, String template, String descricao) {
        this.palavra = palavra;
        this.template = template;
        this.descricao = descricao;
    }

    public String getPalavra() { return palavra; }
    public String getTemplate() { return template; }
    public String getDescricao() { return descricao; }

    // Localiza dinamicamente se a palavra-chave possui um snippet registrado no Enum
    public static SnippetSql buscarPorPalavra(String palavra) {
        for (SnippetSql snippet : values()) {
            if (snippet.getPalavra().equalsIgnoreCase(palavra)) {
                return snippet;
            }
        }
        return null;
    }
}

