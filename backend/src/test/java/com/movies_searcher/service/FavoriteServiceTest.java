package com.movies_searcher.service;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import com.movies_searcher.dto.FavoriteCreateDTO;
import com.movies_searcher.dto.FavoriteResponseDTO;
import com.movies_searcher.model.Favorite;
import com.movies_searcher.model.User;
import com.movies_searcher.repository.FavoriteRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
public class FavoriteServiceTest {
    
    @Mock
    private FavoriteRepository favoriteRepository;

    @InjectMocks
    private FavoriteService favoriteService;

    @Mock
    private Authentication authentication;

    @Test
    @DisplayName("It must list all favorites for a user")
    void shouldListAllFavoritesForUser() {
        User userMock = new User("Alex", "alex@test.com", "123");
        Favorite favorite1 = new Favorite(userMock, 550L, "movie", "/path1.jpg", "Fight Club");
        Favorite favorite2 = new Favorite(userMock, 551L, "movie", "/path2.jpg", "The Matrix");
        when(favoriteRepository.findAllByUser(userMock)).thenReturn(List.of(favorite1, favorite2));
        List<FavoriteResponseDTO> result = favoriteService.listFavorites(userMock);
        assertEquals(2, result.size());
        assertEquals(550L, result.get(0).tmdbId());
        assertEquals("Fight Club", result.get(0).title());
        assertEquals(551L, result.get(1).tmdbId());
        assertEquals("The Matrix", result.get(1).title());
        verify(favoriteRepository, times(1)).findAllByUser(userMock);
    }

    @Test
    @DisplayName("It must return an empty list when user has no favorites")
    void shouldReturnEmptyListWhenNoFavorites() {
        User userMock = new User("Alex", "alex@test.com", "123");
        when(favoriteRepository.findAllByUser(userMock)).thenReturn(List.of());
        List<FavoriteResponseDTO> result = favoriteService.listFavorites(userMock);
        assertEquals(0, result.size());
        verify(favoriteRepository, times(1)).findAllByUser(userMock);
    }

    @Test
    @DisplayName("It must successfully create a favorite it already doesnt exist")
    void shouldCreateFavoriteSuccessfully() {
        
        User user = new User("Alex", "alex@test.com", "123");
        user.setId(1L);
        FavoriteCreateDTO dto = new FavoriteCreateDTO(550L, "movie", "/path.jpg", "Fight Club");
        
        when(authentication.getPrincipal()).thenReturn(user); 
        
        when(favoriteRepository.existsByUserAndTmdbId(user, 550L)).thenReturn(false);

        favoriteService.createFavorite(dto, authentication);
        verify(favoriteRepository, times(1)).save(any(Favorite.class));
    }

    @Test
    @DisplayName("It must throw an error when this media is already favorited")
    void shouldThrowExceptionWhenFavoriteAlreadyExists() {
        User user = new User("Alex", "alex@test.com", "123");
        FavoriteCreateDTO dto = new FavoriteCreateDTO(550L, "movie", "/path.jpg", "Fight Club");

        when(authentication.getPrincipal()).thenReturn(user);
        
        when(favoriteRepository.existsByUserAndTmdbId(user, 550L)).thenReturn(true);

        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            favoriteService.createFavorite(dto, authentication);
        });

        assertEquals("This media was already define as favorite", exception.getMessage());

        verify(favoriteRepository, never()).save(any());
    }

    @Test
    @DisplayName("It must remove successfully a favorite when it exists")
    void shouldRemoveFavoriteSuccessfully() {
        User user = new User("Alex", "alex@test.com", "123");
        Favorite favorite  = new Favorite(user, 550L, "movie", "path", "Title");
        when(authentication.getPrincipal()).thenReturn(user);
        when(favoriteRepository.findByUserAndTmdbId(user, 550L)).thenReturn(Optional.of(favorite));

        favoriteService.removeFavorite(550L, authentication);
        verify(favoriteRepository, times(1)).delete(favorite);
    }

    @Test
    @DisplayName("It must throw an Exception when trying to remove favorite that is not favorited yet")
    void shouldThrowExceptionWhenRemovingNonExistingFavorite(){
        User user = new User("Alex", "alex@test.com", "123");
        when(authentication.getPrincipal()).thenReturn(user);
        when(favoriteRepository.findByUserAndTmdbId(user, 999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            favoriteService.removeFavorite(999L, authentication);
        });

        assertEquals("Media not found in favorites", exception.getMessage());
        verify(favoriteRepository, never()).delete(any());    
    }
}
