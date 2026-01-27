package com.movies_searcher.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.movies_searcher.model.TokenBlacklist;
import com.movies_searcher.repository.TokenBlacklistRepository;

@ExtendWith(MockitoExtension.class)
public class TokenBlackListServiceTest {
    @Mock
    private TokenBlacklistRepository tokenBlacklistRepository;

    @InjectMocks
    private TokenBlacklistService tokenBlacklistService;

    @Test
    @DisplayName("It must add a token to the blacklist")
    void shouldAddTokenToBlacklist() {
        String token = "sample.jwt.token";
        tokenBlacklistService.addTokenToBlacklist(token);

        ArgumentCaptor<TokenBlacklist> tokenCaptor = ArgumentCaptor.forClass(TokenBlacklist.class);
        verify(tokenBlacklistRepository, times(1)).save(tokenCaptor.capture());

        TokenBlacklist savedObject =  tokenCaptor.getValue();
        assertEquals(token, savedObject.getToken()); 
        assertNotNull(savedObject.getExpirationDate());
    }

    @Test
    @DisplayName("Should return true if token is in blacklist")
    void shouldReturnTrueIfTokenIsBlacklisted() {
        String token = "token.bloqueado";
        when(tokenBlacklistRepository.existsByToken(token)).thenReturn(true);

        boolean result = tokenBlacklistService.isTokenBlacklisted(token);

        assertTrue(result);
        verify(tokenBlacklistRepository, times(1)).existsByToken(token);
    }

    @Test
    @DisplayName("Should return false if token is not in blacklist")
    void shouldReturnFalseIfTokenIsNotBlacklisted() {
        String token = "token.valido";
        when(tokenBlacklistRepository.existsByToken(token)).thenReturn(false);

        boolean result = tokenBlacklistService.isTokenBlacklisted(token);

        assertFalse(result);
        verify(tokenBlacklistRepository, times(1)).existsByToken(token);
    }
}
