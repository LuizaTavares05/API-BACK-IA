package br.com.chatiabe.application.service;

import br.com.chatiabe.application.dto.DocumentResponse;
import br.com.chatiabe.application.dto.DocumentStatusResponse;
import br.com.chatiabe.application.port.inbound.DocumentIngestionUseCase;
import br.com.chatiabe.application.port.outbound.DocumentChunkRepository;
import br.com.chatiabe.application.port.outbound.DocumentRepository;
import br.com.chatiabe.application.port.outbound.WebhookNotifier;
import br.com.chatiabe.domain.exception.DocumentProcessingException;
import br.com.chatiabe.domain.exception.UnsupportedFileFormatException;
import br.com.chatiabe.domain.model.Document;
import br.com.chatiabe.domain.model.DocumentChunk;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentIngestionService implements DocumentIngestionUseCase {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024L;
    private static final long MAX_EXTRACTED_TEXT_SIZE = 10 * 1024 * 1024L;

    private final DocumentRepository documentRepository;
    private final DocumentChunkRepository documentChunkRepository;
    private final EmbeddingService embeddingService;
    private final WebhookNotifier webhookNotifier;

    @Value("${app.rag.chunk-size:1000}")
    private int chunkSize;

    @Value("${app.rag.chunk-overlap:200}")
    private int chunkOverlap;

    public DocumentIngestionService(DocumentRepository documentRepository,
                                    DocumentChunkRepository documentChunkRepository,
                                    EmbeddingService embeddingService,
                                    WebhookNotifier webhookNotifier) {
        this.documentRepository = documentRepository;
        this.documentChunkRepository = documentChunkRepository;
        this.embeddingService = embeddingService;
        this.webhookNotifier = webhookNotifier;
    }

    @Override
    public DocumentResponse ingestDocument(MultipartFile file, UUID userId) {
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("File name is required");
        }

        String extension = getExtension(fileName);
        if (!extension.equals("txt") && !extension.equals("pdf")) {
            throw new UnsupportedFileFormatException("Formato de arquivo não suportado. Apenas .txt e .pdf são aceitos.");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size exceeds the maximum limit of 5MB");
        }

        String rawText = extractText(file, extension);
        if (rawText == null || rawText.isBlank()) {
            throw new DocumentProcessingException("Documento vazio ou sem texto extraível");
        }

        if (rawText.length() > MAX_EXTRACTED_TEXT_SIZE) {
            throw new DocumentProcessingException("Texto extraído excede o limite máximo de 10MB");
        }

        String mimeType = extension.equals("pdf") ? "application/pdf" : "text/plain";
        Document document = Document.create(userId, fileName, file.getSize(), mimeType);
        document.markProcessing();
        document = documentRepository.save(document);

        try {
            List<String> chunks = chunkText(rawText);
            List<DocumentChunk> documentChunks = new ArrayList<>();

            for (int i = 0; i < chunks.size(); i++) {
                float[] embedding = embeddingService.generateEmbedding(chunks.get(i));
                DocumentChunk chunk = DocumentChunk.create(document.getId(), i, chunks.get(i), embedding);
                documentChunks.add(chunk);
            }

            documentChunkRepository.saveAll(documentChunks);
            document.markCompleted(chunks.size());
            document = documentRepository.save(document);

            webhookNotifier.notify(document);
        } catch (Exception e) {
            document.markFailed(e.getMessage());
            documentRepository.save(document);
            throw e;
        }

        return toDocumentResponse(document);
    }

    @Override
    public DocumentStatusResponse getDocumentStatus(UUID documentId, UUID userId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found"));

        if (!document.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Document not found");
        }

        return toDocumentStatusResponse(document);
    }

    @Override
    public DocumentResponse reprocessDocument(UUID documentId, UUID userId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Document not found"));

        if (!document.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Document not found");
        }

        document.markProcessing();
        documentRepository.save(document);

        return toDocumentResponse(document);
    }

    private String extractText(MultipartFile file, String extension) {
        try {
            if (extension.equals("pdf")) {
                try (PDDocument pdf = Loader.loadPDF(file.getBytes())) {
                    PDFTextStripper stripper = new PDFTextStripper();
                    return stripper.getText(pdf);
                }
            }
            return new String(file.getBytes(), java.nio.charset.StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new DocumentProcessingException("Erro ao extrair texto do documento: " + e.getMessage());
        }
    }

    private List<String> chunkText(String text) {
        List<String> chunks = new ArrayList<>();
        int start = 0;
        int textLength = text.length();

        while (start < textLength) {
            int end = Math.min(start + chunkSize, textLength);
            chunks.add(text.substring(start, end));
            if (end >= textLength) break;
            start = end - chunkOverlap;
        }

        return chunks;
    }

    private String getExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0) return "";
        return fileName.substring(dotIndex + 1).toLowerCase();
    }

    private DocumentResponse toDocumentResponse(Document doc) {
        return new DocumentResponse(
                doc.getId(), doc.getFileName(), doc.getFileSize(),
                doc.getStatus(), doc.getChunkCount(), doc.getCreatedAt()
        );
    }

    private DocumentStatusResponse toDocumentStatusResponse(Document doc) {
        return new DocumentStatusResponse(
                doc.getId(), doc.getFileName(), doc.getFileSize(),
                doc.getStatus(), doc.getChunkCount(), doc.getCreatedAt(),
                doc.getCompletedAt(), doc.getErrorMessage()
        );
    }
}
