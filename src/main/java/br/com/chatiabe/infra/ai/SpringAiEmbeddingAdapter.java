package br.com.chatiabe.infra.ai;

import br.com.chatiabe.application.port.outbound.EmbeddingProvider;
import br.com.chatiabe.domain.exception.EmbeddingProviderException;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class SpringAiEmbeddingAdapter implements EmbeddingProvider {

    private final EmbeddingModel embeddingModel;

    public SpringAiEmbeddingAdapter(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    @Override
    public float[] generateEmbedding(String text) {
        try {
            List<Double> vector = embeddingModel.embed(text);
            float[] result = new float[vector.size()];
            for (int i = 0; i < vector.size(); i++) {
                result[i] = vector.get(i).floatValue();
            }
            return result;
        } catch (Exception e) {
            throw new EmbeddingProviderException("Failed to generate embedding: " + e.getMessage(), e);
        }
    }

    @Override
    public List<float[]> generateEmbeddings(List<String> texts) {
        try {
            return texts.stream()
                    .map(this::generateEmbedding)
                    .toList();
        } catch (Exception e) {
            throw new EmbeddingProviderException("Failed to generate embeddings in batch: " + e.getMessage(), e);
        }
    }
}
