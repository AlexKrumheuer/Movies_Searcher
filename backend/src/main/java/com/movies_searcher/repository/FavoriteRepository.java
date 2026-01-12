package com.movies_searcher.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.movies_searcher.model.Favorite;
import com.movies_searcher.model.User;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findAllByUser(User user);
    boolean existsByUserAndTmdbId(User user, Long tmdbId);
    Optional<Favorite> findByUserAndTmdbId(User user, Long tmdbId);
}
