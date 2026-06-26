package br.com.chatiabe.application.port.outbound;

import br.com.chatiabe.domain.model.FileAttachment;
import java.util.Optional;
import java.util.UUID;

public interface FileStorageService {
    FileAttachment save(FileAttachment attachment);
    Optional<FileAttachment> findById(UUID id);
    boolean existsByIdAndUserId(UUID id, UUID userId);
}
