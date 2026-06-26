package br.com.chatiabe.application.service;

import br.com.chatiabe.application.dto.FileUploadResponse;
import br.com.chatiabe.application.port.inbound.FileUploadUseCase;
import br.com.chatiabe.application.port.outbound.FileStorageService;
import br.com.chatiabe.domain.exception.FileSizeExceededException;
import br.com.chatiabe.domain.exception.UnsupportedFileFormatException;
import br.com.chatiabe.domain.model.FileAttachment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileUploadService implements FileUploadUseCase {

    private static final long MAX_FILE_SIZE = 5_242_880L;

    private final FileStorageService fileStorageService;

    public FileUploadService(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @Override
    public FileUploadResponse uploadFile(MultipartFile file, UUID userId) {
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("File name is required");
        }

        String extension = getExtension(fileName);
        if (!"txt".equalsIgnoreCase(extension) && !"pdf".equalsIgnoreCase(extension)) {
            throw new UnsupportedFileFormatException(
                    "Unsupported file format: ." + extension + ". Only .txt and .pdf files are accepted"
            );
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new FileSizeExceededException(
                    "File size exceeds the maximum limit of 5MB (5.242.880 bytes)"
            );
        }

        try {
            FileAttachment attachment = FileAttachment.create(userId, fileName, file.getSize(), file.getBytes());
            FileAttachment saved = fileStorageService.save(attachment);

            return new FileUploadResponse(
                    saved.getId(),
                    saved.getFileName(),
                    saved.getFileSize(),
                    saved.getUploadedAt()
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to read uploaded file", e);
        }
    }

    @Override
    public byte[] downloadFile(UUID fileId, UUID userId) {
        if (!fileStorageService.existsByIdAndUserId(fileId, userId)) {
            throw new IllegalArgumentException("File not found");
        }

        FileAttachment attachment = fileStorageService.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));

        return attachment.getData();
    }

    private String getExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1 || dotIndex == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(dotIndex + 1);
    }
}
