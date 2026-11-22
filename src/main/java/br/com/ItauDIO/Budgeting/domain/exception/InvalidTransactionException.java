package br.com.ItauDIO.Budgeting.domain.exception;

public class InvalidTransactionException extends IllegalArgumentException {
    public InvalidTransactionException(String message) {
        super(message);
    }
}
