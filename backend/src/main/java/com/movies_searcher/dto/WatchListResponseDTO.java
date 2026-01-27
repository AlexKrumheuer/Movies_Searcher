package com.movies_searcher.dto;

import com.movies_searcher.model.WatchList;

public record WatchListResponseDTO(
    Long id,
    Long tmdbId,
    String videoType,
    String posterPath,
    String title
) {
    public WatchListResponseDTO(WatchList watchedList) {
        this(
            watchedList.getId(),
            watchedList.getTmdbId(),
            watchedList.getVideoType(),
            watchedList.getPosterPath(),
            watchedList.getTitle()
        );
    }
}
