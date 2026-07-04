package com.supergestao.Flyway.migration.assistant.dominio.regra.sql.indentar;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class IndentarSqlApiPgFormatter {
    private static final String API_URL      = "https://sqlformat.darold.net/";
    private static final int    TIMEOUT_SEG  = 15;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(TIMEOUT_SEG))
            .build();

    /*Formata o SQL via API do pgFormatter. Crie esta validação, testei, mas neste momento não será usado     */
    public static String formatar(String sql) {
        if (sql == null || sql.isBlank()) {
            return sql;
        }

        try {
            // 1. Converte caracteres acentuados para escapes XXXX antes de enviar
            // Isso impede que o decodificador JSON em Perl do servidor quebre com "wide character"
            String sqlEscapado = escapeUnicode(sql);

            ObjectNode jsonParams = MAPPER.createObjectNode();
            jsonParams.put("content", sqlEscapado);
            jsonParams.put("spaces", 4);
            jsonParams.put("uc_keyword", 2);  // 2 = maiúsculo
            jsonParams.put("uc_function", 0); // 0 = inalterado (mantém funções como UPPER e substring)
            jsonParams.put("uc_type", 1);     // 1 = minúsculo
            jsonParams.put("keep_newline", 1); // 1 = preserva linhas em branco originais

            String corpoJson = MAPPER.writeValueAsString(jsonParams);

            HttpRequest requisicao = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .timeout(Duration.ofSeconds(TIMEOUT_SEG))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .POST(HttpRequest.BodyPublishers.ofString(corpoJson, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> resposta = CLIENT.send(
                    requisicao,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
            );

            if (resposta.statusCode() != 200) {
                System.out.println("Erro API " + resposta.statusCode() + ": " + resposta.body());
                return sql;
            }

            JsonNode json = MAPPER.readTree(resposta.body());

            if (json.has("formatted")) {
                String resultadoFormatado = json.get("formatted").asText();

                return unescapeUnicode(resultadoFormatado);
            }

            return sql;

        } catch (Exception e) {
            System.out.println("Erro na chamada à API do pgFormatter: " + e.getMessage());
            return sql;
        }
    }

    private static String escapeUnicode(String input) {
        if (input == null) return null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c > 127) {
                sb.append(String.format("\\u%04x", (int) c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static String unescapeUnicode(String input) {
        if (input == null) return null;
        StringBuilder sb = new StringBuilder();
        int i = 0;
        int len = input.length();
        while (i < len) {
            char c = input.charAt(i);
            if (c == '\\' && i + 5 < len && input.charAt(i + 1) == 'u') {
                try {
                    String hex = input.substring(i + 2, i + 6);
                    char unicode = (char) Integer.parseInt(hex, 16);
                    sb.append(unicode);
                    i += 6;
                    continue;
                } catch (NumberFormatException ignored) {}
            }
            sb.append(c);
            i++;
        }
        return sb.toString();
    }
}

//
//Com base no código-fonte oficial da interface Web e API do pgFormatter em
//
//CGI.pm
//, aqui estão todos os parâmetros possíveis de serem enviados no payload JSON e o comportamento de cada um:
//
//1. Parâmetros de Entrada e Conteúdo
//content (String): O código SQL que você deseja formatar.
//original_content (String, padrão: ""): O código SQL original não formatado (usado para comparação ou log no servidor).
//show_example (Inteiro/Booleano 0 ou 1, padrão: 0): Usado pelo site para exibir uma consulta de exemplo.
//2. Parâmetros de Indentação e Espaçamento
//spaces (Inteiro, padrão: 4): A quantidade de espaços usada para cada nível de indentação do código.
//keep_newline (Inteiro/Booleano 0 ou 1, padrão: 0): Preserva as linhas em branco vazias digitadas originalmente no código.
//0: Remove linhas em branco extras consecutivas.
//1: Mantém as linhas em branco que separam as seções de código.
//no_space_function (Inteiro/Booleano 0 ou 1, padrão: 0): Remove o espaço entre o nome da função e o parêntese (ex: grava_log(...) ao invés de grava_log (...)).
//3. Parâmetros de Caixa Alta/Baixa (Case)
//uc_keyword (Inteiro 0, 1, 2 ou 3, padrão: 2): Caixa para as palavras reservadas do SQL (como SELECT, FROM, WHERE):
//0: Mantém inalterado.
//1: Tudo em minúsculo (select).
//2: Tudo em maiúsculo (SELECT).
//3: Primeira letra em maiúsculo (Select).
//uc_function (Inteiro 0, 1, 2 ou 3, padrão: 0): Caixa para as funções internas do banco (como coalesce, substring, max):
//Mesmos valores numéricos (0 a 3) de uc_keyword.
//uc_type (Inteiro 0, 1, 2 ou 3, padrão: 1): Caixa para nomes de tipos de dados (como bigint, text, timestamp):
//Mesmos valores numéricos (0 a 3) de uc_keyword.
//4. Parâmetros de Vírgula e Listas
//comma (String end ou start, padrão: end): Define a posição das vírgulas em listas:
//end: Coloca a vírgula no final da linha (ex: tabela,).
//start: Coloca a vírgula no início da linha seguinte (ex: , tabela).
//comma_break (Inteiro/Booleano 0 ou 1, padrão: 0): Em declarações de INSERT, insere uma nova linha após cada vírgula na lista de valores.
//wrap_after (Inteiro, padrão: 0): Quantidade de colunas ou itens após o qual uma lista será quebrada em múltiplas linhas.
//5. Parâmetros de Estética e Limpeza
//nocomment (Inteiro/Booleano 0 ou 1, padrão: 0): Remove todos os comentários do código SQL (-- ou /* ... */).
//nogrouping (Inteiro/Booleano 0 ou 1, padrão: 0): Adiciona uma quebra de linha extra entre comandos agrupados dentro de transações (BEGIN ... COMMIT).
//redundant_parenthesis (Inteiro/Booleano 0 ou 1, padrão: 0): Tenta encontrar e remover parênteses desnecessários em expressões.
//anonymize (Inteiro/Booleano 0 ou 1, padrão: 0): Ofusca dados confidenciais (números e strings literais) transformando-os em dados genéricos antes da formatação.
//numbering (Inteiro/Booleano 0 ou 1, padrão: 0): Insere um comentário contendo o índice numérico sequencial antes de cada declaração SQL formatada.
//        6. Parâmetros de Dialeto e Conexão
//redshift (Inteiro/Booleano 0 ou 1, padrão: 0): Habilita formatação com suporte a palavras-chave exclusivas do Amazon Redshift.
//separator (String, padrão: ""): Define um caractere separador dinâmico de comandos SQL personalizado.
//format_type (Inteiro/Booleano 0 ou 1, padrão: 0): Habilita formatação automática estrita para o alinhamento de tipos.
//colorize (Inteiro/Booleano 0 ou 1, padrão: 1): Liga ou desliga a saída colorida para a interface Web.
//
