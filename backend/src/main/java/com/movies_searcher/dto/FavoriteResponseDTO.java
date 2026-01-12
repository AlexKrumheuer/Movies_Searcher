package com.movies_searcher.dto;

import com.movies_searcher.model.Favorite;

public record FavoriteResponseDTO(
    Long id,
    Long tmdbId,
    String videoType,
    String posterPath,
    String title
) {
    public FavoriteResponseDTO(Favorite favorite) {
        this(
            favorite.getId(),
            favorite.getTmdbId(),
            favorite.getVideoType(),
            favorite.getPosterPath(),
            favorite.getTitle()
        );
    }
}
