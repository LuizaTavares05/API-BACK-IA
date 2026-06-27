package br.com.chatiabe.adapter.out.persistence.repository;

import br.com.chatiabe.adapter.out.persistence.entity.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SpringDataDocumentRepository extends JpaRepository<DocumentEntity, UUID> {
    boolean existsByIdAndUserId(UUID id, UUID userId);
}
