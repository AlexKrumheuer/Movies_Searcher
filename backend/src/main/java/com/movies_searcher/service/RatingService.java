package com.movies_searcher.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.movies_searcher.dto.RatingCreateDTO;
import com.movies_searcher.dto.RatingResponseDTO;
import com.movies_searcher.model.Rating;
import com.movies_searcher.model.User;
import com.movies_searcher.repository.RatingRepository;

@Service
public class RatingService {
    private final RatingRepository ratingRepository;
    public RatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    public RatingResponseDTO getRating(Long tmdbId, User user) {
        Rating rating = ratingRepository.findByUserAndTmdbId(user, tmdbId)
                            .orElseThrow(() -> new RuntimeException("Rating not found"));
        return new RatingResponseDTO(
            rating.getId(),
            rating.getRating(),
            rating.getTmdbId(),
            rating.getVideoType() 
        );
    }

    public List<RatingResponseDTO> getRatingByUser(User user) {
        List<Rating> ratings = ratingRepository.findAllByUser(user);
        return ratings.stream()
            .map(rating -> new RatingResponseDTO(
                rating.getId(),
                rating.getRating(),
                rating.getTmdbId(),
                rating.getVideoType() 
            ))
            .toList();
    }

    public RatingResponseDTO saveRating(RatingCreateDTO data, User user) {
        Rating rating = ratingRepository.findByUserAndTmdbId(user, data.tmdbId())
        .orElseGet(() -> {
            Rating newRating = new Rating();
            newRating.setUser(user);
            newRating.setTmdbId(data.tmdbId());
            newRating.setVideoType(data.videoType());
            return newRating;
        });

        rating.setRating(data.rating());
        Rating savedRating = ratingRepository.save(rating);

        return new RatingResponseDTO(
            savedRating.getId(),
            savedRating.getRating(),
            savedRating.getTmdbId(),
            savedRating.getVideoType() 
        );
    }

    public void removeRating(Long tmdbId, Authentication authentication) {
        User userLogged = (User) authentication.getPrincipal();
        Rating rating = ratingRepository.findByUserAndTmdbId(userLogged, tmdbId)
                .orElseThrow(() -> new RuntimeException("Media not rated"));
        ratingRepository.delete(rating);
    }        
}
