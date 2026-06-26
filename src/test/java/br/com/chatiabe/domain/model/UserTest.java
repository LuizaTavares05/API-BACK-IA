package br.com.chatiabe.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class UserTest {

    @Test
    void shouldCreateUser() {
        var id = UUID.randomUUID();
        var now = Instant.now();
        var user = new User(id, "john", "hashed123", "john@test.com", now);

        assertEquals(id, user.getId());
        assertEquals("john", user.getUsername());
        assertEquals("hashed123", user.getPassword());
        assertEquals("john@test.com", user.getEmail());
        assertEquals(now, user.getCreatedAt());
    }

    @Test
    void shouldAllowSetters() {
        var user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("jane");
        user.setPassword("hashed456");
        user.setEmail("jane@test.com");
        user.setCreatedAt(Instant.now());

        assertEquals("jane", user.getUsername());
        assertNotNull(user.getId());
    }
}
