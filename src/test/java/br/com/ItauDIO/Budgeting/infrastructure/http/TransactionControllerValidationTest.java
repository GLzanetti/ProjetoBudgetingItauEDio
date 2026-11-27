package br.com.ItauDIO.Budgeting.infrastructure.http;

import br.com.ItauDIO.Budgeting.application.ListTransactionsByCategoryUseCase;
import br.com.ItauDIO.Budgeting.application.PersistTransactionUseCase;
import br.com.ItauDIO.Budgeting.application.ProcessVoiceCommandUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TransactionControllerValidationTest {

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
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /transactions deve retornar 400 quando descrição estiver em branco")
    void deveRetornar400QuandoDescricaoEmBranco() throws Exception {
        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "description": "   ",
                                  "category": "GROCERIES",
                                  "amount": 5000
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.type").value("/errors/validation"));
    }

    @Test
    @DisplayName("POST /transactions deve retornar 400 quando valor for zero ou negativo")
    void deveRetornar400QuandoValorInvalido() throws Exception {
        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "description": "Compra no mercado",
                                  "category": "GROCERIES",
                                  "amount": 0
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.type").value("/errors/validation"));
    }

    @Test
    @DisplayName("POST /transactions deve retornar 400 quando categoria for nula")
    void deveRetornar400QuandoCategoriaNula() throws Exception {
        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "description": "Compra no mercado",
                                  "category": null,
                                  "amount": 5000
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.type").value("/errors/validation"));
    }
}
