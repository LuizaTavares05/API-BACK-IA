package br.com.chatiabe.adapter.out.storage;

import br.com.chatiabe.adapter.out.persistence.entity.FileAttachmentEntity;
import br.com.chatiabe.adapter.out.persistence.repository.SpringDataFileAttachmentRepository;
import br.com.chatiabe.application.port.outbound.FileStorageService;
import br.com.chatiabe.domain.model.FileAttachment;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

@Component
public class LocalFileStorageService implements FileStorageService {

    private final SpringDataFileAttachmentRepository repository;

    public LocalFileStorageService(SpringDataFileAttachmentRepository repository) {
        this.repository = repository;
    }

    @Override
    public FileAttachment save(FileAttachment attachment) {
        FileAttachmentEntity entity = new FileAttachmentEntity(
                attachment.getId(),
                attachment.getUserId(),
                attachment.getFileName(),
                attachment.getFileSize(),
                attachment.getData(),
                attachment.getUploadedAt()
        );
        FileAttachmentEntity saved = repository.save(entity);
        return new FileAttachment(
                saved.getId(), saved.getUserId(), saved.getFileName(),
                saved.getFileSize(), saved.getData(), saved.getUploadedAt()
        );
    }

    @Override
    public Optional<FileAttachment> findById(UUID id) {
        return repository.findById(id).map(e ->
                new FileAttachment(e.getId(), e.getUserId(), e.getFileName(),
                        e.getFileSize(), e.getData(), e.getUploadedAt())
        );
    }

    @Override
    public boolean existsByIdAndUserId(UUID id, UUID userId) {
        return repository.existsByIdAndUserId(id, userId);
    }
}
