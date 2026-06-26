package br.com.chatiabe.adapter.in.web;

import java.util.List;
import java.util.UUID;

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
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @GetMapping("/sessions")
    public ResponseEntity<List<ChatSessionResponse>> listSessions() {
        return null;
    }

    @PostMapping("/sessions")
    public ResponseEntity<ChatSessionResponse> createSession(@Valid @RequestBody CreateSessionRequest request) {
        return null;
    }

    @PostMapping("/messages")
    public ResponseEntity<MessageResponse> sendMessage(@Valid @RequestBody SendMessageRequest request) {
        return null;
    }

    @GetMapping("/sessions/{sessionId}/messages")
    public ResponseEntity<List<MessageResponse>> getMessages(@PathVariable UUID sessionId) {
        return null;
    }
}
