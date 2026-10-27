package br.com.ItauDIO.Budgeting.infraestructure.http.request;

import br.com.ItauDIO.Budgeting.application.input.PersistTransactionInput;
import br.com.ItauDIO.Budgeting.domain.Category;

public record TransactionRequest(String description, Category category, long amount) {
    public PersistTransactionInput toInput() {
        return new PersistTransactionInput(description, category, amount);
    }
}
