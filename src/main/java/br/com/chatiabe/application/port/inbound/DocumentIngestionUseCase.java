package br.com.chatiabe.application.port.inbound;

import br.com.chatiabe.application.dto.DocumentResponse;
import br.com.chatiabe.application.dto.DocumentStatusResponse;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

public interface DocumentIngestionUseCase {
    DocumentResponse ingestDocument(MultipartFile file, UUID userId);
    DocumentStatusResponse getDocumentStatus(UUID documentId, UUID userId);
    DocumentResponse reprocessDocument(UUID documentId, UUID userId);
}
