package br.com.chatiabe.application.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;

import br.com.chatiabe.application.dto.FileUploadResponse;
import br.com.chatiabe.application.port.inbound.FileUploadUseCase;
import br.com.chatiabe.application.port.outbound.FileStorageService;
import br.com.chatiabe.domain.exception.FileSizeExceededException;
import br.com.chatiabe.domain.exception.UnsupportedFileFormatException;
import br.com.chatiabe.domain.model.FileAttachment;

@Service
public class FileUploadService implements FileUploadUseCase {

    private static final long MAX_FILE_SIZE = 5_242_880L;
    private static final java.util.Set<String> ALLOWED_EXTENSIONS = java.util.Set.of(".txt", ".pdf");

    private final FileStorageService fileStorageService;

    public FileUploadService(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @Override
    public FileUploadResponse uploadFile(String fileName, long fileSize, byte[] content) {
        String extension = getExtension(fileName);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new UnsupportedFileFormatException("Only .txt and .pdf files are accepted");
        }

        if (fileSize > MAX_FILE_SIZE) {
            throw new FileSizeExceededException("File exceeds maximum size of 5MB");
        }

        var attachment = new FileAttachment(
                UUID.randomUUID(),
                fileName,
                fileSize,
                null,
                Instant.now()
        );

        var saved = fileStorageService.save(attachment, content);
        return new FileUploadResponse(saved.getId(), saved.getFileName(), saved.getFileSize(), saved.getUploadedAt());
    }

    @Override
    public byte[] downloadFile(UUID fileId) {
        return fileStorageService.getContent(fileId)
                .orElseThrow(() -> new IllegalArgumentException("File not found: " + fileId));
    }

    private String getExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return dotIndex == -1 ? "" : fileName.substring(dotIndex).toLowerCase();
    }
}
