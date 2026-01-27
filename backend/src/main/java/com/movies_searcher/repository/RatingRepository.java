package com.movies_searcher.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.movies_searcher.model.Rating;
import com.movies_searcher.model.User;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findByUserAndTmdbId(User user, Long tmdbId);
    boolean existsByUserAndTmdbId(User user, Long tmdbId);
    List<Rating> findAllByUser(User user);
}
