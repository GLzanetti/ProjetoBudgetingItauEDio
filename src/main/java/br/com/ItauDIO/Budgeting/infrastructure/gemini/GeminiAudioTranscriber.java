package br.com.ItauDIO.Budgeting.infrastructure.gemini;

import br.com.ItauDIO.Budgeting.application.port.AudioTranscriber;
import br.com.ItauDIO.Budgeting.infrastructure.exception.AIServiceException;
import br.com.ItauDIO.Budgeting.infrastructure.exception.AudioProcessingException;
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
                .orElseThrow(() -> new AudioProcessingException("Formato de audio não reconhecido: " + audio.getFilename()));

        try {
            var userMessage = UserMessage.builder().text(prompt).media(List.of(new Media(mimeType, audio))).build();

            var response = genAiChatModel.call(new Prompt(List.of(userMessage)));
            String text = response.getResult().getOutput().getText();

            if (text == null || text.isBlank()) {
                throw new AudioProcessingException("A transcrição do áudio retornou vazia.");
            }

            return text;
        } catch (AudioProcessingException e) {
            throw e;
        } catch (Exception e) {
            throw new AIServiceException("Falha ao transcrever áudio via Gemini.", e);
        }
    }
}
