package br.com.chatiabe.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class FileAttachment {

    private UUID id;
    private UUID userId;
    private String fileName;
    private long fileSize;
    private byte[] data;
    private LocalDateTime uploadedAt;

    public FileAttachment(UUID id, UUID userId, String fileName, long fileSize, byte[] data, LocalDateTime uploadedAt) {
        this.id = id;
        this.userId = userId;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.data = data;
        this.uploadedAt = uploadedAt;
    }

    public static FileAttachment create(UUID userId, String fileName, long fileSize, byte[] data) {
        return new FileAttachment(
                UUID.randomUUID(),
                userId,
                fileName,
                fileSize,
                data,
                LocalDateTime.now()
        );
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public String getFileName() { return fileName; }
    public long getFileSize() { return fileSize; }
    public byte[] getData() { return data; }
    public LocalDateTime getUploadedAt() { return uploadedAt; }
}
