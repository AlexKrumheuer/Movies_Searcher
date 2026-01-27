package com.movies_searcher.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RatingCreateDTO(
    @NotNull(message="Rating must not be null")
    Integer rating,
    @NotNull(message="Tmdb Id must not be null")
    Long tmdbId,
    @NotBlank(message="Video type must not be null")
    String videoType
) {
    
}
