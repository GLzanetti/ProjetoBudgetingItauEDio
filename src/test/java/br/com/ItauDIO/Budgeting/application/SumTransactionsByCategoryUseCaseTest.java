package br.com.ItauDIO.Budgeting.application;

import br.com.ItauDIO.Budgeting.application.output.CategoryTotalOutput;
import br.com.ItauDIO.Budgeting.domain.Category;
import br.com.ItauDIO.Budgeting.domain.TransactionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SumTransactionsByCategoryUseCaseTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private SumTransactionsByCategoryUseCase sumTransactionsByCategoryUseCase;

    @Test
    @DisplayName("Deve somar transações por categoria e retornar output formatado em reais")
    void deveSomarTransacoesPorCategoriaERetornarOutputFormatado() {
        when(transactionRepository.sumAmountByCategory(Category.GROCERIES)).thenReturn(12550L);

        CategoryTotalOutput result = sumTransactionsByCategoryUseCase.execute(Category.GROCERIES);

        assertNotNull(result);
        assertAll(
                () -> assertEquals("GROCERIES", result.category()),
                () -> assertEquals(new BigDecimal("125.50"), result.total())
        );

        verify(transactionRepository, times(1)).sumAmountByCategory(Category.GROCERIES);
    }

    @Test
    @DisplayName("Deve retornar zero formatado quando categoria não possuir transações")
    void deveRetornarZeroQuandoCategoriaNaoTiverTransacoes() {
        when(transactionRepository.sumAmountByCategory(Category.PHARMA)).thenReturn(0L);

        CategoryTotalOutput result = sumTransactionsByCategoryUseCase.execute(Category.PHARMA);

        assertNotNull(result);
        assertAll(
                () -> assertEquals("PHARMA", result.category()),
                () -> assertEquals(new BigDecimal("0.00"), result.total())
        );

        verify(transactionRepository, times(1)).sumAmountByCategory(Category.PHARMA);
    }

    @Test
    @DisplayName("Deve propagar exceção quando o repositório falhar na consulta")
    void devePropagarExcecaoQuandoRepositorioFalhar() {
        when(transactionRepository.sumAmountByCategory(Category.AUTO))
                .thenThrow(new RuntimeException("Erro ao consultar banco de dados"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> sumTransactionsByCategoryUseCase.execute(Category.AUTO));

        assertEquals("Erro ao consultar banco de dados", exception.getMessage());
        verify(transactionRepository, times(1)).sumAmountByCategory(Category.AUTO);
    }
}
