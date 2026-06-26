package br.com.chatiabe.application.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtTokenProvider.generateToken(user.getId(), "USER");
        return new LoginResponse(token, 86400);
    }

    @Override
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username already in use: " + request.username());
        }

        User user = new User(
                UUID.randomUUID(),
                request.username(),
                passwordEncoder.encode(request.password()),
                request.email(),
                Instant.now()
        );

        User saved = userRepository.save(user);

        return new RegisterResponse(
                saved.getId(),
                saved.getUsername(),
                saved.getCreatedAt()
        );
    }
}
