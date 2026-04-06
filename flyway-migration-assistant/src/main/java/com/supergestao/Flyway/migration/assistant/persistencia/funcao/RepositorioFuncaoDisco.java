package com.supergestao.Flyway.migration.assistant.persistencia.funcao;

import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemErro;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Funcao;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Modulo;
import com.supergestao.Flyway.migration.assistant.exception.ValidacaoException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class RepositorioFuncaoDisco implements RepositorioFuncao {
    @Override
    public void criarDiretorioFuncao(String caminhoCompleto) {
        try {
            Files.createDirectories(Paths.get(caminhoCompleto));
        } catch (IOException e) {
            throw new ValidacaoException(MensagemErro.ERRO_SALVAR_FUNCAO.MensagemComParametro(caminhoCompleto), e);
        }
    }
    //PRecisa implementara busca das funções que nada mas é que as subpastas dos modulos, ou seja, vai pegar o diretorio de entrada e ler as
    //pastas, tudo que existir dentro destas pastas vai ser funções, considerar somente pastas, arquivos são os migrations.
    @Override
    public HashMap<String, Funcao> obterFuncaoExistentes(String caminho) {

        try {
            if (caminho == null || caminho.isBlank()) {
                throw new ValidacaoException(MensagemErro.CAMPO_OBRIGATORIO.MensagemComParametro("Diretório das funções não informado"));
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
            throw new ValidacaoException(MensagemErro.ERRO_PROCESSAR_FUN_EXISTENTE.getMensagem(), e);
        }
    }

}
