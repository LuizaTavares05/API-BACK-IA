package br.com.chatiabe.adapter.out.persistence.repository;

import br.com.chatiabe.adapter.out.persistence.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface SpringDataMessageRepository extends JpaRepository<MessageEntity, UUID> {
    List<MessageEntity> findByChatSessionIdOrderByTimestampAsc(UUID chatSessionId);
}
