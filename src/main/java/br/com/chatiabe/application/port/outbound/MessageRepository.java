package br.com.chatiabe.application.port.outbound;

import java.util.List;
import java.util.UUID;

import br.com.chatiabe.domain.model.Message;

public interface MessageRepository {

    Message save(Message message);

    List<Message> findByChatSessionIdOrderByTimestampAsc(UUID chatSessionId);
}
