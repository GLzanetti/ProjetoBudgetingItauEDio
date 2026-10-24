package br.com.ItauDIO.Budgeting;

import com.google.genai.Client;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "GOOGLE_API_KEY", matches = ".+")
public class GoogleGenAIChatModelTests {

    Client genAiClient = Client.builder()
            .apiKey(System.getenv("GOOGLE_API_KEY")) // o nome da sua variável de ambiente
            .build();

    @Test
    public void shouldRecieveResponseWhenChatModelIsCalled(){
        var chatModel = GoogleGenAiChatModel.builder()
                .genAiClient(genAiClient)
                .options(GoogleGenAiChatOptions.builder()
                        .model("gemini-3.5-flash")
                        .temperature(0.8)
                        .responseMimeType("text/plain")
                        .build())
                .build();

        var response = chatModel.call("Gere um registro de bugeting, com descrição de gasto, valor em reais e local.");

        assertThat(response).isNotEmpty();
        System.out.println(response);
    }
}
