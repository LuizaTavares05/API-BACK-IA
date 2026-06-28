package br.com.chatiabe.adapter.in.web;

import br.com.chatiabe.application.dto.*;
import br.com.chatiabe.application.port.inbound.MessageUseCase;
import br.com.chatiabe.application.port.inbound.RagUseCase;
import br.com.chatiabe.infra.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final MessageUseCase messageUseCase;
    private final RagUseCase ragUseCase;
    private final JwtTokenProvider jwtTokenProvider;

    public ChatController(MessageUseCase messageUseCase, RagUseCase ragUseCase,
                          JwtTokenProvider jwtTokenProvider) {
        this.messageUseCase = messageUseCase;
        this.ragUseCase = ragUseCase;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<ChatSessionResponse>> listSessions(HttpServletRequest request) {
        UUID userId = extractUserId(request);
        return ResponseEntity.ok(messageUseCase.listSessions(userId));
    }

    @PostMapping("/sessions")
    public ResponseEntity<ChatSessionResponse> createSession(HttpServletRequest request,
                                                              @Valid @RequestBody CreateSessionRequest createRequest) {
        UUID userId = extractUserId(request);
        ChatSessionResponse response = messageUseCase.createSession(createRequest.title(), userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/messages")
    public ResponseEntity<RagMessageResponse> sendMessage(HttpServletRequest request,
                                                          @Valid @RequestBody SendMessageRequest messageRequest) {
        UUID userId = extractUserId(request);
        return ResponseEntity.ok(ragUseCase.sendMessageWithContext(messageRequest, userId));
    }

    @GetMapping("/sessions/{sessionId}/messages")
    public ResponseEntity<List<MessageResponse>> getMessages(HttpServletRequest request,
                                                             @PathVariable UUID sessionId) {
        UUID userId = extractUserId(request);
        return ResponseEntity.ok(messageUseCase.getMessages(sessionId, userId));
    }

    private UUID extractUserId(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        String token = header.substring(7);
        return jwtTokenProvider.getUserIdFromToken(token);
    }
}
