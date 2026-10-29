package br.com.ItauDIO.Budgeting.infrastructure.gemini;

import br.com.ItauDIO.Budgeting.application.output.SpeechOutput;
import br.com.ItauDIO.Budgeting.application.port.SpeechSynthesizer;
import com.google.genai.Client;
import com.google.genai.types.*;
import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GeminiSpeechSynthesizer implements SpeechSynthesizer {

    private final Client genAiClient;
    private final String model;
    private final String voice;

    public GeminiSpeechSynthesizer(Client genAiClient,
                                    @Value("${app.speech.model}") String model,
                                    @Value("${app.speech.voice}") String voice) {
        this.genAiClient = genAiClient;
        this.model = model;
        this.voice = voice;
    }

    @Override
    public SpeechOutput synthesize(String text) {
        var config = GenerateContentConfig.builder()
                .responseModalities(List.of("AUDIO"))
                .speechConfig(SpeechConfig.builder()
                        .voiceConfig(VoiceConfig.builder()
                                .prebuiltVoiceConfig(PrebuiltVoiceConfig.builder()
                                        .voiceName(voice)
                                        .build())
                                .build())
                        .build())
                .build();

        GenerateContentResponse response = genAiClient.models.generateContent(model, text, config);

        var blob = response.candidates()
                .flatMap(c -> c.stream().findFirst())
                .flatMap(Candidate::content)
                .flatMap(Content::parts)
                .flatMap(p -> p.stream().findFirst())
                .flatMap(Part::inlineData)
                .orElseThrow(() -> new IllegalArgumentException("O gemini não devolveu áudio."));

        byte[] audio = blob.data().orElseThrow(() -> new IllegalArgumentException("O áudio veio vazio."));

        return new SpeechOutput(audio, blob.mimeType().orElse("audio/wav"));
    }
}
