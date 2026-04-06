package com.supergestao.Flyway.migration.assistant.persistencia.modulo;

import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemErro;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Modulo;
import com.supergestao.Flyway.migration.assistant.exception.ValidacaoException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class RepositorioModuloDisco implements RepositorioModulo {
    @Override
    public void criarDiretorioModulo(String caminhoCompleto) {
        try {
            Files.createDirectories(Paths.get(caminhoCompleto));
        } catch (IOException e) {
            throw new ValidacaoException(MensagemErro.ERRO_SALVAR_MODULO.MensagemComParametro(caminhoCompleto), e);
        }
    }

    @Override
    public HashMap<String, Modulo> obterModulosExistentes(String caminho) {

        try {
            if (caminho == null || caminho.isBlank()) {
                throw new ValidacaoException(MensagemErro.CAMPO_OBRIGATORIO.MensagemComParametro("Diretório dos módulos de entrada não informado"));
            }

            HashMap<String, Modulo> modulosExistentes = new HashMap<>();
            File[] pastas = new File(caminho).listFiles(File::isDirectory);
            if (pastas != null) {
                Long id = 0L;
                for (File dir : pastas) {
                    id++;
                    String nome = dir.getName();
                    String prefixo = dir.getName().substring(0, 3).toUpperCase();
                    Modulo modulo = new Modulo(id, nome, prefixo);
                    if (!nome.equalsIgnoreCase(".backup")) {
                        modulosExistentes.put(prefixo, modulo);
                    }
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
    public HashMap<String, Modulo> obterModuloOrigem(String caminho) {

        if (!caminho.toLowerCase().endsWith(".java")) {
            throw new ValidacaoException(MensagemErro.ARQUIVO_NAO_JAVA.MensagemComParametro(caminho));
        }

        try {
            //le o arquivo de entrada
            String conteudo = Files.readString(Path.of(caminho));
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
                String constante = matcher.group(1);
                Long id = Long.valueOf(matcher.group(2));
                String nome = matcher.group(3);
                String prefixo = matcher.group(4).toUpperCase();
                String descricao = matcher.group(5);

                Modulo modulo = new Modulo(id, nome, constante, prefixo, descricao);
                modulosEntrada.put(prefixo, modulo);
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
}
