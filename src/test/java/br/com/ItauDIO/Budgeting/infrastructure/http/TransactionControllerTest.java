package br.com.ItauDIO.Budgeting.infrastructure.http;

import br.com.ItauDIO.Budgeting.application.ListTransactionsByCategoryUseCase;
import br.com.ItauDIO.Budgeting.application.PersistTransactionUseCase;
import br.com.ItauDIO.Budgeting.application.ProcessVoiceCommandUseCase;
import br.com.ItauDIO.Budgeting.application.input.PersistTransactionInput;
import br.com.ItauDIO.Budgeting.application.output.TransactionOutput;
import br.com.ItauDIO.Budgeting.domain.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

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

        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    @DisplayName("Deve criar transação e retornar 201 Created com payload mapeado")
    void deveCriarTransacaoERetornar201Created() throws Exception {
        TransactionOutput output = new TransactionOutput("uuid-123", "Lanche", "GROCERIES", new BigDecimal("35.00"));
        when(persistTransactionUseCase.execute(any(PersistTransactionInput.class))).thenReturn(output);

        String jsonRequest = """
                {
                    "description": "Lanche",
                    "category": "GROCERIES",
                    "amount": 3500
                }
                """;

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("uuid-123"))
                .andExpect(jsonPath("$.description").value("Lanche"))
                .andExpect(jsonPath("$.category").value("GROCERIES"))
                .andExpect(jsonPath("$.amount").value(35.00));

        verify(persistTransactionUseCase).execute(any(PersistTransactionInput.class));
    }

    @Test
    @DisplayName("Deve listar transações por categoria e retornar 200 OK com lista")
    void deveListarTransacoesPorCategoriaERetornar200Ok() throws Exception {
        TransactionOutput item1 = new TransactionOutput("uuid-1", "Supermercado", "GROCERIES", new BigDecimal("150.00"));
        TransactionOutput item2 = new TransactionOutput("uuid-2", "Padaria", "GROCERIES", new BigDecimal("25.00"));

        when(listTransactionsByCategoryUseCase.execute(Category.GROCERIES)).thenReturn(List.of(item1, item2));

        mockMvc.perform(get("/transactions/GROCERIES"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("uuid-1"))
                .andExpect(jsonPath("$[0].description").value("Supermercado"))
                .andExpect(jsonPath("$[0].category").value("GROCERIES"))
                .andExpect(jsonPath("$[0].amount").value(150.00))
                .andExpect(jsonPath("$[1].id").value("uuid-2"))
                .andExpect(jsonPath("$[1].description").value("Padaria"))
                .andExpect(jsonPath("$[1].category").value("GROCERIES"))
                .andExpect(jsonPath("$[1].amount").value(25.00));

        verify(listTransactionsByCategoryUseCase).execute(Category.GROCERIES);
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando categoria for inválida")
    void deveRetornar400BadRequestQuandoCategoriaForInvalida() throws Exception {
        mockMvc.perform(get("/transactions/CATEGORIA_INEXISTENTE"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve processar áudio com IA e retornar áudio WAV gerado")
    void deveProcessarAudioComIAERetornarAudioWav() throws Exception {
        MockMultipartFile audioFile = new MockMultipartFile(
                "file",
                "gravacao.wav",
                "audio/wav",
                new byte[]{1, 2, 3}
        );

        byte[] expectedAudioOutput = new byte[]{4, 5, 6};
        when(processVoiceCommandUseCase.execute(any())).thenReturn(expectedAudioOutput);

        MvcResult result = mockMvc.perform(multipart("/transactions/ai").file(audioFile))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "audio/wav"))
                .andReturn();

        assertArrayEquals(expectedAudioOutput, result.getResponse().getContentAsByteArray());
    }
}
