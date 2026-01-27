package com.movies_searcher.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.movies_searcher.dto.WatchListCreateDTO;
import com.movies_searcher.dto.WatchListResponseDTO;
import com.movies_searcher.model.User;
import com.movies_searcher.model.WatchList;
import com.movies_searcher.repository.WatchListRepository;

@Service
public class WatchListService {
    private final WatchListRepository watchListRepository;
    public WatchListService(WatchListRepository watchListRepository) {
        this.watchListRepository = watchListRepository;
    }

    public List<WatchListResponseDTO> getAllWatch(User user) {
        List<WatchList> watchList = watchListRepository.findAllByUser(user);

        return watchList.stream()
            .map(wat -> new WatchListResponseDTO(
                wat.getId(),
                wat.getTmdbId(),
                wat.getVideoType(),
                wat.getPosterPath(),
                wat.getTitle()
            ))
            .collect(Collectors.toList());
    }

    public WatchList createWatch(WatchListCreateDTO data, Authentication authentication){
        User loggedUser = (User) authentication.getPrincipal();
        if(watchListRepository.existsByUserAndTmdbId(loggedUser, data.tmdbId())){
            throw new RuntimeException("This media was already define as watch list");
        }
        WatchList watchList = new WatchList(loggedUser, data.tmdbId(), data.videoType(), data.posterPath(), data.title());
        watchListRepository.save(watchList);
        return watchList;
    }

    public void removeWatch(Long tmdbId, Authentication authentication) {
        User userLogged = (User) authentication.getPrincipal();
        WatchList watchList = watchListRepository.findByUserAndTmdbId(userLogged, tmdbId)
                .orElseThrow(() -> new RuntimeException("Media not found in watch list"));
        watchListRepository.delete(watchList);
    }        
 }
