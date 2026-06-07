package com.supergestao.Flyway.migration.assistant.dominio.regra.sql.validacao;

import com.supergestao.Flyway.migration.assistant.exception.SqlException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegrasValidacaoSqlTest {

    @Test
    void deveAprovarScriptLimpoESeguro() {
        ValidacaoCompletaSql orquestrador = new ValidacaoCompletaSql();

        String scriptPerfeito = "CREATE TABLE usuarios ( id INT PRIMARY KEY ); \n" +
                "UPDATE usuarios SET status = 'ATIVO' WHERE id = 1;";

        assertDoesNotThrow(() -> orquestrador.validarScriptCompleto(scriptPerfeito));
    }

    @Test
    void deveBloquearUpdateSemWhere() {
        ValidacaoCompletaSql orquestrador = new ValidacaoCompletaSql();
        String scriptMaligno = "UPDATE clientes SET status = 'INATIVO';";

        SqlException erro = assertThrows(SqlException.class, () -> {
            orquestrador.validarScriptCompleto(scriptMaligno);
        });

        System.out.println("Orquestrador pegou o erro de Semântica: \n" + erro.getMessage());
    }

    @Test
    void deveBloquearErroDeDigitacaoNoSql() {
        ValidacaoCompletaSql orquestrador = new ValidacaoCompletaSql();

        String scriptComErroDeDigitacao = "UPDAIT clientes SEET status INATIVO;";

        SqlException erro = assertThrows(SqlException.class, () -> {
            orquestrador.validarScriptCompleto(scriptComErroDeDigitacao);
        });

        System.out.println("Orquestrador pegou o erro de Gramática: \n" + erro.getMessage());
    }

    @Test
    void deveBloquearComandoDeInfraestrutura() {
        ValidacaoCompletaSql orquestrador = new ValidacaoCompletaSql();
        String scriptPerigoso = "DROP DATABASE prd_vendas;";

        SqlException erro = assertThrows(SqlException.class, () -> {
            orquestrador.validarScriptCompleto(scriptPerigoso);
        });

        System.out.println("Orquestrador bloqueou o Hacker: \n" + erro.getMessage());
    }
}
