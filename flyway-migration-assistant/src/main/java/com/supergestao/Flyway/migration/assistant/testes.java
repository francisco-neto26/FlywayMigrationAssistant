package com.supergestao.Flyway.migration.assistant;


import com.supergestao.Flyway.migration.assistant.aplicacao.buscar.BuscarArquivos;
import com.supergestao.Flyway.migration.assistant.aplicacao.sincronizar.SincronizarModulos;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Arquivo;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Funcao;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Modulo;
import com.supergestao.Flyway.migration.assistant.dominio.regra.migration.GerarNomeArquivoMigration;
import com.supergestao.Flyway.migration.assistant.dominio.regra.tempo.GeradorDataHora;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.AcaoBanco;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.ObjetoBanco;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.TipoMigration;
import com.supergestao.Flyway.migration.assistant.exception.ValidacaoException;
import com.supergestao.Flyway.migration.assistant.persistencia.gerenciador.modulos.arquivos.GerenciadorModulosArquivos;
import com.supergestao.Flyway.migration.assistant.persistencia.gerenciador.modulos.arquivos.GerenciadorModulosArquivosDisco;

import java.text.Normalizer;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Map;

import static java.time.LocalDateTime.now;


public class testes {
    private static final ZoneId ZONA_PADRAO = ZoneId.systemDefault();

    public static void testarGeradorDataHora() {
        String caminhoOrigem = "C:\\Users\\User\\Desktop\\ERP-Super-Gestao\\backend\\supergestao\\src\\main\\java\\br\\com\\supergestao\\supergestao\\administracao\\modulos\\enums\\ModuloEnum.java";
        String caminhoExistentes = "C:\\Users\\User\\Desktop\\ERP-Super-Gestao\\backend\\supergestao\\src\\main\\resources\\db\\migration";
        // 1. Instanciamos a sua classe de Infraestrutura (Quem lê do HD)
        GerenciadorModulosArquivos repositorioDisco = new GerenciadorModulosArquivosDisco();

        // 2. Injetamos o Repositório na sua classe de Negócio (Inversão de Dependência)
        SincronizarModulos sincronizarModulos = new SincronizarModulos(repositorioDisco);
        BuscarArquivos listaarquivos = new BuscarArquivos(repositorioDisco);
        GerarNomeArquivoMigration novonome = new GerarNomeArquivoMigration();
        GeradorDataHora hora = new GeradorDataHora();
        try {


            // 3. Chamamos o método separado que apenas CONSULTA (Query)
            boolean existemNovos = sincronizarModulos.existeModuloParaSincronizar(caminhoOrigem, caminhoExistentes);
            Map<String, Modulo> modulosExistentes = sincronizarModulos.obterModulosExistentes(caminhoExistentes);

            String nomearquivo = novonome.gerarNomeArquivoMigration(TipoMigration.REPEATABLE, AcaoBanco.CREATE, ObjetoBanco.TABLE, hora.gerarDataFlyway(), "minha_funcao", "MeuNome");

            System.out.println(nomearquivo);
            String undo = novonome.gerarNomeArquivoMigrationUndo(nomearquivo);
            System.out.println(undo);


            /*
            for (Map.Entry<String, Modulo> entry : modulosExistentes.entrySet()) {
                String chave = entry.getKey();
                Modulo modulo = entry.getValue();
                System.out.println("Chave: " + chave);
                System.out.println("Nome do Módulo: " + modulo.getNome());
                System.out.println("ID: " + modulo.getId());
                System.out.println("Prefixo: " + modulo.getPrefixo());
                System.out.println("Funções: " + modulo.getFuncoes().stream().map(Funcao::getNome).toList());
                for (Funcao funcao : modulo.getFuncoes()){
                    HashSet<Arquivo> arquivos = listaarquivos.carregarArquivos(caminhoExistentes, modulo.getNome(), funcao.getNome());
                    if(arquivos.isEmpty()){
                        System.out.println("Nenhum arquivo encontrado para a função: " + funcao.getNome());
                        continue;
                    }else{
                        System.out.println("arquivos existentes\n");
                        for (Arquivo arquivo : arquivos) {
                            System.out.println("Funçao: " + funcao.getNome());
                            System.out.println("Nome do Arquivo: " + arquivo.getNome());

                        }
                    }
                }

                //System.out.println("Modulos existentes\n");
                if(modulo.getNome().equalsIgnoreCase("Administração")){
                    sincronizarModulos.criarNovaFuncao(modulo.getNome(), "NovaFuncaoTeste", caminhoExistentes);
                }
            }

            HashSet<Arquivo> arquivos = listaarquivos.carregarArquivos(caminhoExistentes, "Compras", "Nota Fisca");

            for (Arquivo arquivo : arquivos) {
                System.out.println("Nome do Arquivo: " + arquivo.getNome());
                System.out.println("arquivos existentes\n");

            }


            if (existemNovos) {
                System.out.println("Módulos novos detectados! Iniciando sincronização...");

                // 4. Chamamos o método que EXECUTA a ação de fato (Command)
                sincronizarModulos.executarSincronizacao(caminhoOrigem, caminhoExistentes);

                System.out.println("Módulos novos criados com sucesso!");
            } else {
                System.out.println("Nenhum módulo novo encontrado. Sistema já está atualizado.");
            }*/
        } catch (ValidacaoException e) {
            // Qualquer erro do Domínio ou de Disco convertido para Domínio cai lindamente aqui!
            System.err.println("Erro na Validação/Sincronização: " + e.getMessage());
            // Se quiser ver aquele rastro do erro técnico (o stack trace), pode colocar um e.printStackTrace();
        }

        String normalizado = Normalizer.normalize("Não axuç", Normalizer.Form.NFD);

        // 2. Agora sim, remove tudo o que não for letra ou número básico
        System.out.println("Texto : " + normalizado.replaceAll("[^a-zA-Z0-9]", ""));



        /*
        try {
            //Map<String, Modulo> modulos = ModulosOrigem.obterModuloOrigem(caminhoOrigem);
            //Map<String, Modulo> modulosExistentes = ModulosExistentes.obterModulosExistentes(caminhoExistentes);
           // boolean modulosnovos = sincronizarModulos.sincronizarModulos(caminhoOrigem, caminhoExistentes, "consultar");
/*
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
        }*/
    }
}
