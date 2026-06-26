package br.com.chatiabe.application.port.outbound;

import br.com.chatiabe.domain.model.Message;
import java.util.List;
import java.util.UUID;

public interface MessageRepository {
    Message save(Message message);
    List<Message> findByChatSessionId(UUID chatSessionId);
}
