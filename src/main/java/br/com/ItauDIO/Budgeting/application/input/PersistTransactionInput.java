package br.com.ItauDIO.Budgeting.application.input;

import br.com.ItauDIO.Budgeting.domain.Category;

public record PersistTransactionInput(String description, Category category, long amount) {
}
