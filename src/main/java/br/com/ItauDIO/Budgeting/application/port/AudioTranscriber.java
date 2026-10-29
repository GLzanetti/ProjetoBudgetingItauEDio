package br.com.ItauDIO.Budgeting.application.port;

import org.springframework.core.io.Resource;

public interface AudioTranscriber {
    String transcribe(Resource audio);
}
