package com.movies_searcher.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.movies_searcher.model.User;
import com.movies_searcher.model.WatchList;

public interface WatchListRepository extends JpaRepository<WatchList, Long> {
    List<WatchList> findAllByUser(User user);
    boolean existsByUserAndTmdbId(User user, Long tmdbId);
    Optional<WatchList> findByUserAndTmdbId(User user, Long tmdbId);
}
