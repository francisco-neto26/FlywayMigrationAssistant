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
    private JTextPane visualizadorSql;
    private final ArquivoService arquivoService;
    private StyledDocument documento;
    private java.util.function.Consumer<String> saveListener;

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
        visualizadorSql = new JTextPane();
        visualizadorSql.setEditable(false);
        visualizadorSql.setFont(new Font("Consolas", Font.PLAIN, 13));
        documento = visualizadorSql.getStyledDocument();
        defineEstilos();
        add(new JScrollPane(visualizadorSql), BorderLayout.CENTER);
    }

    private void defineEstilos() {
        Style estiloPadrao = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        StyleConstants.setForeground(documento.addStyle("palavraChave", estiloPadrao), Cores.AZUL_ESCURO.getCor());
        StyleConstants.setBold(documento.getStyle("palavraChave"), true);
        StyleConstants.setForeground(documento.addStyle("conteudo", estiloPadrao), Cores.VERDE.getCor());
        StyleConstants.setItalic(documento.getStyle("conteudo"), true);
        StyleConstants.setForeground(documento.addStyle("texto", estiloPadrao), Cores.VERMELHO_STRING.getCor());
        StyleConstants.setForeground(documento.addStyle("numero", estiloPadrao), Cores.LARANJA.getCor());
        StyleConstants.setForeground(documento.addStyle("funcao", estiloPadrao), Cores.ROXO.getCor());
        StyleConstants.setBold(documento.getStyle("funcao"), true);
        documento.addStyle("normal", estiloPadrao);
    }

    public void setConteudo(String sql, String titulo, boolean editavel, boolean mostrarSalvar) {
        setBorder(BorderFactory.createTitledBorder("Visualizador SQL - " + titulo));
        visualizadorSql.setText(sql);
        destacarSql(sql);
        visualizadorSql.setEditable(editavel);
        visualizadorSql.setCaretPosition(0);
    }

    public void setEditable(boolean b) {
        visualizadorSql.setEditable(b);
        if (b) visualizadorSql.requestFocus();
    }

    public void displayArquivo(Arquivo arquivo) {
        try {
            String conteudo = arquivoService.lerConteudoArquivo(arquivo.getarquivo());
            setConteudo(conteudo, arquivo.getnome(), false, false);
        } catch (IOException e) {
            limparDefinirTexto("Erro ao ler arquivo:\n\n" + e.getMessage(), "normal");
        }
    }

    public void setOnSave(java.util.function.Consumer<String> listener) {
        this.saveListener = listener;
    }

    public String getTexto() {
        return visualizadorSql.getText();
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
            Matcher sm = textoPadrao.matcher(linha.substring(i));
            if (sm.lookingAt()) {
                documento.insertString(posicao, sm.group(), documento.getStyle("texto"));
                posicao += sm.group().length();
                i += sm.group().length();
                continue;
            }
            Matcher nm = numeroPadrao.matcher(linha.substring(i));
            if (nm.lookingAt()) {
                documento.insertString(posicao, nm.group(), documento.getStyle("numero"));
                posicao += nm.group().length();
                i += nm.group().length();
                continue;
            }
            Matcher pm = palavraPadrao.matcher(linha.substring(i));
            if (pm.lookingAt()) {
                String pal = pm.group();
                if (SqlPalavras.contains(pal.toUpperCase()))
                    documento.insertString(posicao, pal, documento.getStyle("palavraChave"));
                else if (eFuncao(linha, i + pal.length()))
                    documento.insertString(posicao, pal, documento.getStyle("funcao"));
                else documento.insertString(posicao, pal, documento.getStyle("normal"));
                posicao += pal.length();
                i += pal.length();
                continue;
            }
            documento.insertString(posicao, String.valueOf(linha.charAt(i)), documento.getStyle("normal"));
            posicao++;
            i++;
        }
        return posicao;
    }

    private boolean eFuncao(String linha, int posicao) {
        while (posicao < linha.length() && Character.isWhitespace(linha.charAt(posicao))) posicao++;
        return posicao < linha.length() && linha.charAt(posicao) == '(';
    }

    private void limparDefinirTexto(String t, String s) {
        try {
            documento.remove(0, documento.getLength());
            documento.insertString(0, t, documento.getStyle(s));
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void limpar(String t) {
        setConteudo("", t, false, false);
    }
}