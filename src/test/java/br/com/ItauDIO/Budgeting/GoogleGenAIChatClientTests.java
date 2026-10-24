package br.com.ItauDIO.Budgeting;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "GOOGLE_API_KEY", matches = ".+")
public class GoogleGenAIChatClientTests {

    @Autowired
    GoogleGenAiChatModel googleGenAiChatModel;

    @Test
    void shouldExecuteSumWhenPrompted(){
        var chatClient = ChatClient.builder(googleGenAiChatModel)
                .defaultSystem("Você é um matemático.")
                .build();

        var response = chatClient.prompt("Some 10 mais 20. Depois subtraia 30 do resultado anterior. Exiba somente o resultado final sem explicações.")
                .call().content();

        assertThat(response).contains("0");
        System.out.println(response);
    }
}
