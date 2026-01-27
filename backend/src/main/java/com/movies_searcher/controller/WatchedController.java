package com.movies_searcher.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movies_searcher.dto.WatchedCreateDTO;
import com.movies_searcher.dto.WatchedResponseDTO;
import com.movies_searcher.model.User;
import com.movies_searcher.model.Watched;
import com.movies_searcher.service.WatchedService;

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
@RequestMapping("/api/watched")
public class WatchedController {
    
    private final WatchedService watchedService;
    public WatchedController(WatchedService watchedService) {
        this.watchedService = watchedService;
    }

    @GetMapping()
    public ResponseEntity<List<WatchedResponseDTO>> getAllWatched(Authentication authetication) {
        User userLogado = (User) authetication.getPrincipal();
        List<WatchedResponseDTO> watched = watchedService.listWatched(userLogado);

        return ResponseEntity.ok(watched);
    }

    @PostMapping()
    public ResponseEntity<WatchedResponseDTO> createWatched(@RequestBody @Valid WatchedCreateDTO data, Authentication authentication) {
        Watched savedWatched = watchedService.createWatched(data, authentication);
        WatchedResponseDTO responseDTO = new WatchedResponseDTO(savedWatched);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }
    
    @DeleteMapping("/{tmdbId}")
    public ResponseEntity<Void> deleteWatched(@PathVariable Long tmdbId, Authentication authentication) {
        watchedService.removeWatched(tmdbId, authentication);
        return ResponseEntity.noContent().build();
    }
}
