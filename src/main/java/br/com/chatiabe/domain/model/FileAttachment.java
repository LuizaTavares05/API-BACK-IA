package br.com.chatiabe.domain.model;

import java.time.Instant;
import java.util.UUID;

public class FileAttachment {

    private UUID id;
    private String fileName;
    private Long fileSize;
    private UUID chatSessionId;
    private Instant uploadedAt;

    public FileAttachment() {
    }

    public FileAttachment(UUID id, String fileName, Long fileSize, UUID chatSessionId, Instant uploadedAt) {
        this.id = id;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.chatSessionId = chatSessionId;
        this.uploadedAt = uploadedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public UUID getChatSessionId() {
        return chatSessionId;
    }

    public void setChatSessionId(UUID chatSessionId) {
        this.chatSessionId = chatSessionId;
    }

    public Instant getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(Instant uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
