package br.com.ItauDIO.Budgeting.infrastructure.http;

import br.com.ItauDIO.Budgeting.domain.Category;
import br.com.ItauDIO.Budgeting.domain.exception.InvalidCategoryException;
import br.com.ItauDIO.Budgeting.domain.exception.InvalidTransactionException;
import br.com.ItauDIO.Budgeting.domain.exception.TransactionNotFoundException;
import br.com.ItauDIO.Budgeting.infrastructure.exception.AIServiceException;
import br.com.ItauDIO.Budgeting.infrastructure.exception.AudioProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.net.URI;
import java.util.Arrays;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidTransactionException.class)
    public ProblemDetail handleInvalidTransaction(InvalidTransactionException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Transação inválida");
        problemDetail.setType(URI.create("/errors/invalid-transaction"));
        return problemDetail;
    }

    @ExceptionHandler(InvalidCategoryException.class)
    public ProblemDetail handleInvalidCategory(InvalidCategoryException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        problemDetail.setTitle("Categoria inválida");
        problemDetail.setType(URI.create("/errors/invalid-category"));
        return problemDetail;
    }

    @ExceptionHandler(TransactionNotFoundException.class)
    public ProblemDetail handleTransactionNotFound(TransactionNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Transação não encontrada");
        problemDetail.setType(URI.create("/errors/transaction-not-found"));
        return problemDetail;
    }

    @ExceptionHandler(AudioProcessingException.class)
    public ProblemDetail handleAudioProcessing(AudioProcessingException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
        problemDetail.setTitle("Erro no processamento de áudio");
        problemDetail.setType(URI.create("/errors/audio-processing"));
        return problemDetail;
    }

    @ExceptionHandler(AIServiceException.class)
    public ProblemDetail handleAIService(AIServiceException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.SERVICE_UNAVAILABLE, ex.getMessage());
        problemDetail.setTitle("Serviço de IA indisponível");
        problemDetail.setType(URI.create("/errors/ai-service-unavailable"));
        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String detail = ex.getMessage();
        if (ex.getRequiredType() != null && ex.getRequiredType().equals(Category.class)) {
            String validCategories = Arrays.stream(Category.values())
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));
            detail = String.format("A categoria '%s' não é reconhecida. Categorias disponíveis: %s.",
                    ex.getValue(), validCategories);
        }

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
        problemDetail.setTitle("Parâmetro de requisição inválido");
        problemDetail.setType(URI.create("/errors/invalid-parameter"));
        return problemDetail;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Requisição malformada ou corpo JSON inválido."
        );
        problemDetail.setTitle("Requisição malformada");
        problemDetail.setType(URI.create("/errors/malformed-request"));
        return problemDetail;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format("%s: %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.joining(", "));

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                details.isBlank() ? "Falha na validação dos campos." : details
        );
        problemDetail.setTitle("Erro de validação");
        problemDetail.setType(URI.create("/errors/validation"));
        return problemDetail;
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ProblemDetail handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.PAYLOAD_TOO_LARGE,
                "O arquivo enviado excede o tamanho máximo permitido."
        );
        problemDetail.setTitle("Arquivo muito grande");
        problemDetail.setType(URI.create("/errors/file-too-large"));
        return problemDetail;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneralException(Exception ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro interno inesperado. Por favor, tente novamente mais tarde."
        );
        problemDetail.setTitle("Erro interno do servidor");
        problemDetail.setType(URI.create("/errors/internal"));
        return problemDetail;
    }
}
