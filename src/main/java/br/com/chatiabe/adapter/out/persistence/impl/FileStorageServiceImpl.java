package br.com.chatiabe.adapter.out.persistence.impl;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;

import br.com.chatiabe.application.port.outbound.FileStorageService;
import br.com.chatiabe.domain.model.FileAttachment;

@Repository
public class FileStorageServiceImpl implements FileStorageService {

    private final Map<UUID, FileAttachment> metadataStore = new ConcurrentHashMap<>();
    private final Map<UUID, byte[]> contentStore = new ConcurrentHashMap<>();

    @Override
    public FileAttachment save(FileAttachment attachment, byte[] content) {
        metadataStore.put(attachment.getId(), attachment);
        contentStore.put(attachment.getId(), content);
        return attachment;
    }

    @Override
    public Optional<FileAttachment> findById(UUID fileId) {
        return Optional.ofNullable(metadataStore.get(fileId));
    }

    @Override
    public Optional<byte[]> getContent(UUID fileId) {
        return Optional.ofNullable(contentStore.get(fileId));
    }

    @Override
    public void deleteById(UUID fileId) {
        metadataStore.remove(fileId);
        contentStore.remove(fileId);
    }
}
