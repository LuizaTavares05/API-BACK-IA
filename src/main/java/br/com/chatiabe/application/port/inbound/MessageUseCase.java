package br.com.chatiabe.application.port.inbound;

import br.com.chatiabe.application.dto.*;
import java.util.List;
import java.util.UUID;

public interface MessageUseCase {
    MessageResponse sendMessage(SendMessageRequest request, UUID userId);
    List<ChatSessionResponse> listSessions(UUID userId);
    ChatSessionResponse createSession(String title, UUID userId);
    List<MessageResponse> getMessages(UUID sessionId, UUID userId);
}
