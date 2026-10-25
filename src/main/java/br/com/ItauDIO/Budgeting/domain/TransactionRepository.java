package br.com.ItauDIO.Budgeting.domain;

import java.util.List;

public interface TransactionRepository {
    Transaction save(Transaction transaction);
    List<Transaction> findALlByCategory(Category category);
}
