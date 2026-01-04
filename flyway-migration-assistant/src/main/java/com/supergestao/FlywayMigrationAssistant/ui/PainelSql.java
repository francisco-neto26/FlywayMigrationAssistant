package com.supergestao.FlywayMigrationAssistant.ui;

import com.supergestao.FlywayMigrationAssistant.model.Arquivo;
import com.supergestao.FlywayMigrationAssistant.model.Cores;
import com.supergestao.FlywayMigrationAssistant.service.ArquivoService;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PainelSql extends JPanel {
    private JTextPane vizualizadorSql;
    private ArquivoService arquivoService;
    private StyledDocument documento;

    private static final Set<String> SqlPalavras = new HashSet<>(Arrays.asList(
            "SELECT", "FROM", "WHERE", "INSERT", "UPDATE", "DELETE", "CREATE", "DROP",
            "ALTER", "TABLE", "INDEX", "VIEW", "FUNCTION", "PROCEDURE", "TRIGGER",
            "IF", "EXISTS", "NOT", "NULL", "PRIMARY", "KEY", "FOREIGN", "REFERENCES",
            "UNIQUE", "DEFAULT", "CHECK", "CONSTRAINT", "ON", "AS", "IN", "AND", "OR",
            "JOIN", "LEFT", "RIGHT", "INNER", "OUTER", "UNION", "GROUP", "BY", "HAVING",
            "ORDER", "ASC", "DESC", "LIMIT", "OFFSET", "DISTINCT", "COUNT", "SUM",
            "AVG", "MAX", "MIN", "CASE", "WHEN", "THEN", "ELSE", "END", "BEGIN",
            "COMMIT", "ROLLBACK", "TRANSACTION", "GRANT", "REVOKE", "REPLACE",
            "INTO", "VALUES", "SET", "RETURNS", "RETURN", "LANGUAGE", "COMMENT",
            "COLUMN", "SERIAL", "BIGSERIAL", "VARCHAR", "INTEGER", "BIGINT", "BOOLEAN",
            "TIMESTAMP", "DATE", "TIME", "TEXT", "NUMERIC", "DECIMAL", "CURRENT_TIMESTAMP",
            "TRUE", "FALSE", "IS", "ILIKE", "LIKE", "BETWEEN", "CASCADE", "RESTRICT"
    ));

    public PainelSql() {
        this.arquivoService = new ArquivoService();
        inicializaPainelSql();
    }

    private void inicializaPainelSql() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Visualizador SQL"));

        vizualizadorSql = new JTextPane();
        vizualizadorSql.setEditable(false);
        vizualizadorSql.setFont(new Font("Consolas", Font.PLAIN, 13));
        documento = vizualizadorSql.getStyledDocument();

        defineEstilos();

        JScrollPane scrollPainel = new JScrollPane(vizualizadorSql);
        scrollPainel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPainel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        add(scrollPainel, BorderLayout.CENTER);
    }

    private void defineEstilos() {
        Style estiloPadrao = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);

        Style palavraChave = documento.addStyle("palavraChave", estiloPadrao);
        StyleConstants.setForeground(palavraChave, Cores.AZUL_ESCURO.getCor());
        StyleConstants.setBold(palavraChave, true);

        Style conteudo = documento.addStyle("conteudo", estiloPadrao);
        StyleConstants.setForeground(conteudo, Cores.VERDE.getCor());
        StyleConstants.setItalic(conteudo, true);

        Style texto = documento.addStyle("texto", estiloPadrao);
        StyleConstants.setForeground(texto, Cores.VERMELHO_STRING.getCor());

        Style numero = documento.addStyle("numero", estiloPadrao);
        StyleConstants.setForeground(numero, Cores.LARANJA.getCor());

        Style funcao = documento.addStyle("funcao", estiloPadrao);
        StyleConstants.setForeground(funcao, Cores.ROXO.getCor());
        StyleConstants.setBold(funcao, true);

        Style normal = documento.addStyle("normal", estiloPadrao);
        StyleConstants.setForeground(normal, Cores.PRETO.getCor());
    }

    public void displayArquivo(Arquivo arquivo) {
        try {
            String conteudo = arquivoService.lerConteudoArquivo(arquivo.getarquivo());
            destacarSql(conteudo);
            vizualizadorSql.setCaretPosition(0);
        } catch (IOException e) {
            limparDefinirTexto("Erro ao ler arquivo:\n\n" + e.getMessage(), "normal");
        }
    }

    private void destacarSql(String sql) {
        try {
            documento.remove(0, documento.getLength());

            int posicao = 0;
            String[] linhas = sql.split("\n", -1);

            for (int i = 0; i < linhas.length; i++) {
                String linha = linhas[i];

                if (linha.trim().startsWith("--")) {
                    documento.insertString(posicao, linha, documento.getStyle("conteudo"));
                    posicao += linha.length();
                } else {
                    posicao = destacarLinha(linha, posicao);
                }

                if (i < linhas.length - 1) {
                    documento.insertString(posicao, "\n", documento.getStyle("normal"));
                    posicao += 1;
                }
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private int destacarLinha(String linha, int posicaoInicial) throws BadLocationException {
        int posicao = posicaoInicial;

        Pattern textoPadrao = Pattern.compile("'[^']*'");
        Pattern numeroPadrao = Pattern.compile("\\b\\d+\\b");
        Pattern palavraPadrao = Pattern.compile("\\b\\w+\\b");

        int i = 0;
        while (i < linha.length()) {
            boolean matched = false;

            Matcher stringMatcher = textoPadrao.matcher(linha.substring(i));
            if (stringMatcher.lookingAt()) {
                String match = stringMatcher.group();
                documento.insertString(posicao, match, documento.getStyle("texto"));
                posicao += match.length();
                i += match.length();
                matched = true;
                continue;
            }

            Matcher numberMatcher = numeroPadrao.matcher(linha.substring(i));
            if (numberMatcher.lookingAt()) {
                String match = numberMatcher.group();
                documento.insertString(posicao, match, documento.getStyle("numero"));
                posicao += match.length();
                i += match.length();
                matched = true;
                continue;
            }

            Matcher palavraCombinada = palavraPadrao.matcher(linha.substring(i));
            if (palavraCombinada.lookingAt()) {
                String palavra = palavraCombinada.group();
                String upperWord = palavra.toUpperCase();

                if (SqlPalavras.contains(upperWord)) {
                    documento.insertString(posicao, palavra, documento.getStyle("palavraChave"));
                } else if (eFuncao(linha, i + palavra.length())) {
                    documento.insertString(posicao, palavra, documento.getStyle("funcao"));
                } else {
                    documento.insertString(posicao, palavra, documento.getStyle("normal"));
                }

                posicao += palavra.length();
                i += palavra.length();
                matched = true;
                continue;
            }

            if (!matched) {
                documento.insertString(posicao, String.valueOf(linha.charAt(i)), documento.getStyle("normal"));
                posicao++;
                i++;
            }
        }

        return posicao;
    }

    private boolean eFuncao(String linha, int posicao) {
        while (posicao < linha.length() && Character.isWhitespace(linha.charAt(posicao))) {
            posicao++;
        }
        return posicao < linha.length() && linha.charAt(posicao) == '(';
    }

    private void limparDefinirTexto(String texto, String estiloNome) {
        try {
            documento.remove(0, documento.getLength());
            documento.insertString(0, texto, documento.getStyle(estiloNome));
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void limpar() {
        limparDefinirTexto("", "normal");
    }
}