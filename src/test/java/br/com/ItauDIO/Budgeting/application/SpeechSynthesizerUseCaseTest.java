package br.com.ItauDIO.Budgeting.application;

import br.com.ItauDIO.Budgeting.application.output.SpeechOutput;
import br.com.ItauDIO.Budgeting.infrastructure.gemini.GeminiSpeechSynthesizer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpeechSynthesizerUseCaseTest {

    @Mock
    private GeminiSpeechSynthesizer geminiSpeechSynthesizer;

    @InjectMocks
    private SpeechSynthesizerUseCase speechSynthesizerUseCase;

    @Test
    @DisplayName("Deve sintetizar texto em áudio delegando para o adaptador")
    void deveSintetizarTextoEmAudio() {
        String texto = "Sua despesa foi registrada com sucesso.";
        byte[] audioBytesEsperados = new byte[]{1, 2, 3, 4, 5};
        SpeechOutput speechOutput = new SpeechOutput(audioBytesEsperados, "audio/wav");

        when(geminiSpeechSynthesizer.synthesize(texto)).thenReturn(speechOutput);

        byte[] resultado = speechSynthesizerUseCase.synthesize(texto);

        assertArrayEquals(audioBytesEsperados, resultado);
        verify(geminiSpeechSynthesizer, times(1)).synthesize(texto);
    }

    @Test
    @DisplayName("Deve propagar exceção quando o adaptador do Gemini falhar na síntese")
    void devePropagarExcecaoQuandoGeminiFalharSintese() {
        String texto = "Texto para falha";
        when(geminiSpeechSynthesizer.synthesize(texto))
                .thenThrow(new IllegalArgumentException("O gemini não devolveu áudio."));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> speechSynthesizerUseCase.synthesize(texto));

        assertEquals("O gemini não devolveu áudio.", exception.getMessage());
        verify(geminiSpeechSynthesizer, times(1)).synthesize(texto);
    }
}
