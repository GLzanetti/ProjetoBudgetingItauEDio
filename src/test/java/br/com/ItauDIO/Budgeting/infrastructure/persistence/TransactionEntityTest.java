package br.com.ItauDIO.Budgeting.infrastructure.persistence;

import br.com.ItauDIO.Budgeting.domain.Category;
import br.com.ItauDIO.Budgeting.domain.Transaction;
import br.com.ItauDIO.Budgeting.domain.TransactionId;
import br.com.ItauDIO.Budgeting.infrastructure.persistence.entity.TransactionEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class TransactionEntityTest {

    @Test
    @DisplayName("Deve garantir mapeamento bidirecional entre domínio e TransactionEntity")
    void deveGarantirMapeamentoBidirecionalEntreDominioEEntity() {
        UUID expectedUuid = UUID.randomUUID();
        TransactionId transactionId = new TransactionId(expectedUuid);
        Transaction domainOriginal = new Transaction(transactionId, "Combustível Posto Ipiranga", 25000L, Category.AUTO);

        TransactionEntity entity = TransactionEntity.from(domainOriginal);

        assertAll(
                () -> assertEquals(expectedUuid, entity.getId()),
                () -> assertEquals("Combustível Posto Ipiranga", entity.getDescription()),
                () -> assertEquals(25000L, entity.getAmount()),
                () -> assertEquals(Category.AUTO, entity.getCategory())
        );

        Transaction domainReconvertido = entity.toDomain();

        assertAll(
                () -> assertEquals(domainOriginal.getId(), domainReconvertido.getId()),
                () -> assertEquals(domainOriginal.getId().id(), domainReconvertido.getId().id()),
                () -> assertEquals(domainOriginal.getDescription(), domainReconvertido.getDescription()),
                () -> assertEquals(domainOriginal.getAmount(), domainReconvertido.getAmount()),
                () -> assertEquals(domainOriginal.getCategory(), domainReconvertido.getCategory())
        );
    }
}

