package br.com.chatiabe.adapter.out.persistence.repository;

import br.com.chatiabe.adapter.out.persistence.entity.FileAttachmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SpringDataFileAttachmentRepository extends JpaRepository<FileAttachmentEntity, UUID> {
    boolean existsByIdAndUserId(UUID id, UUID userId);
}
