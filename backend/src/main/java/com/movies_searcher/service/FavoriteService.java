package com.movies_searcher.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.movies_searcher.dto.FavoriteCreateDTO;
import com.movies_searcher.model.Favorite;
import com.movies_searcher.model.User;
import com.movies_searcher.repository.FavoriteRepository;

@Service
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    public FavoriteService(FavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }

    public List<Favorite> listFavorites(User user) {
        return favoriteRepository.findAllByUser(user);
    }

    public Favorite createFavorite(FavoriteCreateDTO data, Authentication authentication){
        User loggedUser = (User) authentication.getPrincipal();
        if(favoriteRepository.existsByUserAndTmdbId(loggedUser, data.tmdbId())){
            throw new RuntimeException("This media was already define as favorite");
        }
        Favorite favorite = new Favorite(loggedUser, data.tmdbId(), data.videoType(), data.posterPath(), data.title());
        favoriteRepository.save(favorite);
        return favorite;
    }

    public void removeFavorite(Long tmdbId, Authentication authentication) {
        User userLogged = (User) authentication.getPrincipal();
        Favorite favorite = favoriteRepository.findByUserAndTmdbId(userLogged, tmdbId)
                .orElseThrow(() -> new RuntimeException("Media not found in favorites"));
        favoriteRepository.delete(favorite);
    }        
 }
