package br.com.chatiabe.application.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.chatiabe.application.dto.LoginRequest;
import br.com.chatiabe.application.dto.LoginResponse;
import br.com.chatiabe.application.dto.RegisterRequest;
import br.com.chatiabe.application.dto.RegisterResponse;
import br.com.chatiabe.application.port.inbound.AuthUseCase;
import br.com.chatiabe.application.port.outbound.UserRepository;
import br.com.chatiabe.domain.model.User;
import br.com.chatiabe.infra.security.JwtTokenProvider;

@Service
public class AuthService implements AuthUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        var user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        String token = jwtTokenProvider.generateToken(user.getId());
        return new LoginResponse(token, 86400);
    }

    @Override
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username already in use");
        }

        var user = new User(
                UUID.randomUUID(),
                request.username(),
                passwordEncoder.encode(request.password()),
                request.email(),
                Instant.now()
        );

        var saved = userRepository.save(user);
        return new RegisterResponse(saved.getId(), saved.getUsername(), saved.getCreatedAt());
    }
}
