package com.movies_searcher.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movies_searcher.dto.WatchListCreateDTO;
import com.movies_searcher.dto.WatchListResponseDTO;
import com.movies_searcher.model.User;
import com.movies_searcher.model.WatchList;
import com.movies_searcher.service.WatchListService;

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
@RequestMapping("/api/watchlist")
public class WatchListController {
    
    private final WatchListService watchListService;
    public WatchListController(WatchListService watchListService) {
        this.watchListService = watchListService;
    }

    @GetMapping()
    public ResponseEntity<List<WatchListResponseDTO>> getAllFavorite(Authentication authetication) {
        User userLogado = (User) authetication.getPrincipal();
        List<WatchListResponseDTO> watchedList = watchListService.getAllWatch(userLogado);

        return ResponseEntity.ok(watchedList);
    }

    @PostMapping()
    public ResponseEntity<WatchListResponseDTO> createFavorite(@RequestBody @Valid WatchListCreateDTO data, Authentication authentication) {
        WatchList savedWatchedList = watchListService.createWatch(data, authentication);
        WatchListResponseDTO responseDTO = new WatchListResponseDTO(savedWatchedList);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }
    
    @DeleteMapping("/{tmdbId}")
    public ResponseEntity<Void> deleteFavorite(@PathVariable Long tmdbId, Authentication authentication) {
        watchListService.removeWatch(tmdbId, authentication);
        return ResponseEntity.noContent().build();
    }
}
