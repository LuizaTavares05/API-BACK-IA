package br.com.chatiabe.adapter.out.persistence.mapper;

import br.com.chatiabe.adapter.out.persistence.entity.DocumentEntity;
import br.com.chatiabe.domain.model.Document;

public class DocumentMapper {

    public static DocumentEntity toEntity(Document domain) {
        return new DocumentEntity(
                domain.getId(),
                domain.getUserId(),
                domain.getFileName(),
                domain.getFileSize(),
                domain.getMimeType(),
                domain.getStatus(),
                domain.getChunkCount(),
                domain.getErrorMessage(),
                domain.getCreatedAt(),
                domain.getCompletedAt()
        );
    }

    public static Document toDomain(DocumentEntity entity) {
        return new Document(
                entity.getId(),
                entity.getUserId(),
                entity.getFileName(),
                entity.getFileSize(),
                entity.getMimeType(),
                entity.getStatus(),
                entity.getChunkCount(),
                entity.getErrorMessage(),
                entity.getCreatedAt(),
                entity.getCompletedAt()
        );
    }
}
