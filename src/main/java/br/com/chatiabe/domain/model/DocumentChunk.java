package br.com.chatiabe.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class DocumentChunk {

    private UUID id;
    private UUID documentId;
    private int chunkIndex;
    private String content;
    private float[] embedding;
    private String metadata;
    private String status;
    private LocalDateTime createdAt;

    public DocumentChunk(UUID id, UUID documentId, int chunkIndex, String content,
                         float[] embedding, String metadata, String status, LocalDateTime createdAt) {
        this.id = id;
        this.documentId = documentId;
        this.chunkIndex = chunkIndex;
        this.content = content;
        this.embedding = embedding;
        this.metadata = metadata;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static DocumentChunk create(UUID documentId, int chunkIndex, String content, float[] embedding) {
        return new DocumentChunk(
                UUID.randomUUID(),
                documentId,
                chunkIndex,
                content,
                embedding,
                null,
                "ACTIVE",
                LocalDateTime.now()
        );
    }

    public UUID getId() { return id; }
    public UUID getDocumentId() { return documentId; }
    public int getChunkIndex() { return chunkIndex; }
    public String getContent() { return content; }
    public float[] getEmbedding() { return embedding; }
    public String getMetadata() { return metadata; }
    public String getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
