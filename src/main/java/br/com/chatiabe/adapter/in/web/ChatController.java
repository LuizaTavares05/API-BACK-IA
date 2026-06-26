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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final MessageUseCase messageUseCase;

    public ChatController(MessageUseCase messageUseCase) {
        this.messageUseCase = messageUseCase;
    }

    @GetMapping("/sessions")
    @Operation(summary = "Listar sessões de chat do usuário autenticado")
    @ApiResponse(responseCode = "200", description = "Lista de sessões retornada com sucesso")
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    public ResponseEntity<List<ChatSessionResponse>> listSessions() {
        return ResponseEntity.ok(messageUseCase.listSessions());
    }

    @PostMapping("/sessions")
    @Operation(summary = "Criar nova sessão de chat")
    @ApiResponse(responseCode = "201", description = "Sessão criada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    public ResponseEntity<ChatSessionResponse> createSession(@Valid @RequestBody CreateSessionRequest request) {
        var response = messageUseCase.createSession(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/messages")
    @Operation(summary = "Enviar uma mensagem no chat")
    @ApiResponse(responseCode = "200", description = "Mensagem enviada com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    @ApiResponse(responseCode = "404", description = "Sessão não encontrada")
    public ResponseEntity<MessageResponse> sendMessage(@Valid @RequestBody SendMessageRequest request) {
        return ResponseEntity.ok(messageUseCase.sendMessage(request));
    }

    @GetMapping("/sessions/{sessionId}/messages")
    @Operation(summary = "Obter histórico de mensagens de uma sessão")
    @ApiResponse(responseCode = "200", description = "Histórico de mensagens retornado com sucesso")
    @ApiResponse(responseCode = "401", description = "Não autenticado")
    @ApiResponse(responseCode = "404", description = "Sessão não encontrada")
    public ResponseEntity<List<MessageResponse>> getMessages(@PathVariable UUID sessionId) {
        return ResponseEntity.ok(messageUseCase.getMessages(sessionId));
    }
}
