package br.com.chatiabe.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import br.com.chatiabe.application.dto.LoginRequest;
import br.com.chatiabe.application.dto.RegisterRequest;
import br.com.chatiabe.application.port.outbound.UserRepository;
import br.com.chatiabe.domain.model.User;
import br.com.chatiabe.infra.security.JwtTokenProvider;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider jwtTokenProvider;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, jwtTokenProvider);
    }

    @Test
    void shouldLoginSuccessfully() {
        var userId = UUID.randomUUID();
        var user = new User(userId, "john", "encodedPass", null, Instant.now());
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass123", "encodedPass")).thenReturn(true);
        when(jwtTokenProvider.generateToken(userId)).thenReturn("jwt-token");

        var response = authService.login(new LoginRequest("john", "pass123"));

        assertEquals("jwt-token", response.token());
        assertEquals(86400, response.expiresIn());
    }

    @Test
    void shouldThrowOnInvalidUsername() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class,
                () -> authService.login(new LoginRequest("unknown", "pass")));
    }

    @Test
    void shouldThrowOnInvalidPassword() {
        var user = new User(UUID.randomUUID(), "john", "encodedPass", null, Instant.now());
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "encodedPass")).thenReturn(false);

        assertThrows(BadCredentialsException.class,
                () -> authService.login(new LoginRequest("john", "wrong")));
    }

    @Test
    void shouldRegisterSuccessfully() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(passwordEncoder.encode("pass123")).thenReturn("hashed");
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var response = authService.register(new RegisterRequest("newuser", "pass123", "new@test.com"));

        assertEquals("newuser", response.username());
        assertNotNull(response.id());
        assertNotNull(response.createdAt());
    }

    @Test
    void shouldThrowOnDuplicateUsername() {
        when(userRepository.existsByUsername("existing")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> authService.register(new RegisterRequest("existing", "pass", null)));
    }
}
