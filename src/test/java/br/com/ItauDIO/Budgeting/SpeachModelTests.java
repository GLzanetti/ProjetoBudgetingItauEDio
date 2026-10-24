package br.com.ItauDIO.Budgeting;

import com.google.genai.Client;
import com.google.genai.types.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "GOOGLE_API_KEY", matches = ".+")
public class SpeachModelTests {

    Client genAiClient = Client.builder()
            .apiKey(System.getenv("GOOGLE_API_KEY")) // o nome da sua variável de ambiente
            .build();

    @Test
    public void shouldProduceAudioWhenTextIsProvided() throws Exception{
        var config = GenerateContentConfig.builder()
                .responseModalities(List.of("AUDIO"))
                .speechConfig(SpeechConfig.builder()
                        .voiceConfig(VoiceConfig.builder()
                                .prebuiltVoiceConfig(PrebuiltVoiceConfig.builder()
                                        .voiceName("Kore")
                                        .build())
                                .build())
                        .build())
                .build();

        GenerateContentResponse response = genAiClient.models.generateContent(
                "gemini-3.8-flash-lite-tts",
                "Olá! Este é um teste de voz.",
                config);

        var blob = response.candidates().get().get(0)
                .content().get().parts().get().get(0)
                .inlineData().get();

        System.out.println("Mime: " + blob.mimeType().orElse("?"));
        Files.write(Path.of("out.wav"), blob.data().get());
    }
}
