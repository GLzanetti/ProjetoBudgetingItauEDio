package br.com.ItauDIO.Budgeting.application;

import br.com.ItauDIO.Budgeting.infrastructure.exception.AIServiceException;
import br.com.ItauDIO.Budgeting.infrastructure.exception.AudioProcessingException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcessVoiceCommandUseCaseTest {

    @Mock
    private TranscribeAudioUseCase transcribeAudioUseCase;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private ChatClient chatClient;

    @Mock
    private SpeechSynthesizerUseCase speechSynthesizerUseCase;

    @InjectMocks
    private ProcessVoiceCommandUseCase processVoiceCommandUseCase;

    @Test
    @DisplayName("Deve orquestrar transcrição, prompt da IA e síntese de voz")
    void deveOrquestrarComandoDeVozComSucesso() {
        Resource audioResource = new ByteArrayResource(new byte[]{1, 2, 3});
        byte[] expectedAudio = new byte[]{10, 20, 30};

        when(transcribeAudioUseCase.transcribe(audioResource)).thenReturn("Gastei 50 no mercado");
        when(chatClient.prompt().user("Gastei 50 no mercado").call().content()).thenReturn("Transação registrada.");
        when(speechSynthesizerUseCase.synthesize("Transação registrada.")).thenReturn(expectedAudio);

        byte[] result = processVoiceCommandUseCase.execute(audioResource);

        assertArrayEquals(expectedAudio, result);
        verify(transcribeAudioUseCase).transcribe(audioResource);
        verify(speechSynthesizerUseCase).synthesize("Transação registrada.");
    }

    @Test
    @DisplayName("Deve propagar exceção quando transcrição falhar")
    void devePropagarExcecaoQuandoTranscricaoFalhar() {
        Resource audioResource = new ByteArrayResource(new byte[]{1, 2, 3});

        when(transcribeAudioUseCase.transcribe(audioResource))
                .thenThrow(new AudioProcessingException("Formato inválido"));

        assertThrows(AudioProcessingException.class, () -> processVoiceCommandUseCase.execute(audioResource));
        verifyNoInteractions(chatClient, speechSynthesizerUseCase);
    }

    @Test
    @DisplayName("Deve propagar exceção quando serviço de IA falhar")
    void devePropagarExcecaoQuandoServicoDeIaFalhar() {
        Resource audioResource = new ByteArrayResource(new byte[]{1, 2, 3});

        when(transcribeAudioUseCase.transcribe(audioResource)).thenReturn("Quanto gastei?");
        when(chatClient.prompt().user("Quanto gastei?").call().content())
                .thenThrow(new AIServiceException("Serviço indisponível"));

        assertThrows(AIServiceException.class, () -> processVoiceCommandUseCase.execute(audioResource));
        verifyNoInteractions(speechSynthesizerUseCase);
    }
}
