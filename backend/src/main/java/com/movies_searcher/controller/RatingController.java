package com.movies_searcher.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movies_searcher.dto.RatingCreateDTO;
import com.movies_searcher.dto.RatingResponseDTO;
import com.movies_searcher.model.User;
import com.movies_searcher.service.RatingService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/rating")
public class RatingController {
    
    private final RatingService ratingService;
    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @GetMapping("/{tmdbId}")
    public ResponseEntity<RatingResponseDTO> getRating(@PathVariable Long tmdbId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        RatingResponseDTO rating = ratingService.getRating(tmdbId, user);
        return ResponseEntity.ok(rating);
    }

    @GetMapping("/user")
    public ResponseEntity<List<RatingResponseDTO>> getRatingsByUser(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<RatingResponseDTO> ratings = ratingService.getRatingByUser(user);
        return ResponseEntity.ok(ratings);
    }

    @PostMapping()
    public ResponseEntity<RatingResponseDTO> createRating(@RequestBody @Valid RatingCreateDTO data,Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        RatingResponseDTO rating = ratingService.saveRating(data, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(rating);
    }

    @DeleteMapping("/{tmdbId}")
    public ResponseEntity<Void> deleteFavorite(@PathVariable Long tmdbId, Authentication authentication) {
        ratingService.removeRating(tmdbId, authentication);
        return ResponseEntity.noContent().build();
    }
    
}
