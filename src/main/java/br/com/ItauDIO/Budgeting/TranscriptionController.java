package br.com.ItauDIO.Budgeting;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.http.MediaType;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TranscriptionController {

    private final GoogleGenAiChatModel genAiChatModel;


    public TranscriptionController(GoogleGenAiChatModel genAiChatModel) {
        this.genAiChatModel = genAiChatModel;
    }

    @PostMapping(value = "/transcribe", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String transcribe(@RequestParam("file") MultipartFile file){
        var resource = file.getResource();

        var userMessage = UserMessage.builder()
                .text("Transcreva fielmente este áudio, ele está em Portugues do Brasil, nos audios apareceram valores em dinheiro no formato número do valor + 'reais', não quero explicações extras.")
                .media(List.of(new Media(new MimeType("audio", "mpeg"), resource)))
                .build();

        var result = genAiChatModel.call(new Prompt(List.of(userMessage)));

        return result.getResult().getOutput().getText();
    }
}
