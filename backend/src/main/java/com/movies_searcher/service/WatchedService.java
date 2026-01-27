package com.movies_searcher.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.movies_searcher.dto.WatchedCreateDTO;
import com.movies_searcher.dto.WatchedResponseDTO;
import com.movies_searcher.model.User;
import com.movies_searcher.model.Watched;
import com.movies_searcher.repository.WatchedRepository;

@Service
public class WatchedService {
    private final WatchedRepository watchedRepository;
    public WatchedService(WatchedRepository watchedRepository) {
        this.watchedRepository = watchedRepository;
    }

    public List<WatchedResponseDTO> listWatched(User user) {
        List<Watched> watched = watchedRepository.findAllByUser(user);

        return watched.stream()
            .map(wat -> new WatchedResponseDTO(
                wat.getId(),
                wat.getTmdbId(),
                wat.getVideoType(),
                wat.getPosterPath(),
                wat.getTitle()
            ))
            .collect(Collectors.toList());
    }

    public Watched createWatched(WatchedCreateDTO data, Authentication authentication){
        User loggedUser = (User) authentication.getPrincipal();
        if(watchedRepository.existsByUserAndTmdbId(loggedUser, data.tmdbId())){
            throw new RuntimeException("This media was already define as watched");
        }
        Watched watched = new Watched(loggedUser, data.tmdbId(), data.videoType(), data.posterPath(), data.title());
        watchedRepository.save(watched);
        return watched;
    }

    public void removeWatched(Long tmdbId, Authentication authentication) {
        User userLogged = (User) authentication.getPrincipal();
        Watched watched = watchedRepository.findByUserAndTmdbId(userLogged, tmdbId)
                .orElseThrow(() -> new RuntimeException("Media not found in watched"));
        watchedRepository.delete(watched);
    }        
 }
