package com.supergestao.Flyway.migration.assistant.dominio.regra.migration;

import com.supergestao.Flyway.migration.assistant.dominio.modelo.Funcao;
import com.supergestao.Flyway.migration.assistant.dominio.modelo.Modulo;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.AcaoBanco;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.ObjetoBanco;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.SubAcaoBanco;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.TipoMigration;
import com.supergestao.Flyway.migration.assistant.ui.estado.ContextoAplicacao;

import java.util.ArrayList;
import java.util.List;

import static com.supergestao.Flyway.migration.assistant.ui.utilitario.FormataSnakeCase.formatarSnakeCase;

public class GerarNomeArquivoMigration {
    public String gerarNomeArquivoMigration(ContextoAplicacao contextoAplicacao,
                                            TipoMigration tipoMigration,
                                            Modulo modulo,
                                            Funcao funcao,
                                            AcaoBanco acaoBanco,
                                            ObjetoBanco objetoBanco,
                                            SubAcaoBanco subAcaoBanco,
                                            String nome) {

        List<String> parteNome = new ArrayList<>();
        StringBuilder nomeFinal = new StringBuilder();

        adicionarParteValida(parteNome, (modulo != null) ? modulo.getPrefixo() : null);
        adicionarParteValida(parteNome, (funcao != null) ? funcao.getNome() : null);
        adicionarParteValida(parteNome, (acaoBanco != null) ? acaoBanco.getAcao() : null);
        adicionarParteValida(parteNome, (objetoBanco != null) ? objetoBanco.getIdentificador() : null);
        adicionarParteValida(parteNome, (subAcaoBanco != null) ? subAcaoBanco.getDescricao() : null);
        adicionarParteValida(parteNome, nome);

        nomeFinal.append(tipoMigration.getPrefixo().toUpperCase());
        if (tipoMigration.getRequerTimestamp()) {
            nomeFinal.append(contextoAplicacao.getGeradorDataHora().gerarDataFlyway());
        }

        nomeFinal.append("__").append(formatarSnakeCase(String.join(" ", parteNome))).append(".sql");
        return nomeFinal.toString();
    }

    public String gerarNomeArquivoMigrationUndo(String nomeArquivo) {
        return nomeArquivo.replaceFirst("^V", "U");
    }

    private void adicionarParteValida(List<String> parteNome, String textoOriginal) {
        if (textoOriginal != null && !textoOriginal.isBlank()) {
            String textoFormatado = formatarSnakeCase(textoOriginal);
            parteNome.add(textoFormatado.trim());
        }
    }
}