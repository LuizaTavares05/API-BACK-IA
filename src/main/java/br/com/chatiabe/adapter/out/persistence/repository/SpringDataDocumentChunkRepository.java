package br.com.chatiabe.adapter.out.persistence.repository;

import br.com.chatiabe.adapter.out.persistence.entity.DocumentChunkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;

public interface SpringDataDocumentChunkRepository extends JpaRepository<DocumentChunkEntity, UUID> {

    @Query(value = """
            SELECT dc.id, dc.document_id, dc.chunk_index, dc.content,
                   dc.embedding, dc.metadata, dc.status, dc.created_at,
                   d.file_name AS document_name,
                   1 - (dc.embedding <=> CAST(:queryVector AS vector)) AS similarity_score
            FROM document_chunks dc
            JOIN documents d ON d.id = dc.document_id
            WHERE d.user_id = :userId
              AND dc.status = 'ACTIVE'
              AND 1 - (dc.embedding <=> CAST(:queryVector AS vector)) >= :minSimilarity
            ORDER BY similarity_score DESC
            LIMIT :topK
            """, nativeQuery = true)
    List<Object[]> findSimilar(@Param("queryVector") String queryVector,
                               @Param("topK") int topK,
                               @Param("minSimilarity") double minSimilarity,
                               @Param("userId") UUID userId);
}
