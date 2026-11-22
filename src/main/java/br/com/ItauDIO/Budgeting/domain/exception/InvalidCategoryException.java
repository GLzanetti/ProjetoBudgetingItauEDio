package br.com.ItauDIO.Budgeting.domain.exception;

public class InvalidCategoryException extends IllegalArgumentException {
    public InvalidCategoryException(String message) {
        super(message);
    }
}
