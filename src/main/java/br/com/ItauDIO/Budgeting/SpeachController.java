package br.com.ItauDIO.Budgeting;

import com.google.genai.Client;
import com.google.genai.types.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api")
public class SpeachController {

    private static final String MODEL = "gemini-3.8-flash-lite-tts";
    private final Client client;

    public SpeachController(Client client) {
        this.client = client;
    }

    @PostMapping(value = "/speech", produces = "audio/wav")
    public ResponseEntity<byte[]> shouldProduceAudioWhenTextIsProvided(@RequestBody SpeechRequest request) throws Exception{
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

        GenerateContentResponse response = client.models.generateContent(
                MODEL,
                request.texto(),
                config);

        var blob = response.candidates().get().get(0)
                .content().get().parts().get().get(0)
                .inlineData().get();

        byte[] audio = blob.data().get();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("audio/wav"))
                .body(audio);
    }

    record SpeechRequest(String texto){}
}
