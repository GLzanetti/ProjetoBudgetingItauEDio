package br.com.ItauDIO.Budgeting.application.port;

import br.com.ItauDIO.Budgeting.application.output.SpeechOutput;

public interface SpeechSynthesizer {
    SpeechOutput synthesize(String texto);
}
