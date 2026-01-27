package com.movies_searcher.service;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.movies_searcher.dto.WatchListCreateDTO;
import com.movies_searcher.dto.WatchListResponseDTO;
import com.movies_searcher.model.User;
import com.movies_searcher.model.WatchList;
import com.movies_searcher.repository.WatchListRepository;


@ExtendWith(MockitoExtension.class)
public class WatchServiceListTest {

    @Mock
    private WatchListRepository watchListRepository;

    @InjectMocks
    private WatchListService watchListService;

    @Mock
    private Authentication authentication;

    @Test
    @DisplayName("It must list all watch list for a user")
    void shouldListAllWatchListForUser() {
        User userMock = new User("Alex", "alex@test.com", "123");
        WatchList watch1 = new WatchList(userMock, 600L, "movie", "/path1.jpg", "Inception");
        WatchList watch2 = new WatchList(userMock, 601L, "movie", "/path2.jpg", "Interstellar");
        when(watchListRepository.findAllByUser(userMock)).thenReturn(List.of(watch1, watch2));

        List<WatchListResponseDTO> result = watchListService.getAllWatch(userMock);
        assertEquals(2, result.size());
        assertEquals(600L, result.get(0).tmdbId());
        assertEquals("Inception", result.get(0).title());
        assertEquals(601L, result.get(1).tmdbId());
        assertEquals("Interstellar", result.get(1).title());

        verify(watchListRepository, times(1)).findAllByUser(userMock);
    }

    @Test
    @DisplayName("It must return an empty list when user has no watch list")
    void shouldReturnEmptyListWhenNoWatchList() {
        User userMock = new User("alex", "alex@test.com", "123");
        when(watchListRepository.findAllByUser(userMock)).thenReturn(List.of());
        List<WatchListResponseDTO> result = watchListService.getAllWatch(userMock);
        assertEquals(0, result.size());
        verify(watchListRepository, times(1)).findAllByUser(userMock);
    }

    @Test
    @DisplayName("It must not add a media to watch list if it already exists")
    void shouldNotAddMediaIfAlreadyInWatchList() {
        User userMock = new User("alex", "alex@test.com", "123");
        WatchListCreateDTO watchListData = new WatchListCreateDTO(600L, "movie", "/path.jpg", "Inception");
        when(authentication.getPrincipal()).thenReturn(userMock);
        when(watchListRepository.existsByUserAndTmdbId(userMock, 600L)).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            watchListService.createWatch(watchListData, authentication);
        });
        assertEquals("This media was already define as watch list", exception.getMessage());
        verify(watchListRepository, never()).save(any(WatchList.class));
    }

    @Test
    @DisplayName("It must add a media to watch list successfully")
    void shouldAddMediaToWatchListSuccessfully() {
        User userMock = new User("alex", "alex@test.com", "123");
        WatchListCreateDTO watchListData = new WatchListCreateDTO(600L, "movie", "/path.jpg", "Inception");
        
        when(authentication.getPrincipal()).thenReturn(userMock);
        when(watchListRepository.existsByUserAndTmdbId(userMock, 600L)).thenReturn(false);

        watchListService.createWatch(watchListData, authentication);

        ArgumentCaptor<WatchList> watchListCaptor = ArgumentCaptor.forClass(WatchList.class);

        verify(watchListRepository, times(1)).save(watchListCaptor.capture());

        WatchList savedObject = watchListCaptor.getValue();
        
        assertEquals(watchListData.tmdbId(), savedObject.getTmdbId()); 
        assertEquals(watchListData.videoType(), savedObject.getVideoType());
        assertEquals(watchListData.posterPath(), savedObject.getPosterPath());
        assertEquals(watchListData.title(), savedObject.getTitle());  
        assertEquals(userMock, savedObject.getUser());
    }

    @Test
    @DisplayName("It must remove successfully a watch when it already exists")
    void shouldRemoveWatchedSuccessfully() {
        User user = new User("Alex", "alex@test.com", "123");
        WatchList watchList  = new WatchList(user, 550L, "movie", "path", "Title");
        when(authentication.getPrincipal()).thenReturn(user);
        when(watchListRepository.findByUserAndTmdbId(user, 550L)).thenReturn(Optional.of(watchList));

        watchListService.removeWatch(550L, authentication);
        verify(watchListRepository, times(1)).delete(watchList);
    }

    @Test
    @DisplayName("It must throw an Exception when trying to remove watch that is not in watch list yet")
    void shouldThrowExceptionWhenRemovingNonExistingWatched(){
        User user = new User("Alex", "alex@test.com", "123");
        when(authentication.getPrincipal()).thenReturn(user);
        when(watchListRepository.findByUserAndTmdbId(user, 999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            watchListService.removeWatch(999L, authentication);
        });

        assertEquals("Media not found in watch list", exception.getMessage());
        verify(watchListRepository, never()).delete(any());    
    }
}
