package br.com.chatiabe.application.port.outbound;

import java.util.Optional;
import java.util.UUID;

import br.com.chatiabe.domain.model.FileAttachment;

public interface FileStorageService {

    FileAttachment save(FileAttachment attachment, byte[] content);

    Optional<FileAttachment> findById(UUID fileId);

    Optional<byte[]> getContent(UUID fileId);

    void deleteById(UUID fileId);
}
