package br.com.chatiabe.adapter.out.persistence.mapper;

import br.com.chatiabe.adapter.out.persistence.entity.DocumentChunkEntity;
import br.com.chatiabe.domain.model.DocumentChunk;

public class DocumentChunkMapper {

    public static DocumentChunkEntity toEntity(DocumentChunk domain) {
        return new DocumentChunkEntity(
                domain.getId(),
                domain.getDocumentId(),
                domain.getChunkIndex(),
                domain.getContent(),
                domain.getEmbedding(),
                domain.getMetadata(),
                domain.getStatus(),
                domain.getCreatedAt()
        );
    }

    public static DocumentChunk toDomain(DocumentChunkEntity entity) {
        return new DocumentChunk(
                entity.getId(),
                entity.getDocumentId(),
                entity.getChunkIndex(),
                entity.getContent(),
                entity.getEmbedding(),
                entity.getMetadata(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}
