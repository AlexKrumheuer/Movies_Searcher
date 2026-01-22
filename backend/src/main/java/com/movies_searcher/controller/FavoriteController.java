package com.movies_searcher.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movies_searcher.dto.FavoriteCreateDTO;
import com.movies_searcher.dto.FavoriteResponseDTO;
import com.movies_searcher.model.Favorite;
import com.movies_searcher.model.User;
import com.movies_searcher.service.FavoriteService;

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
@RequestMapping("/api/favorite")
public class FavoriteController {
    
    private final FavoriteService favoriteService;
    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @GetMapping()
    public ResponseEntity<List<FavoriteResponseDTO>> getAllFavorite(Authentication authetication) {
        User userLogado = (User) authetication.getPrincipal();
        List<FavoriteResponseDTO> favoritesList = favoriteService.listFavorites(userLogado);

        return ResponseEntity.ok(favoritesList);
    }

    @PostMapping()
    public ResponseEntity<FavoriteResponseDTO> createFavorite(@RequestBody @Valid FavoriteCreateDTO data, Authentication authentication) {
        Favorite savedFavorite = favoriteService.createFavorite(data, authentication);
        FavoriteResponseDTO responseDTO = new FavoriteResponseDTO(savedFavorite);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }
    
    @DeleteMapping("/{tmdbId}")
    public ResponseEntity<Void> deleteFavorite(@PathVariable Long tmdbId, Authentication authentication) {
        favoriteService.removeFavorite(tmdbId, authentication);
        return ResponseEntity.noContent().build();
    }
}
