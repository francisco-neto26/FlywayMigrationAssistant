package com.supergestao.Flyway.migration.assistant.dominio.regra.sql.indentar;

import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemSistema;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Resultado;
import com.supergestao.Flyway.migration.assistant.exception.PersistenciaException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class IndentarSql {

    private static final Pattern PATTERN_PALAVRAS_ESPACO = Pattern.compile(
            "\\b(DECLARE|BEGIN|CURSOR)\\b",
            Pattern.CASE_INSENSITIVE
    );


    /* Formata o SQL usando o pgFormatter do projeto, extrai os recursos necessários para o diretório temporário do SO
      e os executa via interpretador Perl.*/
    public static Resultado formatar(String sql) {

        if (sql == null || sql.isBlank()) {
            return new Resultado(true, sql, null);
        }

        try {
            //busca local do perl
            String comandoPerl = obtemComandoPerl();

            //obtem pg_format e sua biblioteca do .jar para a pasta temp
            File arquivoPgFormat = obtemRecursosPgFormatter();
            File pastaLibTemp = new File(arquivoPgFormat.getParentFile(), "lib");

            //Monta o comando do processo local
            List<String> comando = new ArrayList<>();
            comando.add(comandoPerl);

            //Adiciona diretório lib temporário à busca de bibliotecas do Perl
            comando.add("-I");
            comando.add(pastaLibTemp.getAbsolutePath());

            comando.add(arquivoPgFormat.getAbsolutePath());
            //Define padrão indentação com 4 espaços por nível
            comando.add("--spaces");
            comando.add("4");

            //Define palavras-chave como maiúsculas
            comando.add("--keyword-case");
            comando.add("2");

            //Define nome de funções como foi informado em tela, não altera
            comando.add("--function-case");
            comando.add("2");

            //Define tipo de dados como minúsculo
            comando.add("--type-case");
            comando.add("1");

            //Define se preserva linhas em branco
            comando.add("--keep-newline");

            //Lê da entrada padrão (stdin)
            comando.add("-");

            ProcessBuilder pb = new ProcessBuilder(comando);
            pb.redirectErrorStream(true);
            Process processo = pb.start();

            //Grava o SQL original na entrada padrão (stdin) do pgFormatter
            try (BufferedWriter fluxoEscrita  = new BufferedWriter(
                    new OutputStreamWriter(processo.getOutputStream(), StandardCharsets.UTF_8))) {
                fluxoEscrita .write(sql);
                fluxoEscrita .flush();
            }

            //Lê a saída padrão (stdout) com o SQL formatado
            StringBuilder retorno = new StringBuilder();
            try (BufferedReader fluxoLeitura  = new BufferedReader(
                    new InputStreamReader(processo.getInputStream(), StandardCharsets.UTF_8))) {
                String linha;
                while ((linha = fluxoLeitura .readLine()) != null) {
                    retorno.append(linha).append("\n");
                }
            }

            int codigoSaida = processo.waitFor();
            if (codigoSaida == 0) {
                String sqlFormatado = ajustarEspacos(retorno.toString().trim());
                return new Resultado(true, sqlFormatado, null);
            } else {
                String msg = MensagemSistema.ERRO_RODAR_PGFORMATTER.MensagemComParametro(codigoSaida, retorno.toString());
                return new Resultado(false, sql, msg);
            }

        } catch (IOException e) {
            String msg = MensagemSistema.ERRO_IO_PGFORMATTER.MensagemComParametro(e.getMessage());
            return new Resultado(false, sql, msg);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            String msg = MensagemSistema.ERRO_INTERRUPCAO_PGFORMATTER.MensagemComParametro(e.getMessage());
            return new Resultado(false, sql, msg);
        } catch (Exception e) {
            String msg = MensagemSistema.ERRO_GERAL_PGFORMATTER.MensagemComParametro(e.getMessage());
            return new Resultado(false, sql, msg);
        }
    }

    private static String obtemComandoPerl() throws FileNotFoundException {

        //Testa se o comando "Perl" já está globalmente disponível no PATH
        if (testaComandoPerl("perl")) {
            return "perl";
        }

        //Se não estiver no PATH e for Windows, procura em instalações padrão do Git
        String sistemaOperacional = System.getProperty("os.name").toLowerCase();
        if (sistemaOperacional.contains("win")) {
            String[] caminhosComuns = {
                    "C:\\Program Files\\Git\\usr\\bin\\perl.exe",
                    "C:\\Program Files (x86)\\Git\\usr\\bin\\perl.exe",
                    System.getProperty("user.home") + "\\AppData\\Local\\Programs\\Git\\usr\\bin\\perl.exe"
            };

            for (String caminho : caminhosComuns) {
                File file = new File(caminho);
                if (file.exists() && file.isFile()) {
                    return file.getAbsolutePath();
                }
            }
        }
        throw new PersistenciaException(MensagemSistema.ERRO_PERL.getMensagem());
    }


    private static boolean testaComandoPerl(String comando) {
        try {
            Process processo = new ProcessBuilder(comando, "-e", "print 1").start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(processo.getInputStream()))) {
                String saida = reader.readLine();
                return "1".equals(saida);
            } finally {
                processo.destroy();
            }
        } catch (Exception e) {
            return false;
        }
    }

    private static File obtemRecursosPgFormatter() throws IOException {
        String diretorioTemporarioTexto = System.getProperty("java.io.tmpdir") + File.separator + "flyway_migration_assistant_pgformatter";
        File diretorioTemporario = new File(diretorioTemporarioTexto);
        if (!diretorioTemporario.exists()) {
            diretorioTemporario.mkdirs();
        }

        File dirLibPgFormatter = new File(diretorioTemporario, "lib" + File.separator + "pgFormatter");
        if (!dirLibPgFormatter.exists()) {
            dirLibPgFormatter.mkdirs();
        }

        //Extrai o script principal e as suas dependências .pm
        obterRecurso("/pgformatter/pg_format", new File(diretorioTemporario, "pg_format"));
        obterRecurso("/pgformatter/lib/pgFormatter/Beautify.pm", new File(dirLibPgFormatter, "Beautify.pm"));
        obterRecurso("/pgformatter/lib/pgFormatter/CGI.pm", new File(dirLibPgFormatter, "CGI.pm"));
        obterRecurso("/pgformatter/lib/pgFormatter/CLI.pm", new File(dirLibPgFormatter, "CLI.pm"));

        return new File(diretorioTemporario, "pg_format");
    }

    private static void obterRecurso(String caminhoRecurso, File arquivoDestino) throws IOException {
        try (InputStream inputStreamIndentar = IndentarSql.class.getResourceAsStream(caminhoRecurso)) {
            if (inputStreamIndentar == null) {
                throw new PersistenciaException(MensagemSistema.ERRO_LER_CLASS.MensagemComParametro(caminhoRecurso));
            }
            try (OutputStream sistemaOperacional = new FileOutputStream(arquivoDestino)) {
                byte[] buffer = new byte[8192];
                int lidos;
                while ((lidos = inputStreamIndentar.read(buffer)) != -1) {
                    sistemaOperacional.write(buffer, 0, lidos);
                }
            }
        }
    }

    private static String ajustarEspacos(String sql) {
        if (sql == null || sql.isBlank()) {
            return sql;
        }
        String[] linhas = sql.split("\\r?\\n");
        List<String> novasLinhas = new ArrayList<>();
        for (int i = 0; i < linhas.length; i++) {
            String linha = linhas[i];

            if (deveAdicionarEspacos(linha)) {                ;
                //Adiciona uma linha em branco ANTES (se a linha anterior não for branca e não for o início do script)
                if (!novasLinhas.isEmpty() && !novasLinhas.getLast().isBlank()) {
                    novasLinhas.add("");
                }
                novasLinhas.add(linha);
                //Adiciona uma linha em branco DEPOIS (se não for a última linha e a próxima não for branca)
                if (i < linhas.length - 1 && !linhas[i].contains("CURSOR")) {
                    String proximaLinha = linhas[i + 1].trim();
                    if (!proximaLinha.isEmpty() && !proximaLinha.contains("CURSOR")) {
                        novasLinhas.add("");
                    }
                }
            } else {
                novasLinhas.add(linha);
            }
        }
        return String.join("\n", novasLinhas);
    }

    private static boolean deveAdicionarEspacos(String linha) {
        String linhaTrimmed = linha.trim();

        // Ignora se for linha vazia ou se for comentário
        if (linhaTrimmed.isEmpty() ||
                linhaTrimmed.startsWith("--") ||
                linhaTrimmed.startsWith("/*") ||
                linhaTrimmed.startsWith("*")) {
            return false;
        }

        return PATTERN_PALAVRAS_ESPACO.matcher(linha).find();
    }


}
