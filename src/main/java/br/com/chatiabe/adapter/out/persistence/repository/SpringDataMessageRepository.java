package br.com.chatiabe.adapter.out.persistence.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.chatiabe.adapter.out.persistence.entity.MessageEntity;

public interface SpringDataMessageRepository extends JpaRepository<MessageEntity, UUID> {

    List<MessageEntity> findByChatSessionIdOrderByTimestampAsc(UUID chatSessionId);
}
