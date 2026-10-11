package com.supergestao.Flyway.migration.assistant.dominio.modelo;

import com.supergestao.Flyway.migration.assistant.dominio.tipo.AcaoBanco;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.ObjetoBanco;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.SubAcaoBanco;
import com.supergestao.Flyway.migration.assistant.dominio.tipo.TipoMigration;

import java.time.LocalDateTime;

public class ArquivoNovo extends Arquivo{

    Modulo modulo;
    Funcao funcao;
    AcaoBanco acaoBanco;
    ObjetoBanco objetoBanco;
    SubAcaoBanco subAcaoBanco;


    public ArquivoNovo(Modulo modulo, Funcao funcao, String nome,AcaoBanco acaoBanco, ObjetoBanco objetoBanco, SubAcaoBanco subAcaoBanco, TipoMigration tipoMigration, LocalDateTime dataCriacao, LocalDateTime dataAlteracao ) {
        super(nome, tipoMigration, dataCriacao, dataAlteracao);
        this.modulo = modulo;
        this.funcao = funcao;
        this.acaoBanco = acaoBanco;
        this.objetoBanco = objetoBanco;
        this.subAcaoBanco = subAcaoBanco;
    }

    public Modulo getModulo() {
        return (modulo != null) ? modulo : null;
    }

    public void setModulo(Modulo modulo) {
        this.modulo = modulo;
    }

    public Funcao getFuncao() {
        return (funcao != null) ? funcao : null;
    }

    public void setFuncao(Funcao funcao) {
        this.funcao = funcao;
    }

    public AcaoBanco getAcaoBanco() {
        return (acaoBanco != null) ? acaoBanco : null;
    }

    public void setAcaoBanco(AcaoBanco acaoBanco) {
        this.acaoBanco = acaoBanco;
    }

    public ObjetoBanco getObjetoBanco() {
        return (objetoBanco != null) ? objetoBanco : null;
    }

    public void setObjetoBanco(ObjetoBanco objetoBanco) {
        this.objetoBanco = objetoBanco;
    }

    public SubAcaoBanco getSubAcaoBanco() {
        return (subAcaoBanco != null) ? subAcaoBanco : null;
    }

    public void setSubAcaoBanco(SubAcaoBanco subAcaoBanco) {
        this.subAcaoBanco = subAcaoBanco;
    }

    public String getModuloNome() {
        return (modulo != null) ? modulo.getNome() : null;
    }

    public String getFuncaoNome() {
        return (funcao != null) ? funcao.getNome() : null;
    }

    public String getAcaoBancoNome() {
        return (acaoBanco != null) ? acaoBanco.getAcao() : null;
    }

    public String getObjetoBancoNome() {
        return (objetoBanco != null) ? objetoBanco.getIdentificador() : null;
    }

    public String getSubAcaoBancoNome() {
        return (subAcaoBanco != null) ? subAcaoBanco.getDescricao() : null;
    }
}
