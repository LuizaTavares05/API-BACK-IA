package br.com.chatiabe.application.service;

import br.com.chatiabe.application.port.inbound.EmbeddingUseCase;
import br.com.chatiabe.application.port.outbound.EmbeddingProvider;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EmbeddingService implements EmbeddingUseCase {

    private final EmbeddingProvider embeddingProvider;

    public EmbeddingService(EmbeddingProvider embeddingProvider) {
        this.embeddingProvider = embeddingProvider;
    }

    @Override
    public float[] generateEmbedding(String text) {
        return embeddingProvider.generateEmbedding(text);
    }

    @Override
    public List<float[]> generateEmbeddings(List<String> texts) {
        return embeddingProvider.generateEmbeddings(texts);
    }
}
