package br.com.chatiabe.application.port.outbound;

import br.com.chatiabe.domain.model.DocumentChunk;
import java.util.List;
import java.util.UUID;

public interface DocumentChunkRepository {
    DocumentChunk save(DocumentChunk chunk);
    List<DocumentChunk> saveAll(List<DocumentChunk> chunks);
    List<SimilarityResult> findSimilar(float[] queryVector, int topK, double minSimilarity, UUID userId);

    record SimilarityResult(
            DocumentChunk chunk,
            String documentName,
            double score
    ) {}
}
