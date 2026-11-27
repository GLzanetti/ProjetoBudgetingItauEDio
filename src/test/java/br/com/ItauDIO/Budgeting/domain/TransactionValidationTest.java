package br.com.ItauDIO.Budgeting.domain;

import br.com.ItauDIO.Budgeting.domain.exception.InvalidTransactionException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class TransactionValidationTest {

    @Test
    @DisplayName("Deve lançar InvalidTransactionException quando descrição for nula")
    void deveLancarExcecaoQuandoDescricaoNula() {
        assertThrows(InvalidTransactionException.class,
                () -> new Transaction(null, 1000L, Category.GROCERIES));
    }

    @Test
    @DisplayName("Deve lançar InvalidTransactionException quando descrição estiver em branco")
    void deveLancarExcecaoQuandoDescricaoEmBranco() {
        assertThrows(InvalidTransactionException.class,
                () -> new Transaction("   ", 1000L, Category.GROCERIES));
    }

    @Test
    @DisplayName("Deve lançar InvalidTransactionException quando valor for zero")
    void deveLancarExcecaoQuandoValorZero() {
        assertThrows(InvalidTransactionException.class,
                () -> new Transaction("Mercado", 0L, Category.GROCERIES));
    }

    @Test
    @DisplayName("Deve lançar InvalidTransactionException quando valor for negativo")
    void deveLancarExcecaoQuandoValorNegativo() {
        assertThrows(InvalidTransactionException.class,
                () -> new Transaction("Mercado", -500L, Category.GROCERIES));
    }

    @Test
    @DisplayName("Deve lançar InvalidTransactionException quando categoria for nula")
    void deveLancarExcecaoQuandoCategoriaNula() {
        assertThrows(InvalidTransactionException.class,
                () -> new Transaction("Mercado", 1000L, null));
    }
}
