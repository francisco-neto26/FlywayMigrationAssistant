package com.supergestao.FlywayMigrationAssistant.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ModuloConfig {

    private static final Map<String, String> MODULE_PREFIXES = new HashMap<>();

    static {
        MODULE_PREFIXES.put("config", "A");
        MODULE_PREFIXES.put("clientes", "B");
        MODULE_PREFIXES.put("vendas", "C");
        MODULE_PREFIXES.put("produtos", "D");
        MODULE_PREFIXES.put("financeiro", "E");
        MODULE_PREFIXES.put("estoque", "F");
        MODULE_PREFIXES.put("relatorios", "G");
    }

    public static String getPrefix(String moduleName) {
        return MODULE_PREFIXES.getOrDefault(moduleName.toLowerCase(), "Z");
    }

    public static Set<String> getModuleNames() {
        return MODULE_PREFIXES.keySet();
    }

    public static void addModule(String moduleName, String prefix) {
        MODULE_PREFIXES.put(moduleName.toLowerCase(), prefix.toUpperCase());
    }
}
