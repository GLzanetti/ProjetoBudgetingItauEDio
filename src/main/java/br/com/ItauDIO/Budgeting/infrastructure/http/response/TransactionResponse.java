package br.com.ItauDIO.Budgeting.infrastructure.http.response;

import br.com.ItauDIO.Budgeting.application.output.TransactionOutput;

public record TransactionResponse (String id, String category, String description ,double amount){
    public static TransactionResponse from(TransactionOutput output){
        return new TransactionResponse(output.id(), output.category(), output.description(), output.value());
    }
}
