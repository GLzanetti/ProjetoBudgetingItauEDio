package br.com.ItauDIO.Budgeting.infrastructure.http;

import br.com.ItauDIO.Budgeting.domain.Category;
import br.com.ItauDIO.Budgeting.domain.exception.InvalidCategoryException;
import br.com.ItauDIO.Budgeting.domain.exception.InvalidTransactionException;
import br.com.ItauDIO.Budgeting.domain.exception.TransactionNotFoundException;
import br.com.ItauDIO.Budgeting.infrastructure.exception.AIServiceException;
import br.com.ItauDIO.Budgeting.infrastructure.exception.AudioProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.lang.reflect.Method;
import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Deve tratar InvalidTransactionException com 400 Bad Request")
    void deveTratarInvalidTransactionException() {
        var ex = new InvalidTransactionException("O valor da transação não pode ser negativo");

        ProblemDetail pd = handler.handleInvalidTransaction(ex);

        assertEquals(HttpStatus.BAD_REQUEST.value(), pd.getStatus());
        assertEquals("Transação inválida", pd.getTitle());
        assertEquals("O valor da transação não pode ser negativo", pd.getDetail());
        assertEquals(URI.create("/errors/invalid-transaction"), pd.getType());
    }

    @Test
    @DisplayName("Deve tratar InvalidCategoryException com 400 Bad Request")
    void deveTratarInvalidCategoryException() {
        var ex = new InvalidCategoryException("Categoria informada não existe");

        ProblemDetail pd = handler.handleInvalidCategory(ex);

        assertEquals(HttpStatus.BAD_REQUEST.value(), pd.getStatus());
        assertEquals("Categoria inválida", pd.getTitle());
        assertEquals("Categoria informada não existe", pd.getDetail());
        assertEquals(URI.create("/errors/invalid-category"), pd.getType());
    }

    @Test
    @DisplayName("Deve tratar TransactionNotFoundException com 404 Not Found")
    void deveTratarTransactionNotFoundException() {
        var ex = new TransactionNotFoundException("Transação com ID 123 não encontrada");

        ProblemDetail pd = handler.handleTransactionNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND.value(), pd.getStatus());
        assertEquals("Transação não encontrada", pd.getTitle());
        assertEquals("Transação com ID 123 não encontrada", pd.getDetail());
        assertEquals(URI.create("/errors/transaction-not-found"), pd.getType());
    }

    @Test
    @DisplayName("Deve tratar AudioProcessingException com 422 Unprocessable Entity")
    void deveTratarAudioProcessingException() {
        var ex = new AudioProcessingException("Formato de áudio não suportado");

        ProblemDetail pd = handler.handleAudioProcessing(ex);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), pd.getStatus());
        assertEquals("Erro no processamento de áudio", pd.getTitle());
        assertEquals("Formato de áudio não suportado", pd.getDetail());
        assertEquals(URI.create("/errors/audio-processing"), pd.getType());
    }

    @Test
    @DisplayName("Deve tratar AIServiceException com 503 Service Unavailable")
    void deveTratarAIServiceException() {
        var ex = new AIServiceException("Gemini fora do ar temporariamente");

        ProblemDetail pd = handler.handleAIService(ex);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE.value(), pd.getStatus());
        assertEquals("Serviço de IA indisponível", pd.getTitle());
        assertEquals("Gemini fora do ar temporariamente", pd.getDetail());
        assertEquals(URI.create("/errors/ai-service-unavailable"), pd.getType());
    }

    @Test
    @DisplayName("Deve tratar MethodArgumentTypeMismatchException para Category com lista de opções válidas")
    void deveTratarMethodArgumentTypeMismatchParaCategoria() throws NoSuchMethodException {
        Method dummyMethod = this.getClass().getDeclaredMethod("dummyCategoryMethod", Category.class);
        MethodParameter parameter = new MethodParameter(dummyMethod, 0);

        var ex = new MethodArgumentTypeMismatchException("VALOR_INVALIDO", Category.class, "category", parameter, new IllegalArgumentException());

        ProblemDetail pd = handler.handleMethodArgumentTypeMismatch(ex);

        assertEquals(HttpStatus.BAD_REQUEST.value(), pd.getStatus());
        assertEquals("Parâmetro de requisição inválido", pd.getTitle());
        assertEquals(URI.create("/errors/invalid-parameter"), pd.getType());
        assertTrue(pd.getDetail().contains("A categoria 'VALOR_INVALIDO' não é reconhecida"));
        assertTrue(pd.getDetail().contains("GROCERIES"));
        assertTrue(pd.getDetail().contains("PHARMA"));
        assertTrue(pd.getDetail().contains("AUTO"));
    }

    @Test
    @DisplayName("Deve tratar HttpMessageNotReadableException com 400 Bad Request")
    void deveTratarHttpMessageNotReadableException() {
        var ex = new HttpMessageNotReadableException("JSON parse error", new MockHttpInputMessage(new byte[0]));

        ProblemDetail pd = handler.handleHttpMessageNotReadable(ex);

        assertEquals(HttpStatus.BAD_REQUEST.value(), pd.getStatus());
        assertEquals("Requisição malformada", pd.getTitle());
        assertEquals("Requisição malformada ou corpo JSON inválido.", pd.getDetail());
        assertEquals(URI.create("/errors/malformed-request"), pd.getType());
    }

    @Test
    @DisplayName("Deve tratar MethodArgumentNotValidException com 400 Bad Request e detalhes dos campos")
    void deveTratarMethodArgumentNotValidException() throws NoSuchMethodException {
        Method dummyMethod = this.getClass().getDeclaredMethod("dummyCategoryMethod", Category.class);
        MethodParameter parameter = new MethodParameter(dummyMethod, 0);

        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "target");
        bindingResult.addError(new FieldError("target", "amount", "deve ser maior que zero"));

        var ex = new MethodArgumentNotValidException(parameter, bindingResult);

        ProblemDetail pd = handler.handleMethodArgumentNotValid(ex);

        assertEquals(HttpStatus.BAD_REQUEST.value(), pd.getStatus());
        assertEquals("Erro de validação", pd.getTitle());
        assertEquals("amount: deve ser maior que zero", pd.getDetail());
        assertEquals(URI.create("/errors/validation"), pd.getType());
    }

    @Test
    @DisplayName("Deve tratar MaxUploadSizeExceededException com 413 Payload Too Large")
    void deveTratarMaxUploadSizeExceededException() {
        var ex = new MaxUploadSizeExceededException(1024L * 1024L);

        ProblemDetail pd = handler.handleMaxUploadSizeExceeded(ex);

        assertEquals(HttpStatus.PAYLOAD_TOO_LARGE.value(), pd.getStatus());
        assertEquals("Arquivo muito grande", pd.getTitle());
        assertEquals("O arquivo enviado excede o tamanho máximo permitido.", pd.getDetail());
        assertEquals(URI.create("/errors/file-too-large"), pd.getType());
    }

    @Test
    @DisplayName("Deve tratar Exception genérica com 500 Internal Server Error sem vazar detalhes técnicos")
    void deveTratarExceptionGenerica() {
        var ex = new NullPointerException("Erro de ponteiro nulo inesperado");

        ProblemDetail pd = handler.handleGeneralException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), pd.getStatus());
        assertEquals("Erro interno do servidor", pd.getTitle());
        assertEquals("Ocorreu um erro interno inesperado. Por favor, tente novamente mais tarde.", pd.getDetail());
        assertEquals(URI.create("/errors/internal"), pd.getType());
        assertFalse(pd.getDetail().contains("NullPointerException"));
    }

    @SuppressWarnings("unused")
    private void dummyCategoryMethod(Category category) {
    }
}

