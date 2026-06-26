package br.com.chatiabe.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import br.com.chatiabe.application.dto.CreateSessionRequest;
import br.com.chatiabe.application.dto.SendMessageRequest;
import br.com.chatiabe.application.port.outbound.ChatSessionRepository;
import br.com.chatiabe.application.port.outbound.MessageRepository;
import br.com.chatiabe.domain.model.ChatSession;
import br.com.chatiabe.domain.model.Message;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;
    @Mock
    private ChatSessionRepository chatSessionRepository;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    private MessageService messageService;
    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        messageService = new MessageService(messageRepository, chatSessionRepository);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        lenient().when(authentication.getPrincipal()).thenReturn(userId.toString());
        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldListSessions() {
        var session = new ChatSession(UUID.randomUUID(), "Chat 1", userId, Instant.now());
        when(chatSessionRepository.findByUserId(userId)).thenReturn(List.of(session));

        var sessions = messageService.listSessions();

        assertEquals(1, sessions.size());
        assertEquals("Chat 1", sessions.get(0).title());
    }

    @Test
    void shouldCreateSession() {
        when(chatSessionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = messageService.createSession(new CreateSessionRequest("New Chat"));

        assertNotNull(response.id());
        assertEquals("New Chat", response.title());
    }

    @Test
    void shouldSendMessage() {
        var sessionId = UUID.randomUUID();
        var session = new ChatSession(sessionId, "Chat", userId, Instant.now());
        when(chatSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(messageRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = messageService.sendMessage(new SendMessageRequest(sessionId, "Hello"));

        assertEquals(sessionId, response.chatSessionId());
        assertEquals("Hello", response.content());
    }

    @Test
    void shouldThrowWhenSessionNotFound() {
        var sessionId = UUID.randomUUID();
        when(chatSessionRepository.findById(sessionId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> messageService.sendMessage(new SendMessageRequest(sessionId, "Hi")));
    }

    @Test
    void shouldGetMessages() {
        var sessionId = UUID.randomUUID();
        var session = new ChatSession(sessionId, "Chat", userId, Instant.now());
        var message = new Message(UUID.randomUUID(), sessionId, Message.Role.USER, "Hello", Instant.now());

        when(chatSessionRepository.findById(sessionId)).thenReturn(Optional.of(session));
        when(messageRepository.findByChatSessionIdOrderByTimestampAsc(sessionId)).thenReturn(List.of(message));

        var messages = messageService.getMessages(sessionId);

        assertEquals(1, messages.size());
        assertEquals("Hello", messages.get(0).content());
    }
}
