package br.com.ItauDIO.Budgeting.infrastructure.persistence;

import br.com.ItauDIO.Budgeting.domain.Category;
import br.com.ItauDIO.Budgeting.domain.Transaction;
import br.com.ItauDIO.Budgeting.domain.TransactionId;
import br.com.ItauDIO.Budgeting.infrastructure.persistence.entity.TransactionEntity;
import br.com.ItauDIO.Budgeting.infrastructure.persistence.repository.JpaTransactionRepository;
import br.com.ItauDIO.Budgeting.infrastructure.persistence.repository.TransactionEntityRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JpaTransactionRepositoryTest {

    @Mock
    private TransactionEntityRepository transactionEntityRepository;

    @InjectMocks
    private JpaTransactionRepository jpaTransactionRepository;

    @Test
    @DisplayName("Deve salvar transação mapeando para entity e retornando o domínio atualizado")
    void deveSalvarTransacaoMapeandoParaEntity() {
        TransactionId transactionId = new TransactionId(UUID.randomUUID());
        Transaction domainTx = new Transaction(transactionId, "Manutenção Veículo", 45000L, Category.AUTO);

        TransactionEntity savedEntity = new TransactionEntity(
                transactionId.id(),
                "Manutenção Veículo",
                45000L,
                Category.AUTO
        );

        when(transactionEntityRepository.save(any(TransactionEntity.class))).thenReturn(savedEntity);

        Transaction result = jpaTransactionRepository.save(domainTx);

        ArgumentCaptor<TransactionEntity> captor = ArgumentCaptor.forClass(TransactionEntity.class);
        verify(transactionEntityRepository, times(1)).save(captor.capture());

        TransactionEntity capturedEntity = captor.getValue();
        assertAll(
                () -> assertEquals(transactionId.id(), capturedEntity.getId()),
                () -> assertEquals("Manutenção Veículo", capturedEntity.getDescription()),
                () -> assertEquals(45000L, capturedEntity.getAmount()),
                () -> assertEquals(Category.AUTO, capturedEntity.getCategory()),
                () -> assertEquals(domainTx.getId(), result.getId()),
                () -> assertEquals("Manutenção Veículo", result.getDescription()),
                () -> assertEquals(45000L, result.getAmount()),
                () -> assertEquals(Category.AUTO, result.getCategory())
        );
    }

    @Test
    @DisplayName("Deve buscar todas por categoria e reconverter para domínio")
    void deveBuscarTodasPorCategoriaEReconverterParaDominio() {
        Category category = Category.AUTO;
        TransactionEntity entity1 = new TransactionEntity(UUID.randomUUID(), "Troca de óleo", 12000L, category);
        TransactionEntity entity2 = new TransactionEntity(UUID.randomUUID(), "Lavagem", 6000L, category);

        when(transactionEntityRepository.findAllByCategory(category)).thenReturn(List.of(entity1, entity2));

        List<Transaction> result = jpaTransactionRepository.findAllByCategory(category);

        assertAll(
                () -> assertEquals(2, result.size()),
                () -> assertEquals(entity1.getId(), result.get(0).getId().id()),
                () -> assertEquals("Troca de óleo", result.get(0).getDescription()),
                () -> assertEquals(12000L, result.get(0).getAmount()),
                () -> assertEquals(category, result.get(0).getCategory()),
                () -> assertEquals(entity2.getId(), result.get(1).getId().id()),
                () -> assertEquals("Lavagem", result.get(1).getDescription()),
                () -> assertEquals(6000L, result.get(1).getAmount()),
                () -> assertEquals(category, result.get(1).getCategory())
        );

        verify(transactionEntityRepository, times(1)).findAllByCategory(category);
    }

    @Test
    @DisplayName("Deve propagar exceção quando o repositório JPA falhar")
    void devePropagarExcecaoQuandoEntityRepositoryFalhar() {
        when(transactionEntityRepository.findAllByCategory(any(Category.class)))
                .thenThrow(new RuntimeException("Falha ao consultar banco H2"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> jpaTransactionRepository.findAllByCategory(Category.GROCERIES));

        assertEquals("Falha ao consultar banco H2", exception.getMessage());
        verify(transactionEntityRepository, times(1)).findAllByCategory(Category.GROCERIES);
    }

    @Test
    @DisplayName("Deve somar valor por categoria com sucesso delegando ao repositório JPA")
    void deveSomarValorPorCategoriaComSucesso() {
        Category category = Category.GROCERIES;
        when(transactionEntityRepository.sumAmountByCategory(category)).thenReturn(7500L);

        long total = jpaTransactionRepository.sumAmountByCategory(category);

        assertEquals(7500L, total);
        verify(transactionEntityRepository, times(1)).sumAmountByCategory(category);
    }

    @Test
    @DisplayName("Deve retornar zero quando o repositório JPA retornar null para a soma da categoria")
    void deveRetornarZeroQuandoSumAmountByCategoryRetornarNull() {
        Category category = Category.PHARMA;
        when(transactionEntityRepository.sumAmountByCategory(category)).thenReturn(null);

        long total = jpaTransactionRepository.sumAmountByCategory(category);

        assertEquals(0L, total);
        verify(transactionEntityRepository, times(1)).sumAmountByCategory(category);
    }
}

