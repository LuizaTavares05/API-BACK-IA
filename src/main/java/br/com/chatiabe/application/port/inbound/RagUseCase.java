package br.com.chatiabe.application.port.inbound;

import br.com.chatiabe.application.dto.RagMessageResponse;
import br.com.chatiabe.application.dto.SendMessageRequest;
import java.util.UUID;

public interface RagUseCase {
    RagMessageResponse sendMessageWithContext(SendMessageRequest request, UUID userId);
}
