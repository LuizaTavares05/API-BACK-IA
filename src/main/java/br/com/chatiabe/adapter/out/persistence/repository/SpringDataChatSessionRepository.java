package br.com.chatiabe.adapter.out.persistence.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.chatiabe.adapter.out.persistence.entity.ChatSessionEntity;

public interface SpringDataChatSessionRepository extends JpaRepository<ChatSessionEntity, UUID> {

    List<ChatSessionEntity> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
