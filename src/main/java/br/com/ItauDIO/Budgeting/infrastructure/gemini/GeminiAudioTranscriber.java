package br.com.ItauDIO.Budgeting.infrastructure.gemini;

import br.com.ItauDIO.Budgeting.application.port.AudioTranscriber;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;

import java.util.List;

@Component
public class GeminiAudioTranscriber implements AudioTranscriber {
    private final String prompt = "Transcreva fielmente este áudio, no mesmo idioma em que foi falado. "
            + "Não resuma, não traduza e não comente. Responda apenas com a transcrição.";

    private final GoogleGenAiChatModel genAiChatModel;

    public GeminiAudioTranscriber(GoogleGenAiChatModel genAiChatModel) {
        this.genAiChatModel = genAiChatModel;
    }

    @Override
    public String transcribe(Resource audio) {
        MimeType mimeType = MediaTypeFactory.getMediaType(audio)
                .orElseThrow(() -> new IllegalArgumentException("Formato de audio não reconhecido: " + audio.getFilename()));

        var userMessage = UserMessage.builder().text(prompt).media(List.of(new Media(mimeType, audio))).build();

        var response = genAiChatModel.call(new Prompt(List.of(userMessage)));
        String text = response.getResult().getOutput().getText();

        return text;
    }
}
