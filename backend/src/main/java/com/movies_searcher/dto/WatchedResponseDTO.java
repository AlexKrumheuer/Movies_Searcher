package com.movies_searcher.dto;

import com.movies_searcher.model.Watched;

public record WatchedResponseDTO(
    Long id,
    Long tmdbId,
    String videoType,
    String posterPath,
    String title
) {
    public WatchedResponseDTO(Watched watched) {
        this(
            watched.getId(),
            watched.getTmdbId(),
            watched.getVideoType(),
            watched.getPosterPath(),
            watched.getTitle()
        );
    }
}
