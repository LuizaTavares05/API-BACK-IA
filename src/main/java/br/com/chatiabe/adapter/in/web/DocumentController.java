package br.com.chatiabe.adapter.in.web;

import br.com.chatiabe.application.dto.DocumentResponse;
import br.com.chatiabe.application.dto.DocumentStatusResponse;
import br.com.chatiabe.application.port.inbound.DocumentIngestionUseCase;
import br.com.chatiabe.infra.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    private final DocumentIngestionUseCase documentIngestionUseCase;
    private final JwtTokenProvider jwtTokenProvider;

    public DocumentController(DocumentIngestionUseCase documentIngestionUseCase,
                              JwtTokenProvider jwtTokenProvider) {
        this.documentIngestionUseCase = documentIngestionUseCase;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping
    public ResponseEntity<DocumentResponse> ingestDocument(HttpServletRequest request,
                                                           @RequestParam("file") MultipartFile file) {
        UUID userId = extractUserId(request);
        DocumentResponse response = documentIngestionUseCase.ingestDocument(file, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentStatusResponse> getDocumentStatus(HttpServletRequest request,
                                                                    @PathVariable UUID id) {
        UUID userId = extractUserId(request);
        DocumentStatusResponse response = documentIngestionUseCase.getDocumentStatus(id, userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/reprocess")
    public ResponseEntity<DocumentResponse> reprocessDocument(HttpServletRequest request,
                                                              @PathVariable UUID id) {
        UUID userId = extractUserId(request);
        DocumentResponse response = documentIngestionUseCase.reprocessDocument(id, userId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    private UUID extractUserId(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        String token = header.substring(7);
        return jwtTokenProvider.getUserIdFromToken(token);
    }
}
