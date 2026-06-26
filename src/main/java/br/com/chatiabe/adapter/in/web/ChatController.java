package br.com.chatiabe.adapter.in.web;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.chatiabe.application.dto.ChatSessionResponse;
import br.com.chatiabe.application.dto.CreateSessionRequest;
import br.com.chatiabe.application.dto.MessageResponse;
import br.com.chatiabe.application.dto.SendMessageRequest;
import br.com.chatiabe.application.port.inbound.MessageUseCase;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final MessageUseCase messageUseCase;

    public ChatController(MessageUseCase messageUseCase) {
        this.messageUseCase = messageUseCase;
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<ChatSessionResponse>> listSessions() {
        List<ChatSessionResponse> response = messageUseCase.listSessions();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sessions")
    public ResponseEntity<ChatSessionResponse> createSession(@Valid @RequestBody CreateSessionRequest request) {
        ChatSessionResponse response = messageUseCase.createSession(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/messages")
    public ResponseEntity<MessageResponse> sendMessage(@Valid @RequestBody SendMessageRequest request) {
        MessageResponse response = messageUseCase.sendMessage(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sessions/{sessionId}/messages")
    public ResponseEntity<List<MessageResponse>> getMessages(@PathVariable UUID sessionId) {
        List<MessageResponse> response = messageUseCase.getMessages(sessionId);
        return ResponseEntity.ok(response);
    }
}
