package br.com.ItauDIO.Budgeting.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TransactionIdTest {

    @Test
    @DisplayName("Deve gerar um UUID válido por padrão quando instanciado sem argumentos")
    void deveGerarUUIDValidoPorPadrao() {
        TransactionId transactionId = new TransactionId();

        assertNotNull(transactionId.id());
    }

    @Test
    @DisplayName("Deve preservar o UUID fornecido no construtor e manter igualdade de record")
    void devePreservarUUIDFornecido() {
        UUID uuid = UUID.randomUUID();
        TransactionId id1 = new TransactionId(uuid);
        TransactionId id2 = new TransactionId(uuid);

        assertEquals(uuid, id1.id());
        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
    }
}

