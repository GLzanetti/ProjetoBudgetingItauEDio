package br.com.ItauDIO.Budgeting.application;

import br.com.ItauDIO.Budgeting.application.input.PersistTransactionInput;
import br.com.ItauDIO.Budgeting.application.output.TransactionOutput;
import br.com.ItauDIO.Budgeting.domain.Category;
import br.com.ItauDIO.Budgeting.domain.Transaction;
import br.com.ItauDIO.Budgeting.domain.TransactionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersistTransactionUseCaseTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private PersistTransactionUseCase persistTransactionUseCase;

    @Test
    @DisplayName("Deve persistir transação e retornar output mapeado corretamente")
    void devePersistirTransacaoERetornarOutputMapeado() {
        PersistTransactionInput input = new PersistTransactionInput("Mercado", Category.GROCERIES, 5000L);
        Transaction savedTransaction = new Transaction(input.description(), input.amount(), input.category());

        when(transactionRepository.save(any(Transaction.class))).thenReturn(savedTransaction);

        TransactionOutput output = persistTransactionUseCase.execute(input);

        ArgumentCaptor<Transaction> captor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository, times(1)).save(captor.capture());

        Transaction captured = captor.getValue();
        assertAll(
                () -> assertEquals("Mercado", captured.getDescription()),
                () -> assertEquals(Category.GROCERIES, captured.getCategory()),
                () -> assertEquals(5000L, captured.getAmount()),
                () -> assertNotNull(captured.getId()),
                () -> assertEquals(savedTransaction.getId().id().toString(), output.id()),
                () -> assertEquals("Mercado", output.description()),
                () -> assertEquals("GROCERIES", output.category()),
                () -> assertEquals(5000.0, output.value())
        );
    }

    @Test
    @DisplayName("Deve propagar exceção quando o repositório falhar")
    void devePropagarExcecaoQuandoRepositorioFalhar() {
        PersistTransactionInput input = new PersistTransactionInput("Farmácia", Category.PHARMA, 3000L);
        when(transactionRepository.save(any(Transaction.class)))
                .thenThrow(new RuntimeException("Erro ao conectar com banco de dados"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> persistTransactionUseCase.execute(input));
        assertEquals("Erro ao conectar com banco de dados", exception.getMessage());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }
}
