package br.com.chatiabe.adapter.out.persistence.adapter;

import br.com.chatiabe.adapter.out.persistence.mapper.DocumentMapper;
import br.com.chatiabe.adapter.out.persistence.repository.SpringDataDocumentRepository;
import br.com.chatiabe.application.port.outbound.DocumentRepository;
import br.com.chatiabe.domain.model.Document;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

@Component
public class DocumentRepositoryAdapter implements DocumentRepository {

    private final SpringDataDocumentRepository springRepo;

    public DocumentRepositoryAdapter(SpringDataDocumentRepository springRepo) {
        this.springRepo = springRepo;
    }

    @Override
    public Document save(Document document) {
        return DocumentMapper.toDomain(springRepo.save(DocumentMapper.toEntity(document)));
    }

    @Override
    public Optional<Document> findById(UUID id) {
        return springRepo.findById(id).map(DocumentMapper::toDomain);
    }

    @Override
    public boolean existsByIdAndUserId(UUID id, UUID userId) {
        return springRepo.existsByIdAndUserId(id, userId);
    }
}
