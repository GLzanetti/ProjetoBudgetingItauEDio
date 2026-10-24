package br.com.ItauDIO.Budgeting;

import com.google.genai.Client;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.MimeType;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "GOOGLE_API_KEY", matches = ".+")
public class TranscriptionModelTests {

    @ParameterizedTest
    @CsvSource({
            "audio/audio1.mp3, 40 reais",
            "audio/audio3.mp3, 50 reais",
            "audio/audio4.mp3, 120 reais"
    })
    public void shouldTranscribeAudioWhenAudioIsSentAsMedia(String fileName, String expectedKeyWord) {
        Client genAiClient = Client.builder()
                .apiKey(System.getenv("GOOGLE_API_KEY")) // o nome da sua variável de ambiente
                .build();

        var chatModel = GoogleGenAiChatModel.builder()
                .genAiClient(genAiClient)
                .options(GoogleGenAiChatOptions.builder()
                        .model("gemini-3.5-flash")
                        .temperature(0.3)
                        .build())
                .build();

        var audio = new ClassPathResource(fileName);

        var userMessage = UserMessage.builder()
                .text("Transcreva fielmente este áudio, ele está em Portugues do Brasil, nos audios apareceram valores em dinheiro no formato número do valor + 'reais', não quero explicações extras.")
                .media(List.of(new Media(new MimeType("audio", "mpeg"), audio)))
                .build();

        var response = chatModel.call(new Prompt(List.of(userMessage)));
        var transcricao = response.getResult().getOutput().getText();

        assertThat(transcricao.toLowerCase()).contains(expectedKeyWord.toLowerCase());
        System.out.println(transcricao);
    }
}
