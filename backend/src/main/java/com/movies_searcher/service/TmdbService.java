package com.movies_searcher.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class TmdbService {
    @Value("${api.key}")
    private String apiKey; // Get api key

    private final RestClient restClient; // Instanciate http client
    
    public TmdbService() {
        
        this.restClient = RestClient.builder()
            .baseUrl("https://api.themoviedb.org/3")
            .build();
    }

    // Get Content by name
    public String buscarConteudoPorNome(String nome, String tipo, int page) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                                    .path("/search/{tipo}")
                                    .queryParam("page", page)
                                    .queryParam("query", nome)
                                    .queryParam("api_key", apiKey)
                                    .queryParam("language", "pt-BR")
                                    .queryParam("include_adult","false")
                                    .build(tipo))
                .retrieve()
                .body(String.class);
    }

    // discover content
    public String descobrirConteudo(
        String tipo,
        int page,
        String idsGenres,
        String category
    ){
        return restClient.get()
                .uri(uriBuilder -> {
                    var builder = uriBuilder
                                    .path("/discover/{tipo}")
                                    .queryParam("page", page)
                                    .queryParam("api_key", apiKey)
                                    .queryParam("with_genres", idsGenres)
                                    .queryParam("language", "pt-BR")
                                    .queryParam("include_adult","false");
                    if("top_rated".equals(category)) {
                        builder.queryParam("sort_by", "vote_average.desc")
                                .queryParam("vote_count.gte", "200");
                    } else if(List.of("upcoming", "now_playing","on_the_air").contains(category)) {
                    } else {
                        builder.queryParam("sort_by", "popularity.desc");
                    }
                
                    return builder.build(tipo);
                })
                .retrieve()
                .body(String.class);
    }

    // Content specific by service
    public String conteudoEspecificoService(
        String tipo, Long id
    ) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                                    .path("/{tipo}/{id}")
                                    .queryParam("append_to_response", "credits,videos")
                                    .queryParam("api_key", apiKey)
                                    .queryParam("language", "pt-BR")
                                    .queryParam("include_adult","false")
                                    .build(tipo, id))
                .retrieve()
                .body(String.class); 
    }


    // Genders
    public String generosService(
        String tipo
    ) {
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                                    .path("/genre/{tipo}/list")
                                    .queryParam("api_key", apiKey)
                                    .queryParam("language", "pt-BR")
                                    .queryParam("include_adult","false")
                                    .build(tipo))
                .retrieve()
                .body(String.class); 
    }

}

