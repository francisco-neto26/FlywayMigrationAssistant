package com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao.modelo;

// Divide o script em blocos SQL
public class BlocoSql {

    private final TipoSql tipo;
    private final SecaoSql cabecalho;
    private final SecaoSql corpo;
    private final SecaoSql fechamento;

    public BlocoSql(TipoSql tipo, SecaoSql cabecalho, SecaoSql corpo, SecaoSql fechamento) {
        this.tipo = tipo;
        this.cabecalho = cabecalho != null ? cabecalho : new SecaoSql("", 0);
        this.corpo = corpo != null ? corpo : new SecaoSql("", 0);
        this.fechamento = fechamento != null ? fechamento : new SecaoSql("", 0);
    }
    public TipoSql getTipo() {
        return tipo;
    }

    public SecaoSql getCabecalho() {
        return cabecalho;
    }

    public SecaoSql getCorpo() {
        return corpo;
    }

    public SecaoSql getFechamento() {
        return fechamento;
    }

    public String getConteudoCompleto() {
        return cabecalho.getConteudo() + corpo.getConteudo() + fechamento.getConteudo();
    }
}
