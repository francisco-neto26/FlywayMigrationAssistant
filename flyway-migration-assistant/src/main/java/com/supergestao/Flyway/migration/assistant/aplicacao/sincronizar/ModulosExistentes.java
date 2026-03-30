package com.supergestao.Flyway.migration.assistant.aplicacao.sincronizar;

import com.supergestao.Flyway.migration.assistant.dominio.mensagem.MensagemErro;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Modulo;
import com.supergestao.Flyway.migration.assistant.exception.ValidacaoException;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.substring;

public class ModulosExistentes {

    public static HashMap<String, Modulo> obterModulosExistentes(String caminho) {

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
                    String prefixo = substring(dir.getName(), 3).toUpperCase();
                    Modulo modulo = new Modulo(id, nome, prefixo);
                    modulosExistentes.put(prefixo, modulo);
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
}
