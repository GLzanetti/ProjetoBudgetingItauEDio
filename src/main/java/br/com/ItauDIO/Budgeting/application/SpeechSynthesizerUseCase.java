package br.com.ItauDIO.Budgeting.application;

import br.com.ItauDIO.Budgeting.infrastructure.gemini.GeminiSpeechSynthesizer;
import org.springframework.stereotype.Service;

@Service
public class SpeechSynthesizerUseCase{
    private final GeminiSpeechSynthesizer geminiSpeechSynthesizer;
    public SpeechSynthesizerUseCase(GeminiSpeechSynthesizer geminiSpeechSynthesizer) {
        this.geminiSpeechSynthesizer = geminiSpeechSynthesizer;
    }

    public byte[] synthesize(String text){
        return geminiSpeechSynthesizer.synthesize(text).audio();
    }
}