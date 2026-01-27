package com.movies_searcher.service;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import com.movies_searcher.dto.WatchedCreateDTO;
import com.movies_searcher.dto.WatchedResponseDTO;
import com.movies_searcher.model.User;
import com.movies_searcher.model.Watched;
import com.movies_searcher.repository.WatchedRepository;

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
public class WatchedServiceTest {
    
    @Mock
    private WatchedRepository watchedRepository;

    @InjectMocks
    private WatchedService watchedService;

    @Mock
    private Authentication authentication;

    @Test
    @DisplayName("It must list all watched for a user")
    void shouldListAllWatchedForUser() {
        User user = new User("Alex", "alex@test.com", "123");
        Watched watched1 = new Watched(user, 550L, "movie", "/path1.jpg", "Fight Club");
        Watched watched2 = new Watched(user, 551L, "movie", "/path2.jpg", "The Matrix");
        when(watchedRepository.findAllByUser(user)).thenReturn(List.of(watched1, watched2));

        List<WatchedResponseDTO> result = watchedService.listWatched(user);

        assertEquals(2, result.size());
        assertEquals(550L, result.get(0).tmdbId());
        assertEquals("Fight Club", result.get(0).title());
        assertEquals(551L, result.get(1).tmdbId());
        assertEquals("The Matrix", result.get(1).title());

        verify(watchedRepository, times(1)).findAllByUser(user);
    }

    @Test
    @DisplayName("It must return an empty list when user has no watched")
    void shouldReturnEmptyListWhenNoWatchList() {
        User userMock = new User("alex", "alex@test.com", "123");
        when(watchedRepository.findAllByUser(userMock)).thenReturn(List.of());
        List<WatchedResponseDTO> result = watchedService.listWatched(userMock);
        assertEquals(0, result.size());
        verify(watchedRepository, times(1)).findAllByUser(userMock);
    }

    @Test
    @DisplayName("It must successfully create a watched if it already doesnt exist")
    void shouldCreateWatchedSuccessfully() {
        
        User user = new User("Alex", "alex@test.com", "123");
        user.setId(1L);
        WatchedCreateDTO dto = new WatchedCreateDTO(550L, "movie", "/path.jpg", "Fight Club");
        
        when(authentication.getPrincipal()).thenReturn(user); 
        
        when(watchedRepository.existsByUserAndTmdbId(user, 550L)).thenReturn(false);

        watchedService.createWatched(dto, authentication);
        verify(watchedRepository, times(1)).save(any(Watched.class));

        ArgumentCaptor<Watched> watchedCaptor = ArgumentCaptor.forClass(Watched.class);

        Watched savedObject =  watchedCaptor.getValue();
        assertEquals(dto.tmdbId(), savedObject.getTmdbId()); 
        assertEquals(dto.videoType(), savedObject.getVideoType());
        assertEquals(dto.posterPath(), savedObject.getPosterPath());
        assertEquals(dto.title(), savedObject.getTitle());  
        assertEquals(user, savedObject.getUser());
    }

    @Test
    @DisplayName("It must throw an error when this media is already watched")
    void shouldThrowExceptionWhenWatchedAlreadyExists() {
        User user = new User("Alex", "alex@test.com", "123");
        WatchedCreateDTO dto = new WatchedCreateDTO(550L, "movie", "/path.jpg", "Fight Club");

        when(authentication.getPrincipal()).thenReturn(user);
        
        when(watchedRepository.existsByUserAndTmdbId(user, 550L)).thenReturn(true);

        
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            watchedService.createWatched(dto, authentication);
        });

        assertEquals("This media was already define as watched", exception.getMessage());

        verify(watchedRepository, never()).save(any());
    }

    @Test
    @DisplayName("It must remove successfully a watched when it exists")
    void shouldRemoveWatchedSuccessfully() {
        User user = new User("Alex", "alex@test.com", "123");
        Watched watchList  = new Watched(user, 550L, "movie", "path", "Title");
        when(authentication.getPrincipal()).thenReturn(user);
        when(watchedRepository.findByUserAndTmdbId(user, 550L)).thenReturn(Optional.of(watchList));

        watchedService.removeWatched(550L, authentication);
        verify(watchedRepository, times(1)).delete(watchList);
    }

    @Test
    @DisplayName("It must throw an Exception when trying to remove watched that is not watched yet")
    void shouldThrowExceptionWhenRemovingNonExistingWatched(){
        User user = new User("Alex", "alex@test.com", "123");
        when(authentication.getPrincipal()).thenReturn(user);
        when(watchedRepository.findByUserAndTmdbId(user, 999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            watchedService.removeWatched(999L, authentication);
        });

        assertEquals("Media not found in watched", exception.getMessage());
        verify(watchedRepository, never()).delete(any());    
    }
}
