package br.com.chatiabe.application.port.outbound;

import br.com.chatiabe.domain.model.Document;
import java.util.Optional;
import java.util.UUID;

public interface DocumentRepository {
    Document save(Document document);
    Optional<Document> findById(UUID id);
    boolean existsByIdAndUserId(UUID id, UUID userId);
}
