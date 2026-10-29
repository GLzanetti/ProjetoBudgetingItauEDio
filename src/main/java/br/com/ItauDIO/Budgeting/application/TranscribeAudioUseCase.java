package br.com.ItauDIO.Budgeting.application;

import br.com.ItauDIO.Budgeting.infrastructure.gemini.GeminiAudioTranscriber;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class TranscribeAudioUseCase {
    private final GeminiAudioTranscriber transcriptionModel;

    public TranscribeAudioUseCase(GeminiAudioTranscriber transcriptionModel) {
        this.transcriptionModel = transcriptionModel;
    }

    public String transcribe(Resource audio) {
        return transcriptionModel.transcribe(audio);
    }
}
