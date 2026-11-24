package br.com.ItauDIO.Budgeting.application;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class ProcessVoiceCommandUseCase {

    private final TranscribeAudioUseCase transcribeAudioUseCase;
    private final ChatClient chatClient;
    private final SpeechSynthesizerUseCase speechSynthesizerUseCase;

    public ProcessVoiceCommandUseCase(TranscribeAudioUseCase transcribeAudioUseCase,
                                      ChatClient chatClient,
                                      SpeechSynthesizerUseCase speechSynthesizerUseCase) {
        this.transcribeAudioUseCase = transcribeAudioUseCase;
        this.chatClient = chatClient;
        this.speechSynthesizerUseCase = speechSynthesizerUseCase;
    }

    public byte[] execute(Resource audioResource) {
        String text = transcribeAudioUseCase.transcribe(audioResource);
        String result = chatClient.prompt().user(text).call().content();
        return speechSynthesizerUseCase.synthesize(result);
    }
}
