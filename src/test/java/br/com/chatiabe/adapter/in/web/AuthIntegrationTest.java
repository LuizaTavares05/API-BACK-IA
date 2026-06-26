package br.com.chatiabe.adapter.in.web;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import br.com.chatiabe.application.dto.ErrorResponse;
import br.com.chatiabe.application.dto.LoginRequest;
import br.com.chatiabe.application.dto.LoginResponse;
import br.com.chatiabe.application.dto.RegisterRequest;
import br.com.chatiabe.application.dto.RegisterResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthIntegrationTest {

    @Autowired
    private TestRestTemplate rest;

    @Test
    void shouldRegisterAndLogin() {
        var registerResponse = rest.postForEntity("/api/auth/register",
                new RegisterRequest("integ-user", "pass123", "u@t.com"),
                RegisterResponse.class);

        assertEquals(HttpStatus.CREATED, registerResponse.getStatusCode());
        assertNotNull(registerResponse.getBody());
        assertEquals("integ-user", registerResponse.getBody().username());
        assertNotNull(registerResponse.getBody().id());

        var loginResponse = rest.postForEntity("/api/auth/login",
                new LoginRequest("integ-user", "pass123"),
                LoginResponse.class);

        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        assertNotNull(loginResponse.getBody());
        assertNotNull(loginResponse.getBody().token());
        assertEquals(86400, loginResponse.getBody().expiresIn());
    }

    @Test
    void shouldRejectDuplicateRegister() {
        rest.postForEntity("/api/auth/register",
                new RegisterRequest("dup-user", "pass123", null),
                RegisterResponse.class);

        var response = rest.postForEntity("/api/auth/register",
                new RegisterRequest("dup-user", "pass123", null),
                ErrorResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().message().contains("Username already in use"));
    }

    @Test
    void shouldRejectInvalidLogin() {
        var response = rest.postForEntity("/api/auth/login",
                new LoginRequest("noone", "wrong"),
                ErrorResponse.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody().message().contains("Invalid username or password"));
    }
}
