package com.supergestao.Flyway.migration.assistant;

import com.supergestao.Flyway.migration.assistant.aplicacao.sincronizar.ModulosExistentes;
import com.supergestao.Flyway.migration.assistant.aplicacao.sincronizar.ModulosOrigem;
import com.supergestao.Flyway.migration.assistant.aplicacao.sincronizar.SincronizarModulos;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Modulo;
import com.supergestao.Flyway.migration.assistant.exception.ValidacaoException;

import java.time.ZoneId;
import java.util.Map;

public class testes {
    private static final ZoneId ZONA_PADRAO = ZoneId.systemDefault();

    public static void testarGeradorDataHora() {


        try {
            Map<String, Modulo> modulos = ModulosOrigem.obterModuloOrigem("C:\\Users\\User\\Desktop\\ERP-Super-Gestao\\backend\\supergestao\\src\\main\\java\\br\\com\\supergestao\\supergestao\\administracao\\modulos\\enums\\ModuloEnum.java");
            Map<String, Modulo> modulosExistentes = ModulosExistentes.obterModulosExistentes("C:\\Users\\User\\Desktop\\ERP-Super-Gestao\\backend\\supergestao\\src\\main\\resources\\db\\migration");
            Map<String, Modulo> modulosnovos = SincronizarModulos.obterModulosNovos(modulosExistentes, modulos);

            modulos.forEach((constante, objetoModulo) -> {
                System.out.println("Chave: " + constante);
                System.out.println("Nome do Módulo: " + objetoModulo.getNome());
                System.out.println("ID: " + objetoModulo.getId());
                System.out.println("Modulos entrada\n");
            });

            modulosExistentes.forEach((prefixo, objetoModulo) -> {
                System.out.println("Prefi: " + prefixo);
                System.out.println("Nome do Módulo: " + objetoModulo.getNome());
                System.out.println("ID: " + objetoModulo.getId());
                System.out.println("Modulos existentes\n");
            });

            modulosnovos.forEach((prefixo, objetoModulo) -> {
                System.out.println("Prefi: " + prefixo);
                System.out.println("Nome do Módulo: " + objetoModulo.getNome());
                System.out.println("ID: " + objetoModulo.getId());
                System.out.println("Modulos novos");
            });

        } catch (ValidacaoException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}
