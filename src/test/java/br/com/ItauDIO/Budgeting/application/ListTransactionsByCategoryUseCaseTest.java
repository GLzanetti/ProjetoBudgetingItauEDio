package br.com.ItauDIO.Budgeting.application;

import br.com.ItauDIO.Budgeting.application.output.TransactionOutput;
import br.com.ItauDIO.Budgeting.domain.Category;
import br.com.ItauDIO.Budgeting.domain.Transaction;
import br.com.ItauDIO.Budgeting.domain.TransactionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListTransactionsByCategoryUseCaseTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private ListTransactionsByCategoryUseCase listTransactionsByCategoryUseCase;

    @Test
    @DisplayName("Deve retornar lista de TransactionOutput quando categoria possuir transações")
    void deveRetornarListaDeTransactionOutputQuandoExistiremTransacoes() {
        Category category = Category.PHARMA;
        Transaction tx1 = new Transaction("Remédio A", 3500L, category);
        Transaction tx2 = new Transaction("Remédio B", 1200L, category);

        when(transactionRepository.findAllByCategory(category)).thenReturn(List.of(tx1, tx2));

        List<TransactionOutput> outputs = listTransactionsByCategoryUseCase.execute(category);

        assertAll(
                () -> assertNotNull(outputs),
                () -> assertEquals(2, outputs.size()),
                () -> assertEquals(tx1.getId().id().toString(), outputs.get(0).id()),
                () -> assertEquals("Remédio A", outputs.get(0).description()),
                () -> assertEquals("PHARMA", outputs.get(0).category()),
                () -> assertEquals(new BigDecimal("35.00"), outputs.get(0).value()),
                () -> assertEquals(tx2.getId().id().toString(), outputs.get(1).id()),
                () -> assertEquals("Remédio B", outputs.get(1).description()),
                () -> assertEquals("PHARMA", outputs.get(1).category()),
                () -> assertEquals(new BigDecimal("12.00"), outputs.get(1).value())
        );

        verify(transactionRepository, times(1)).findAllByCategory(category);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver registros para a categoria")
    void deveRetornarListaVaziaQuandoNaoHouverRegistros() {
        Category category = Category.AUTO;
        when(transactionRepository.findAllByCategory(category)).thenReturn(List.of());

        List<TransactionOutput> outputs = listTransactionsByCategoryUseCase.execute(category);

        assertNotNull(outputs);
        assertTrue(outputs.isEmpty());
        verify(transactionRepository, times(1)).findAllByCategory(category);
    }

    @Test
    @DisplayName("Deve propagar exceção quando a consulta no repositório falhar")
    void devePropagarExcecaoQuandoConsultaFalhar() {
        Category category = Category.GROCERIES;
        when(transactionRepository.findAllByCategory(category))
                .thenThrow(new IllegalStateException("Falha de conexão com a base de dados"));

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> listTransactionsByCategoryUseCase.execute(category));

        assertEquals("Falha de conexão com a base de dados", exception.getMessage());
        verify(transactionRepository, times(1)).findAllByCategory(category);
    }
}
