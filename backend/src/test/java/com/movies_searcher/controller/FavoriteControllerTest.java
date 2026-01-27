package com.movies_searcher.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath; 
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf; 
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.movies_searcher.dto.FavoriteCreateDTO;
import com.movies_searcher.dto.FavoriteResponseDTO;
import com.movies_searcher.model.Favorite;
import com.movies_searcher.model.User;
import com.movies_searcher.repository.FavoriteRepository;
import com.movies_searcher.repository.UserRepository;
import com.movies_searcher.service.FavoriteService;
import com.movies_searcher.service.TokenBlacklistService;
import com.movies_searcher.service.TokenService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;

@WebMvcTest(FavoriteController.class)
public class FavoriteControllerTest {
    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private FavoriteRepository favoriteRepository;

    @MockitoBean
    private FavoriteService favoriteService;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private TokenBlacklistService tokenBlacklistService;

    @Test
    @DisplayName("It must return 201 Created when creating a successfully favorite")
    @WithMockUser(username = "alex", roles = "USER")
    void shouldCreateFavorite() throws Exception {
        FavoriteCreateDTO dto = new FavoriteCreateDTO(500L, "movie", "/img.jpg", "Fight Club");
        Favorite dummyFavorite = new Favorite();
        dummyFavorite.setId(1L);
        dummyFavorite.setTitle("Fight Club");
        when(favoriteService.createFavorite(any(), any())).thenReturn(dummyFavorite);

        mockMvc.perform(post("/api/favorite")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Fight Club"));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request se o DTO for inválido")
    @WithMockUser
    void shouldReturnBadRequestWhenDataIsInvalid() throws Exception {
        FavoriteCreateDTO dtoInvalido = new FavoriteCreateDTO(null, "", "", ""); 

        mockMvc.perform(post("/api/favorite")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoInvalido))
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("It should return all favorites of the signed up user")
    void shouldReturnAllFavorites() throws Exception {
        User user = new User("Alex", "alex@mail.com", "123");
        user.setId(1L);

        UsernamePasswordAuthenticationToken auth = 
            new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        FavoriteResponseDTO dto1 = new FavoriteResponseDTO(1L, 100L, "movie", "/img1.jpg", "Matrix");
        FavoriteResponseDTO dto2 = new FavoriteResponseDTO(2L, 200L, "movie", "/img2.jpg", "Interestelar");
        List<FavoriteResponseDTO> listDtos = List.of(dto1, dto2);
        
        when(favoriteService.listFavorites(user)).thenReturn(listDtos);

       mockMvc.perform(get("/api/favorite")
                .with(authentication(auth)) 
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Matrix"))
                .andExpect(jsonPath("$[1].title").value("Interestelar"));

    }

    @Test
    @DisplayName("It should return no content when removing an existing favorited media")
    void shouldReturnNoContentWhenRemovingAnExistingMedia() throws Exception {
        User user = new User("Alex", "alex@mail.com", "123");
        user.setId(1L);
        
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        doNothing().when(favoriteService).removeFavorite(100L, auth);

        mockMvc.perform(delete("/api/favorite/"+100L)
            .with(authentication(auth))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("It should throw an Exception when removing an non existing favorited media")
    void shouldThrowExceptionWhenRemovingNonExistingFavoritedMedia() throws Exception {
        User user = new User("Alex", "alex@mail.com", "123");
        user.setId(1L);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        doThrow(new RuntimeException("Media not found"))
            .when(favoriteService).removeFavorite(999L, auth);

        mockMvc.perform(delete("/api/favorite/"+999L)
            .with(authentication(auth))
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }
} 
