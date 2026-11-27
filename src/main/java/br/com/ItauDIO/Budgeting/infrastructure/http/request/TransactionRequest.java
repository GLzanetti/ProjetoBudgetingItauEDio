package br.com.ItauDIO.Budgeting.infrastructure.http.request;

import br.com.ItauDIO.Budgeting.application.input.PersistTransactionInput;
import br.com.ItauDIO.Budgeting.domain.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO para cadastro de transações via REST.
 *
 * @param description descrição do lançamento financeiro
 * @param category categoria do lançamento (GROCERIES, PHARMA, AUTO)
 * @param amount valor da transação em centavos (ex: 5000 para R$ 50,00)
 */
public record TransactionRequest(
        @NotBlank(message = "A descrição é obrigatória.") String description,
        @NotNull(message = "A categoria é obrigatória.") Category category,
        @Positive(message = "O valor deve ser maior que zero.") long amount
) {
    public PersistTransactionInput toInput() {
        return new PersistTransactionInput(description, category, amount);
    }
}
