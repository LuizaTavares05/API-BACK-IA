package br.com.chatiabe.adapter.out.persistence.repository;

import br.com.chatiabe.adapter.out.persistence.entity.ChatSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface SpringDataChatSessionRepository extends JpaRepository<ChatSessionEntity, UUID> {
    List<ChatSessionEntity> findByUserIdOrderByCreatedAtDesc(UUID userId);
    boolean existsByIdAndUserId(UUID id, UUID userId);
}
