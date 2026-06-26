package br.com.chatiabe.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class User {

    private UUID id;
    private String username;
    private String password;
    private String email;
    private LocalDateTime createdAt;

    public User(UUID id, String username, String password, String email, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.createdAt = createdAt;
    }

    public static User create(String username, String password, String email) {
        return new User(
                UUID.randomUUID(),
                username,
                password,
                email,
                LocalDateTime.now()
        );
    }

    public UUID getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getEmail() { return email; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
