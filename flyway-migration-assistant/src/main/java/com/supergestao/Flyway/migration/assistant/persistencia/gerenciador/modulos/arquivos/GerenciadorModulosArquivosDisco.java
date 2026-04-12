package com.supergestao.Flyway.migration.assistant.persistencia.gerenciador.modulos.arquivos;

import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemErro;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Arquivo;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Funcao;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Modulo;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.TipoMigration;
import com.supergestao.Flyway.migration.assistant.exception.ValidacaoException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GerenciadorModulosArquivosDisco implements GerenciadorModulosArquivos {
    @Override
    public void salvarModulo(String caminhoDoModulo) {
        try {
            Files.createDirectories(Paths.get(caminhoDoModulo));
        } catch (IOException e) {
            throw new ValidacaoException(MensagemErro.ERRO_SALVAR_MODULO.MensagemComParametro(caminhoDoModulo), e);
        }
    }

    @Override
    public String buscarConteudoArquivo(String caminhoDoArquivo) {
        try {
            return Files.readString(Paths.get(caminhoDoArquivo));
        } catch (IOException e) {
            throw new ValidacaoException(MensagemErro.ERRO_SALVAR_ARQUIVO.MensagemComParametro(Paths.get(caminhoDoArquivo).getFileName().toString()), e);
        }
    }

    @Override
    public void salvarArquivo(String caminhoDoArquivo, String conteudoSQL) {
        try {
            Files.writeString(Paths.get(caminhoDoArquivo), conteudoSQL);
        } catch (IOException e) {
            throw new ValidacaoException(MensagemErro.ERRO_SALVAR_ARQUIVO.MensagemComParametro(Paths.get(caminhoDoArquivo).getFileName().toString()), e);
        }
    }

    @Override
    public HashMap<String, Modulo> obterModulosFuncoes(String diretorioRaiz) {

        try {
            if (diretorioRaiz == null || diretorioRaiz.isBlank()) {
                throw new ValidacaoException(MensagemErro.CAMPO_OBRIGATORIO.MensagemComParametro("Diretório dos módulos de entrada não informado"));
            }

            HashMap<String, Modulo> modulosExistentes = new HashMap<>();
            File[] pastas = new File(diretorioRaiz).listFiles(File::isDirectory);
            if (pastas != null) {
                Long id = 0L;
                for (File dirModulo : pastas) {

                    if (dirModulo.getName().startsWith(".")) {
                        continue;
                    }

                    Modulo modulo = new Modulo(id++, dirModulo.getName(), dirModulo.getName().substring(0, 3).toUpperCase());

                    carregarFuncoesNoModulo(dirModulo, modulo);

                    modulosExistentes.put(modulo.getPrefixo(), modulo);

                }
            }
            return modulosExistentes.entrySet()
                    .stream()
                    .sorted(Comparator.comparing(entry -> entry.getValue().getNome()))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> e1, LinkedHashMap::new));

        } catch (Exception e) {
            throw new ValidacaoException(MensagemErro.ERRO_PROCESSAR_MOD_EXISTENTE.getMensagem(), e);
        }
    }

    @Override
    public HashMap<String, Modulo> obterModuloOrigem(String diretorioRaiz) {

        if (!diretorioRaiz.toLowerCase().endsWith(".java")) {
            throw new ValidacaoException(MensagemErro.ARQUIVO_NAO_JAVA.MensagemComParametro(diretorioRaiz));
        }

        try {
            //le o arquivo de entrada
            String conteudo = Files.readString(Path.of(diretorioRaiz));
            // Regex explicada:
            // (\\w+)             -> Grupo 1: Constante
            // (\\d+)L?           -> Grupo 2: ID numérico, ignora o 'L' se houver (1)
            // \"([^\"]+)\"       -> Grupo 3: Nome
            // \"([^\"]+)\"       -> Grupo 4: Prefixo
            // \"([^\"]+)\"       -> Grupo 5: Descrição
            Pattern pattern = Pattern.compile("(\\w+)\\s*\\(\\s*(\\d+)L?\\s*,\\s*\"([^\"]+)\"\\s*,\\s*\"([^\"]+)\"\\s*,\\s*\"([^\"]+)\"\\s*\\)");
            Matcher matcher = pattern.matcher(conteudo);

            HashMap<String, Modulo> modulosEntrada = new HashMap<>();

            while (matcher.find()) {

                Modulo modulo = new Modulo(Long.valueOf(matcher.group(2)),
                        matcher.group(3),
                        matcher.group(1),
                        matcher.group(4).toUpperCase(),
                        matcher.group(5)
                );

                modulosEntrada.put(modulo.getPrefixo(), modulo);
            }

            return modulosEntrada.entrySet()
                    .stream()
                    .sorted(Comparator.comparing(entry -> entry.getValue().getNome()))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> e1, LinkedHashMap::new));

        } catch (IOException e) {
            throw new ValidacaoException(MensagemErro.ERRO_PROCESSAR_ARQ_MODULO.getMensagem(), e);
        }
    }

    @Override
    public HashSet<Arquivo> carregarArquivos(String caminhoFuncao, String nomeModulo, String nomeFuncao) {
        File pastasFuncoes = Paths.get(caminhoFuncao, nomeModulo, nomeFuncao).toFile();
        File[] arquivosFisicos = pastasFuncoes.listFiles();
        HashSet<Arquivo> arquivoEncontrado = new HashSet<>();
        if (arquivosFisicos != null) {
            for (File arquivo : arquivosFisicos) {
                if (!arquivo.getName().endsWith(".sql")) {
                    continue;
                }
                try {
                    TipoMigration tipo = TipoMigration.obterTipoMigration(arquivo.getName());

                    BasicFileAttributes atributosArquivo = Files.readAttributes(arquivo.toPath(), BasicFileAttributes.class);
                    LocalDateTime criacao = LocalDateTime.ofInstant(atributosArquivo.creationTime().toInstant(), ZoneId.systemDefault());
                    LocalDateTime alteracao = LocalDateTime.ofInstant(atributosArquivo.lastModifiedTime().toInstant(), ZoneId.systemDefault());

                    arquivoEncontrado.add(new Arquivo(arquivo.getName(), tipo, criacao, alteracao));

                } catch (Exception e) {
                    throw new ValidacaoException(MensagemErro.ERRO_PROCESSAR_ARQ_EXISTENTE.MensagemComParametro(arquivo.getName()));
                }
            }
        }
        return arquivoEncontrado;
    }

    private void carregarFuncoesNoModulo(File dirModulo, Modulo modulo) {
        File[] pastasFuncoes = dirModulo.listFiles(File::isDirectory);
        if (pastasFuncoes == null) return;
        for (File dirFuncao : pastasFuncoes) {
            if (dirFuncao.getName().startsWith(".")) {
                continue;
            }
            Funcao funcao = new Funcao(dirFuncao.getName());
            modulo.adicionarFuncao(funcao);
        }
    }

    private void carregarFuncoesNoModuloComArquivos(File dirModulo, Modulo modulo) {
        File[] pastasFuncoes = dirModulo.listFiles(File::isDirectory);
        if (pastasFuncoes == null) return;
        for (File dirFuncao : pastasFuncoes) {
            if (dirFuncao.getName().startsWith(".")) {
                continue;
            }
            Funcao funcao = new Funcao(dirFuncao.getName());
            carregarArquivosNaFuncao(dirFuncao, funcao);
            modulo.adicionarFuncao(funcao);
        }
    }

    private void carregarArquivosNaFuncao(File dirFuncao, Funcao funcao) {
        File[] arquivosFisicos = dirFuncao.listFiles();
        if (arquivosFisicos == null) return;
        for (File arquivo : arquivosFisicos) {
            if (!arquivo.getName().endsWith(".sql")) continue;
            try {

                TipoMigration tipo = TipoMigration.obterTipoMigration(arquivo.getName());

                BasicFileAttributes atributosArquivo = Files.readAttributes(arquivo.toPath(), BasicFileAttributes.class);
                LocalDateTime criacao = LocalDateTime.ofInstant(atributosArquivo.creationTime().toInstant(), ZoneId.systemDefault());
                LocalDateTime alteracao = LocalDateTime.ofInstant(atributosArquivo.lastModifiedTime().toInstant(), ZoneId.systemDefault());

                funcao.adicionarArquivo(new Arquivo(arquivo.getName(), tipo, criacao, alteracao));

            } catch (Exception e) {
                throw new ValidacaoException(MensagemErro.ERRO_PROCESSAR_ARQ_EXISTENTE.MensagemComParametro(arquivo.getName()));
            }
        }
    }
}
