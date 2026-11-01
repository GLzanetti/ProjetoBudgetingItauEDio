package br.com.ItauDIO.Budgeting.application;

import br.com.ItauDIO.Budgeting.infrastructure.gemini.GeminiAudioTranscriber;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TranscribeAudioUseCaseTest {

    @Mock
    private GeminiAudioTranscriber geminiAudioTranscriber;

    @InjectMocks
    private TranscribeAudioUseCase transcribeAudioUseCase;

    @Test
    @DisplayName("Deve transcrever áudio com sucesso delegando para o adaptador")
    void deveTranscreverAudioComSucesso() {
        Resource audioResource = new ByteArrayResource(new byte[]{10, 20, 30});
        String transcricaoEsperada = "Gastei 20 reais no almoço";

        when(geminiAudioTranscriber.transcribe(audioResource)).thenReturn(transcricaoEsperada);

        String resultado = transcribeAudioUseCase.transcribe(audioResource);

        assertEquals(transcricaoEsperada, resultado);
        verify(geminiAudioTranscriber, times(1)).transcribe(audioResource);
    }

    @Test
    @DisplayName("Deve propagar exceção quando falhar a transcrição pelo adaptador")
    void devePropagarExcecaoQuandoFalharTranscricao() {
        Resource audioResource = new ByteArrayResource(new byte[]{0});
        when(geminiAudioTranscriber.transcribe(any(Resource.class)))
                .thenThrow(new IllegalArgumentException("Formato de audio não reconhecido"));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> transcribeAudioUseCase.transcribe(audioResource));

        assertEquals("Formato de audio não reconhecido", exception.getMessage());
        verify(geminiAudioTranscriber, times(1)).transcribe(audioResource);
    }
}
