package br.com.chatiabe.application.port.inbound;

import java.util.List;
import java.util.UUID;

import br.com.chatiabe.application.dto.ChatSessionResponse;
import br.com.chatiabe.application.dto.CreateSessionRequest;
import br.com.chatiabe.application.dto.MessageResponse;
import br.com.chatiabe.application.dto.SendMessageRequest;

public interface MessageUseCase {

    List<ChatSessionResponse> listSessions();

    ChatSessionResponse createSession(CreateSessionRequest request);

    MessageResponse sendMessage(SendMessageRequest request);

    List<MessageResponse> getMessages(UUID sessionId);
}
