package com.movies_searcher.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record WatchListCreateDTO (
    @NotNull(message="Tmdb Id must not be blank")
    Long tmdbId,
    @NotBlank(message="video type must not be blank")
    String videoType,
    @NotBlank(message="Poster path must not be blank")
    String posterPath,
    @NotBlank(message="Title must not be blank")
    String title
) {
    
}
