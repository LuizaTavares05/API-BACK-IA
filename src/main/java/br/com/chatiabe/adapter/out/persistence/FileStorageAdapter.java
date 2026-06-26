package br.com.chatiabe.adapter.out.persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import br.com.chatiabe.application.port.outbound.FileStorageService;
import br.com.chatiabe.domain.model.FileAttachment;
import jakarta.annotation.PostConstruct;

@Repository
public class FileStorageAdapter implements FileStorageService {

    private final ConcurrentHashMap<UUID, FileAttachment> metadataStore = new ConcurrentHashMap<>();
    private final Path uploadPath;

    public FileStorageAdapter(@Value("${app.upload.dir:uploads}") String uploadDir) {
        this.uploadPath = Path.of(uploadDir);
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory: " + uploadPath, e);
        }
    }

    @Override
    public FileAttachment save(FileAttachment attachment, byte[] content) {
        try {
            Path filePath = uploadPath.resolve(attachment.getId().toString());
            Files.write(filePath, content);
            metadataStore.put(attachment.getId(), attachment);
            return attachment;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + attachment.getFileName(), e);
        }
    }

    @Override
    public Optional<FileAttachment> findById(UUID fileId) {
        return Optional.ofNullable(metadataStore.get(fileId));
    }

    @Override
    public Optional<byte[]> getContent(UUID fileId) {
        try {
            Path filePath = uploadPath.resolve(fileId.toString());
            if (Files.exists(filePath)) {
                return Optional.of(Files.readAllBytes(filePath));
            }
            return Optional.empty();
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    @Override
    public void deleteById(UUID fileId) {
        try {
            Files.deleteIfExists(uploadPath.resolve(fileId.toString()));
            metadataStore.remove(fileId);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + fileId, e);
        }
    }
}
