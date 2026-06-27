package br.com.chatiabe.application.port.outbound;

import java.util.List;

public interface EmbeddingProvider {
    float[] generateEmbedding(String text);
    List<float[]> generateEmbeddings(List<String> texts);
}
