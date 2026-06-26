package br.com.chatiabe.adapter.in.web;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import br.com.chatiabe.application.dto.ChatSessionResponse;
import br.com.chatiabe.application.dto.CreateSessionRequest;
import br.com.chatiabe.application.dto.ErrorResponse;
import br.com.chatiabe.application.dto.FileUploadResponse;
import br.com.chatiabe.application.dto.LoginRequest;
import br.com.chatiabe.application.dto.LoginResponse;
import br.com.chatiabe.application.dto.MessageResponse;
import br.com.chatiabe.application.dto.RegisterRequest;
import br.com.chatiabe.application.dto.RegisterResponse;
import br.com.chatiabe.application.dto.SendMessageRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ChatFlowIntegrationTest {

    @Autowired
    private TestRestTemplate rest;

    private String token;
    private String sessionId;

    @BeforeEach
    void setUp() {
        var userId = "flow-user-" + System.currentTimeMillis();

        rest.postForEntity("/api/auth/register",
                new RegisterRequest(userId, "pass123", "flow@t.com"),
                RegisterResponse.class);

        var login = rest.postForEntity("/api/auth/login",
                new LoginRequest(userId, "pass123"),
                LoginResponse.class);

        token = login.getBody().token();
    }

    @Test
    void shouldCompleteChatFlow() {
        sessionId = createSession();
        listSessions();
        var msgId = sendMessage();
        getMessages();
        var fileId = uploadFile();
        downloadFile(fileId);
    }

    @Test
    void shouldRejectUnauthenticated() {
        var response = rest.getForEntity("/api/chat/sessions", ErrorResponse.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    private String createSession() {
        var headers = new HttpHeaders();
        headers.setBearerAuth(token);
        var entity = new HttpEntity<>(new CreateSessionRequest("Test Session"), headers);

        var response = rest.exchange("/api/chat/sessions", HttpMethod.POST, entity, ChatSessionResponse.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Session", response.getBody().title());
        return response.getBody().id().toString();
    }

    private void listSessions() {
        var headers = new HttpHeaders();
        headers.setBearerAuth(token);
        var entity = new HttpEntity<>(headers);

        var response = rest.exchange("/api/chat/sessions", HttpMethod.GET, entity, ChatSessionResponse[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().length >= 1);
    }

    private String sendMessage() {
        var headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);
        var entity = new HttpEntity<>(new SendMessageRequest(java.util.UUID.fromString(sessionId), "Hello"), headers);

        var response = rest.exchange("/api/chat/messages", HttpMethod.POST, entity, MessageResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Hello", response.getBody().content());
        assertEquals("USER", response.getBody().role().name());
        return response.getBody().id().toString();
    }

    private void getMessages() {
        var headers = new HttpHeaders();
        headers.setBearerAuth(token);
        var entity = new HttpEntity<>(headers);

        var response = rest.exchange(
                "/api/chat/sessions/" + sessionId + "/messages",
                HttpMethod.GET, entity, MessageResponse[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().length >= 2);
    }

    private String uploadFile() {
        var headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        var resource = new ByteArrayResource("test content".getBytes()) {
            @Override
            public String getFilename() {
                return "test.txt";
            }
        };

        var body = new LinkedMultiValueMap<String, Object>();
        body.add("file", resource);

        var entity = new HttpEntity<>(body, headers);

        var response = rest.exchange("/api/chat/files", HttpMethod.POST, entity, FileUploadResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("test.txt", response.getBody().fileName());
        return response.getBody().fileId().toString();
    }

    private void downloadFile(String fileId) {
        var headers = new HttpHeaders();
        headers.setBearerAuth(token);
        var entity = new HttpEntity<>(headers);

        var response = rest.exchange(
                "/api/chat/files/" + fileId,
                HttpMethod.GET, entity, byte[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().length > 0);
    }
}
