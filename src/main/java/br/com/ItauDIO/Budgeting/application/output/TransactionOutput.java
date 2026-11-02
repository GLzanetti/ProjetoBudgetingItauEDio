package br.com.ItauDIO.Budgeting.application.output;

import br.com.ItauDIO.Budgeting.domain.Transaction;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record TransactionOutput(String id, String description, String category, BigDecimal value) {
    public static TransactionOutput from(Transaction transaction) {
        return new TransactionOutput(
                transaction.getId().id().toString(),
                transaction.getDescription(),
                transaction.getCategory().name(),
                BigDecimal.valueOf(transaction.getAmount(), 2).setScale(2, RoundingMode.HALF_UP)
        );
    }
}
