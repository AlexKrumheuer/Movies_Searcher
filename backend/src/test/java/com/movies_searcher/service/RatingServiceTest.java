package com.movies_searcher.service;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import com.movies_searcher.dto.RatingCreateDTO;
import com.movies_searcher.dto.RatingResponseDTO;
import com.movies_searcher.model.Rating;
import com.movies_searcher.model.User;
import com.movies_searcher.repository.RatingRepository;

@ExtendWith(MockitoExtension.class)
public class RatingServiceTest {
    @Mock
    private RatingRepository ratingRepository;

    @InjectMocks
    private RatingService ratingService;

    @Test
    @DisplayName("Should return Rating Response DTO when getRating is called with valid tmdbId and user")
    void shouldReturnRatingWhenFound() {
        User userMock = new User("alex", "alex@test.com", "123");
        Long tmdbId = 1L;    
        
        Rating ratingMock = new Rating();
        ratingMock.setId(1L);
        ratingMock.setRating(8);
        ratingMock.setTmdbId(tmdbId);
        ratingMock.setVideoType("movie");

        when(ratingRepository.findByUserAndTmdbId(userMock, tmdbId))
            .thenReturn(Optional.of(ratingMock));

        RatingResponseDTO result = ratingService.getRating(tmdbId, userMock);

        assertThat(result).isNotNull();
        assertEquals(8, result.rating());
        assertEquals(tmdbId, result.tmdbId());
        assertEquals("movie", result.videoType());

        verify(ratingRepository, times(1)).findByUserAndTmdbId(userMock, tmdbId);
    }

    @Test
    @DisplayName("Should throw RuntimeException when getRating is called with invalid tmdbId")
    void shouldThrowExceptionWhenRatingNotFound() {
        User userMock = new User("alex", "alex@test.com", "123");
        Long tmdbId = 999L;    

        when(ratingRepository.findByUserAndTmdbId(userMock, tmdbId))
            .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ratingService.getRating(tmdbId, userMock);
        });

        assertEquals("Rating not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should return a List of Rating Response DTOs when getRatingByUser is called")
    void shouldReturnRatingsByUser() {
        User userMock = new User("alex", "alex@test.com", "123");

        Rating rating1 = new Rating();
        rating1.setId(1L);
        rating1.setRating(8);
        rating1.setTmdbId(1L);
        rating1.setVideoType("movie");

        Rating rating2 = new Rating();
        rating2.setId(2L);
        rating2.setRating(10);
        rating2.setTmdbId(2L);
        rating2.setVideoType("tv");


        List<Rating> ratingsMock = List.of(
            rating1,
            rating2
        );

        when(ratingRepository.findAllByUser(userMock))
            .thenReturn(ratingsMock);

        List<RatingResponseDTO> result = ratingService.getRatingByUser(userMock);

        assertEquals(2, result.size());
        assertEquals(8, result.get(0).rating());
        assertEquals(10, result.get(1).rating());
        assertEquals(1L, result.get(0).tmdbId());
        assertEquals(2L, result.get(1).tmdbId());
        assertEquals("movie", result.get(0).videoType());
        assertEquals("tv", result.get(1).videoType());
        verify(ratingRepository, times(1)).findAllByUser(userMock);        
    }

    @Test
    @DisplayName("Should return an empty list when getRatingByUser is called and user has no ratings")
    void shouldReturnEmptyListWhenUserHasNoRatings() {
        User userMock = new User("alex", "alex@test.com", "123");
        when(ratingRepository.findAllByUser(userMock))
            .thenReturn(List.of());

        List<RatingResponseDTO> result = ratingService.getRatingByUser(userMock);

        assertThat(result).isEmpty();
        assertNotNull(result);
        verify(ratingRepository, times(1)).findAllByUser(userMock);
    }

    @Test
    @DisplayName("Should save and return Rating Response DTO when saveRating is called")
    void shouldSaveAndReturnRating() {
        User userMock = new User("alex", "alex@test.com", "123");
        RatingCreateDTO inputDTO = new RatingCreateDTO(9, 1L, "movie");

        Rating ratingSaved = new Rating();
        ratingSaved.setId(1L);
        ratingSaved.setRating(9);
        ratingSaved.setTmdbId(1L);
        ratingSaved.setVideoType("movie");
        ratingSaved.setUser(userMock);


        when(ratingRepository.findByUserAndTmdbId(userMock, 1L))
            .thenReturn(Optional.empty());

        when(ratingRepository.save(any(Rating.class)))
            .thenReturn(ratingSaved);

        RatingResponseDTO result = ratingService.saveRating(inputDTO, userMock);

        assertEquals(9, result.rating());
        assertEquals(1L, result.tmdbId());
        assertEquals("movie", result.videoType());
        
        verify(ratingRepository, times(1)).save(any(Rating.class));
    }

    @Test
    @DisplayName("Should update and return Rating Response DTO when saveRating is called for existing rating")
    void shouldUpdateAndReturnRating() {
        User userMock = new User("alex", "alex@test.com", "123");
        RatingCreateDTO inputDTO = new RatingCreateDTO(10, 550L, "movie");
        Rating existingRating = new Rating();
        existingRating.setId(1L);
        existingRating.setRating(8);
        existingRating.setTmdbId(550L);
        existingRating.setVideoType("movie");
        existingRating.setUser(userMock);

        when(ratingRepository.findByUserAndTmdbId(userMock, 550L))
            .thenReturn(Optional.of(existingRating));

        when(ratingRepository.save(any(Rating.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));   

        RatingResponseDTO result = ratingService.saveRating(inputDTO, userMock);

        assertEquals(1L, result.id());
        assertEquals(10L, result.rating());

        verify(ratingRepository, times(1)).save(existingRating);
    }

    @Test
    @DisplayName("Should delete rating when removeRating is called with valid tmdbId and authentication")
    void shouldDeleteRatingWhenFound() {
        Long tmdbId = 550L;
        User userMock = new User("alex", "alex@test.com", "123");
        
        Rating deletedRating = new Rating();
        deletedRating.setId(1L);
        deletedRating.setRating(9);
        deletedRating.setTmdbId(tmdbId);
        deletedRating.setVideoType("movie");
        deletedRating.setUser(userMock);

        Authentication authMock = Mockito.mock(Authentication.class);

        when(authMock.getPrincipal()).thenReturn(userMock);

        when(ratingRepository.findByUserAndTmdbId(userMock, tmdbId))
            .thenReturn(Optional.of(deletedRating));

        ratingService.removeRating(tmdbId, authMock);


        verify(ratingRepository, times(1)).delete(deletedRating);
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar remover rating que não existe")
    void shouldThrowExceptionWhenRemovingNonExistentRating() {
        Long tmdbId = 999L;
        User userMock = new User("alex", "alex@test.com", "123");

        Authentication authMock = Mockito.mock(Authentication.class);
        when(authMock.getPrincipal()).thenReturn(userMock);

        when(ratingRepository.findByUserAndTmdbId(userMock, tmdbId))
            .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ratingService.removeRating(tmdbId, authMock);
        });

        assertEquals("Media not rated", exception.getMessage());
        
        verify(ratingRepository, times(0)).delete(any());
    }
}
