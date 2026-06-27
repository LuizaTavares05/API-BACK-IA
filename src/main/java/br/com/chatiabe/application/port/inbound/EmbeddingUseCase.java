package br.com.chatiabe.application.port.inbound;

import java.util.List;

public interface EmbeddingUseCase {
    float[] generateEmbedding(String text);
    List<float[]> generateEmbeddings(List<String> texts);
}
