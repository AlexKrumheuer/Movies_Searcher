package com.movies_searcher.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.movies_searcher.model.User;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(tokenService, "secret", "mySecretKey123");
    }

    @Test
    @DisplayName("Should generate a valid JWT token for a given user")
    void shouldGenerateTokenSuccessfully() {
        User user = new User("Alex", "alex@test.com", "123");
        String token = tokenService.generateToken(user);
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    @DisplayName("Should validate a generated token and return the correct email (Subject)")
    void shouldValidateTokenSuccessfully() {
        User user = new User("Alex", "alex@test.com", "123");
        String tokenValido = tokenService.generateToken(user);
        String subject = tokenService.validateToken(tokenValido);
        assertEquals("alex@test.com", subject);
    }

    @Test
    @DisplayName("Should return empty string when receiving an invalid token")
    void shouldReturnEmptyStringForInvalidToken() {
        String tokenInvalido = "token.totalmente.errado.123";
        String subject = tokenService.validateToken(tokenInvalido);
        assertEquals("", subject);
    }
}