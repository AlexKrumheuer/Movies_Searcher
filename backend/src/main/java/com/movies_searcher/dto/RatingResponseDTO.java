package com.movies_searcher.dto;

import com.movies_searcher.model.Rating;

public record RatingResponseDTO(
    Long id,
    int rating,
    Long tmdbId,
    String videoType
) {
    public RatingResponseDTO(Rating rating) {
        this(
            rating.getId(),
            rating.getRating(),
            rating.getTmdbId(),
            rating.getVideoType()
        );
    }
}
