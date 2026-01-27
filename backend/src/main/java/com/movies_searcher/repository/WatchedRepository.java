package com.movies_searcher.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.movies_searcher.model.User;
import com.movies_searcher.model.Watched;

@Repository
public interface WatchedRepository extends JpaRepository<Watched, Long> {
    List<Watched> findAllByUser(User user);
    boolean existsByUserAndTmdbId(User user, Long tmdbId);
    Optional<Watched> findByUserAndTmdbId(User user, Long tmdbId);
}
