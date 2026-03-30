package com.supergestao.Flyway.migration.assistant.aplicacao.sincronizar;

import com.supergestao.Flyway.migration.assistant.dominio.modelo.Modulo;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SincronizarModulos {

    public static Map<String, Modulo> obterModulosNovos(Map<String, Modulo> modulosExistentes, Map<String, Modulo> moduloOrigem) {
        Set<String> nomesOrigem = moduloOrigem.values().stream()
                .map(Modulo::getNome)
                .collect(Collectors.toSet());

        modulosExistentes.values().removeIf(modulo -> nomesOrigem.stream().anyMatch(nome -> nome.equalsIgnoreCase(modulo.getNome())));

        return modulosExistentes;
    }
}
