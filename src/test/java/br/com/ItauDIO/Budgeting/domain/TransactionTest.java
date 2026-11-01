package br.com.ItauDIO.Budgeting.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TransactionTest {

    @Test
    @DisplayName("Deve criar transação com todos os campos e gerar TransactionId automaticamente")
    void deveCriarTransacaoComCamposCorretos() {
        String description = "Supermercado Mensal";
        long amount = 15000L;
        Category category = Category.GROCERIES;

        Transaction transaction = new Transaction(description, amount, category);

        assertNotNull(transaction.getId());
        assertNotNull(transaction.getId().id());
        assertEquals(description, transaction.getDescription());
        assertEquals(amount, transaction.getAmount());
        assertEquals(category, transaction.getCategory());
    }

    @Test
    @DisplayName("Deve permitir instanciação com TransactionId pré-existente")
    void devePermitirInstanciacaoComIdExistente() {
        TransactionId customId = new TransactionId(UUID.randomUUID());
        String description = "Farmácia";
        long amount = 4500L;
        Category category = Category.PHARMA;

        Transaction transaction = new Transaction(customId, description, amount, category);

        assertEquals(customId, transaction.getId());
        assertEquals(description, transaction.getDescription());
        assertEquals(amount, transaction.getAmount());
        assertEquals(category, transaction.getCategory());
    }

    @ParameterizedTest
    @EnumSource(Category.class)
    @DisplayName("Deve permitir criação com qualquer uma das categorias disponíveis")
    void devePermitirCriacaoComTodasAsCategorias(Category category) {
        Transaction transaction = new Transaction("Despesa Diversa", 2000L, category);

        assertNotNull(transaction.getId());
        assertEquals(category, transaction.getCategory());
    }

    @Test
    @DisplayName("Deve aceitar valores limítrofes e negativos documentando o comportamento atual sem validação")
    void deveAceitarValoresLimitrofesSemValidacao() {
        Transaction zeroAmountTx = new Transaction("Item Grátis", 0L, Category.AUTO);
        Transaction negativeAmountTx = new Transaction("Estorno", -5000L, Category.GROCERIES);

        assertEquals(0L, zeroAmountTx.getAmount());
        assertEquals(-5000L, negativeAmountTx.getAmount());
    }
}

