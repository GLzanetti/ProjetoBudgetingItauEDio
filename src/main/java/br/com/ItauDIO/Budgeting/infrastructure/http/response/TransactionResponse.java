package br.com.ItauDIO.Budgeting.infrastructure.http.response;

import br.com.ItauDIO.Budgeting.application.output.TransactionOutput;

import java.math.BigDecimal;

public record TransactionResponse(String id, String category, String description, BigDecimal amount) {
    public static TransactionResponse from(TransactionOutput output) {
        return new TransactionResponse(output.id(), output.category(), output.description(), output.value());
    }
}
