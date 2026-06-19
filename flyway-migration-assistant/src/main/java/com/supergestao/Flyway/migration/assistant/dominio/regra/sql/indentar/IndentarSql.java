package com.supergestao.Flyway.migration.assistant.dominio.regra.sql.indentar;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class IndentarSql {

    /**
     * Formata o SQL localmente usando o pgFormatter embarcado no projeto.
     * Extrai os recursos necessários para o diretório temporário do SO
     * e os executa via interpretador Perl.
     *
     * @param sql SQL bruto a formatar
     * @return SQL formatado, ou o original inalterado em caso de falha/ausência de Perl
     */
    public static String formatar(String sql) {
        if (sql == null || sql.isBlank()) {
            return sql;
        }

        try {
            // 1. Tenta resolver qual comando Perl utilizar (local ou no PATH)
            String comandoPerl = resolverComandoPerl();

            // 2. Extrai o script pg_format e sua biblioteca do .jar para a pasta temp
            File scriptPgFormat = extrairRecursosPgFormatter();
            File pastaLibTemp = new File(scriptPgFormat.getParentFile(), "lib");

            // 3. Monta o comando do processo local
            List<String> comando = new ArrayList<>();
            comando.add(comandoPerl);

            // Adiciona explicitamente o diretório lib temporário à busca de bibliotecas do Perl
            comando.add("-I");
            comando.add(pastaLibTemp.getAbsolutePath());

            comando.add(scriptPgFormat.getAbsolutePath());
            comando.add("--spaces"); comando.add("4");
            comando.add("--keyword-case"); comando.add("2");  // MAIÚSCULO
            comando.add("--function-case"); comando.add("0"); // inalterado (mantém UPPER/substring)
            comando.add("--type-case"); comando.add("1");     // minúsculo
            comando.add("--keep-newline");                    // Preserva linhas em branco
            comando.add("-");                                 // Lê da entrada padrão (stdin)

            ProcessBuilder pb = new ProcessBuilder(comando);
            pb.redirectErrorStream(true);
            Process processo = pb.start();

            // 4. Grava o SQL original na entrada padrão (stdin) do pgFormatter
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(processo.getOutputStream(), StandardCharsets.UTF_8))) {
                writer.write(sql);
                writer.flush();
            }

            // 5. Lê a saída padrão (stdout) com o SQL formatado
            StringBuilder resultado = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(processo.getInputStream(), StandardCharsets.UTF_8))) {
                String linha;
                while ((linha = reader.readLine()) != null) {
                    resultado.append(linha).append("\n");
                }
            }

            int exitCode = processo.waitFor();
            if (exitCode == 0) {
                return resultado.toString().trim();
            } else {
                System.err.println("Erro ao rodar pgFormatter local (exit code " + exitCode + "): " + resultado);
            }

        } catch (IOException e) {
            System.err.println("Erro de E/S ao extrair ou rodar o pgFormatter (certifique-se de ter o Perl ou Git instalado): " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Processo do pgFormatter foi interrompido: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Falha geral ao formatar SQL localmente (certifique-se de que o Perl está no PATH): " + e.getMessage());
        }

        return sql; // Fallback caso ocorra qualquer erro
    }

    /**
     * Tenta resolver o comando do Perl, buscando no PATH ou em locais comuns do Windows (como Git).
     */
    private static String resolverComandoPerl() throws FileNotFoundException {
        // 1. Testa se o comando "perl" já está globalmente disponível no PATH
        if (testarComandoPerl("perl")) {
            return "perl";
        }

        // 2. Se não estiver no PATH e for Windows, procura em instalações padrão do Git
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
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

        // Se falhou em tudo, lança exceção com instrução
        throw new FileNotFoundException("Interpretador Perl não encontrado no PATH nem nos caminhos do Git. " +
                "Instale o Strawberry Perl (https://strawberryperl.com/) ou adicione o Perl do Git às variáveis de ambiente.");
    }

    /**
     * Testa se um determinado comando Perl responde com sucesso.
     */
    private static boolean testarComandoPerl(String comando) {
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

    /**
     * Extrai os arquivos do pgFormatter contidos na pasta resources para um diretório temporário do sistema.
     */
    private static File extrairRecursosPgFormatter() throws IOException {
        String dirTempStr = System.getProperty("java.io.tmpdir") + File.separator + "flyway_migration_assistant_pgformatter";
        File dirTemp = new File(dirTempStr);
        if (!dirTemp.exists()) {
            dirTemp.mkdirs();
        }

        File dirLibPgFormatter = new File(dirTemp, "lib" + File.separator + "pgFormatter");
        if (!dirLibPgFormatter.exists()) {
            dirLibPgFormatter.mkdirs();
        }

        // Extrai o script principal e suas dependências .pm
        copiarRecurso("/pgformatter/pg_format", new File(dirTemp, "pg_format"));
        copiarRecurso("/pgformatter/lib/pgFormatter/Beautify.pm", new File(dirLibPgFormatter, "Beautify.pm"));
        copiarRecurso("/pgformatter/lib/pgFormatter/CGI.pm", new File(dirLibPgFormatter, "CGI.pm"));
        copiarRecurso("/pgformatter/lib/pgFormatter/CLI.pm", new File(dirLibPgFormatter, "CLI.pm"));

        return new File(dirTemp, "pg_format");
    }

    /**
     * Copia um recurso de dentro do Classpath (.jar) para um arquivo real no disco temporário.
     */
    private static void copiarRecurso(String caminhoRecurso, File arquivoDestino) throws IOException {
        try (InputStream is = IndentarSql.class.getResourceAsStream(caminhoRecurso)) {
            if (is == null) {
                throw new FileNotFoundException("Recurso não encontrado no classpath: " + caminhoRecurso);
            }
            try (OutputStream os = new FileOutputStream(arquivoDestino)) {
                byte[] buffer = new byte[8192];
                int bytesLidos;
                while ((bytesLidos = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesLidos);
                }
            }
        }
    }
}
