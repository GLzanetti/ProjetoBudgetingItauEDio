package br.com.ItauDIO.Budgeting.infrastructure.http.request;

import br.com.ItauDIO.Budgeting.application.input.PersistTransactionInput;
import br.com.ItauDIO.Budgeting.domain.Category;

/**
 * DTO para cadastro de transações via REST.
 *
 * @param description descrição do lançamento financeiro
 * @param category categoria do lançamento (GROCERIES, PHARMA, AUTO)
 * @param amount valor da transação em centavos (ex: 5000 para R$ 50,00)
 */
public record TransactionRequest(String description, Category category, long amount) {
    public PersistTransactionInput toInput() {
        return new PersistTransactionInput(description, category, amount);
    }
}
