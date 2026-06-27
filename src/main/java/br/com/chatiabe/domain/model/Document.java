package br.com.chatiabe.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Document {

    private UUID id;
    private UUID userId;
    private String fileName;
    private long fileSize;
    private String mimeType;
    private String status;
    private int chunkCount;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    public Document(UUID id, UUID userId, String fileName, long fileSize, String mimeType,
                    String status, int chunkCount, String errorMessage,
                    LocalDateTime createdAt, LocalDateTime completedAt) {
        this.id = id;
        this.userId = userId;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.mimeType = mimeType;
        this.status = status;
        this.chunkCount = chunkCount;
        this.errorMessage = errorMessage;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
    }

    public static Document create(UUID userId, String fileName, long fileSize, String mimeType) {
        return new Document(
                UUID.randomUUID(),
                userId,
                fileName,
                fileSize,
                mimeType,
                DocumentStatus.PENDING.name(),
                0,
                null,
                LocalDateTime.now(),
                null
        );
    }

    public void markProcessing() {
        this.status = DocumentStatus.PROCESSING.name();
    }

    public void markCompleted(int chunkCount) {
        this.status = DocumentStatus.COMPLETED.name();
        this.chunkCount = chunkCount;
        this.completedAt = LocalDateTime.now();
    }

    public void markFailed(String errorMessage) {
        this.status = DocumentStatus.FAILED.name();
        this.errorMessage = errorMessage;
        this.completedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public String getFileName() { return fileName; }
    public long getFileSize() { return fileSize; }
    public String getMimeType() { return mimeType; }
    public String getStatus() { return status; }
    public int getChunkCount() { return chunkCount; }
    public String getErrorMessage() { return errorMessage; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
}
