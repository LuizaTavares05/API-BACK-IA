package br.com.chatiabe.adapter.out.persistence.adapter;

import br.com.chatiabe.adapter.out.persistence.entity.DocumentChunkEntity;
import br.com.chatiabe.adapter.out.persistence.mapper.DocumentChunkMapper;
import br.com.chatiabe.adapter.out.persistence.repository.SpringDataDocumentChunkRepository;
import br.com.chatiabe.application.port.outbound.DocumentChunkRepository;
import br.com.chatiabe.domain.model.DocumentChunk;
import org.postgresql.util.PGobject;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class DocumentChunkRepositoryAdapter implements DocumentChunkRepository {

    private final SpringDataDocumentChunkRepository springRepo;

    public DocumentChunkRepositoryAdapter(SpringDataDocumentChunkRepository springRepo) {
        this.springRepo = springRepo;
    }

    @Override
    public DocumentChunk save(DocumentChunk chunk) {
        return DocumentChunkMapper.toDomain(springRepo.save(DocumentChunkMapper.toEntity(chunk)));
    }

    @Override
    public List<DocumentChunk> saveAll(List<DocumentChunk> chunks) {
        List<DocumentChunkEntity> entities = chunks.stream()
                .map(DocumentChunkMapper::toEntity)
                .collect(Collectors.toList());
        return springRepo.saveAll(entities).stream()
                .map(DocumentChunkMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<SimilarityResult> findSimilar(float[] queryVector, int topK, double minSimilarity, UUID userId) {
        String vectorStr = Arrays.toString(queryVector)
                .replace(" ", "");

        List<Object[]> results = springRepo.findSimilar(vectorStr, topK, minSimilarity, userId);

        List<SimilarityResult> similarityResults = new ArrayList<>();
        for (Object[] row : results) {
            DocumentChunkEntity entity = new DocumentChunkEntity(
                    (UUID) row[0],
                    (UUID) row[1],
                    (int) row[2],
                    (String) row[3],
                    parsePgvector((PGobject) row[4]),
                    (String) row[5],
                    (String) row[6],
                    ((java.sql.Timestamp) row[7]).toLocalDateTime()
            );
            String documentName = (String) row[8];
            double score = (double) row[9];

            similarityResults.add(new SimilarityResult(
                    DocumentChunkMapper.toDomain(entity),
                    documentName,
                    score
            ));
        }
        return similarityResults;
    }

    private float[] parsePgvector(PGobject obj) {
        String val = obj.getValue();
        String trimmed = val.substring(1, val.length() - 1);
        if (trimmed.isEmpty()) return new float[0];
        String[] parts = trimmed.split(",");
        float[] result = new float[parts.length];
        for (int i = 0; i < parts.length; i++)
            result[i] = Float.parseFloat(parts[i].trim());
        return result;
    }
}
