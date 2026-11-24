package br.com.ItauDIO.Budgeting.infrastructure.http;

import br.com.ItauDIO.Budgeting.application.ListTransactionsByCategoryUseCase;
import br.com.ItauDIO.Budgeting.application.PersistTransactionUseCase;
import br.com.ItauDIO.Budgeting.application.ProcessVoiceCommandUseCase;
import br.com.ItauDIO.Budgeting.application.input.PersistTransactionInput;
import br.com.ItauDIO.Budgeting.domain.exception.InvalidTransactionException;
import br.com.ItauDIO.Budgeting.infrastructure.exception.AIServiceException;
import br.com.ItauDIO.Budgeting.infrastructure.exception.AudioProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TransactionControllerErrorHandlingTest {

    private MockMvc mockMvc;

    @Mock
    private PersistTransactionUseCase persistTransactionUseCase;

    @Mock
    private ListTransactionsByCategoryUseCase listTransactionsByCategoryUseCase;

    @Mock
    private ProcessVoiceCommandUseCase processVoiceCommandUseCase;

    @BeforeEach
    void setUp() {
        TransactionController controller = new TransactionController(
                persistTransactionUseCase,
                listTransactionsByCategoryUseCase,
                processVoiceCommandUseCase
        );

        this.mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Deve retornar ProblemDetail quando categoria na rota for inválida")
    void deveRetornarProblemDetailQuandoCategoriaForInvalida() throws Exception {
        mockMvc.perform(get("/transactions/CATEGORIA_INEXISTENTE"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("/errors/invalid-parameter"))
                .andExpect(jsonPath("$.title").value("Parâmetro de requisição inválido"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detail").value("A categoria 'CATEGORIA_INEXISTENTE' não é reconhecida. Categorias disponíveis: GROCERIES, PHARMA, AUTO."));
    }

    @Test
    @DisplayName("Deve retornar ProblemDetail quando JSON for malformado")
    void deveRetornarProblemDetailQuandoJsonMalformado() throws Exception {
        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid json"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("/errors/malformed-request"))
                .andExpect(jsonPath("$.title").value("Requisição malformada"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("Deve retornar ProblemDetail 400 quando use case lançar InvalidTransactionException")
    void deveRetornarProblemDetailQuandoInvalidTransactionExceptionLancada() throws Exception {
        when(persistTransactionUseCase.execute(any(PersistTransactionInput.class)))
                .thenThrow(new InvalidTransactionException("Valor da transação não pode ser negativo"));

        String jsonRequest = """
                {
                    "description": "Lanche",
                    "category": "GROCERIES",
                    "amount": -500
                }
                """;

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.type").value("/errors/invalid-transaction"))
                .andExpect(jsonPath("$.title").value("Transação inválida"))
                .andExpect(jsonPath("$.detail").value("Valor da transação não pode ser negativo"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("Deve retornar ProblemDetail 422 quando transcrição falhar por processamento de áudio")
    void deveRetornarProblemDetailQuandoAudioProcessingFalhar() throws Exception {
        MockMultipartFile audioFile = new MockMultipartFile(
                "file",
                "audio.corrompido",
                "application/octet-stream",
                new byte[]{1, 2, 3}
        );

        when(processVoiceCommandUseCase.execute(any()))
                .thenThrow(new AudioProcessingException("Formato de áudio não suportado"));

        mockMvc.perform(multipart("/transactions/ai").file(audioFile))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.type").value("/errors/audio-processing"))
                .andExpect(jsonPath("$.title").value("Erro no processamento de áudio"))
                .andExpect(jsonPath("$.detail").value("Formato de áudio não suportado"))
                .andExpect(jsonPath("$.status").value(422));
    }

    @Test
    @DisplayName("Deve retornar ProblemDetail 503 quando Gemini estiver fora do ar")
    void deveRetornarProblemDetailQuandoAIServiceFalhar() throws Exception {
        MockMultipartFile audioFile = new MockMultipartFile(
                "file",
                "audio.wav",
                "audio/wav",
                new byte[]{1, 2, 3}
        );

        when(processVoiceCommandUseCase.execute(any()))
                .thenThrow(new AIServiceException("Gemini fora do ar temporariamente"));

        mockMvc.perform(multipart("/transactions/ai").file(audioFile))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.type").value("/errors/ai-service-unavailable"))
                .andExpect(jsonPath("$.title").value("Serviço de IA indisponível"))
                .andExpect(jsonPath("$.detail").value("Gemini fora do ar temporariamente"))
                .andExpect(jsonPath("$.status").value(503));
    }

    @Test
    @DisplayName("Deve retornar ProblemDetail 500 sem stack trace para exceção inesperada")
    void deveRetornarProblemDetailQuandoExcecaoInesperada() throws Exception {
        when(persistTransactionUseCase.execute(any(PersistTransactionInput.class)))
                .thenThrow(new RuntimeException("Falha catastrófica no banco"));

        String jsonRequest = """
                {
                    "description": "Lanche",
                    "category": "GROCERIES",
                    "amount": 1000
                }
                """;

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.type").value("/errors/internal"))
                .andExpect(jsonPath("$.title").value("Erro interno do servidor"))
                .andExpect(jsonPath("$.detail").value("Ocorreu um erro interno inesperado. Por favor, tente novamente mais tarde."))
                .andExpect(jsonPath("$.status").value(500));
    }
}
