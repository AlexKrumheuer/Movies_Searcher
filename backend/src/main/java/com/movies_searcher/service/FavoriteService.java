package com.movies_searcher.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.movies_searcher.dto.FavoriteCreateDTO;
import com.movies_searcher.dto.FavoriteResponseDTO;
import com.movies_searcher.model.Favorite;
import com.movies_searcher.model.User;
import com.movies_searcher.repository.FavoriteRepository;

@Service
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    public FavoriteService(FavoriteRepository favoriteRepository) {
        this.favoriteRepository = favoriteRepository;
    }

    public List<FavoriteResponseDTO> listFavorites(User user) {
        List<Favorite> favorites = favoriteRepository.findAllByUser(user);

        return favorites.stream()
            .map(fav -> new FavoriteResponseDTO(
                fav.getId(),
                fav.getTmdbId(),
                fav.getVideoType(),
                fav.getPosterPath(),
                fav.getTitle()
            ))
            .collect(Collectors.toList());
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
