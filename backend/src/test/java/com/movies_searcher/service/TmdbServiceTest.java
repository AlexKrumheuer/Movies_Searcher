package com.movies_searcher.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.hamcrest.Matchers.containsString;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

class TmdbServiceTest {

    private TmdbService tmdbService;
    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        
        mockServer = MockRestServiceServer.bindTo(builder).build();
        
        tmdbService = new TmdbService(builder, "minha-key-teste-123");
    }

    @Test
    @DisplayName("Should search content by name correctly")
    void shouldSearchContentByName() {
        String jsonResponse = """
                {
                    "results": [
                        {"original_title": "Matrix", "id": 1}
                    ]
                }
                """;


        mockServer.expect(requestTo(containsString("/search/movie"))) 
                  .andExpect(queryParam("query", "Matrix")) 
                  .andExpect(queryParam("api_key", "minha-key-teste-123")) 
                  .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON)); 

        String resultado = tmdbService.buscarConteudoPorNome("Matrix", "movie", 1);

        assertNotNull(resultado);
        assertEquals(jsonResponse, resultado); 
        
        mockServer.verify(); 
    }

    @Test
    @DisplayName("Should apply the filter corrtely for top rated content")
    void shouldDiscoverTopRatedContent() {


        String jsonResposta = "{\"results\": []}";

        mockServer.expect(requestTo(containsString("/discover/movie")))
                  .andExpect(queryParam("sort_by", "vote_average.desc")) 
                  .andExpect(queryParam("vote_count.gte", "200"))
                  .andRespond(withSuccess(jsonResposta, MediaType.APPLICATION_JSON));

        String resultado = tmdbService.descobrirConteudo("movie", 1, "28", "top_rated");

        assertEquals(jsonResposta, resultado);
        mockServer.verify();
    }

    @Test
    @DisplayName("Should apply popularity sorting when category is unknown")
    void shouldDiscoverPopularContentByDefault() {

        String jsonResposta = "{\"results\": []}";

        mockServer.expect(requestTo(containsString("/discover/movie")))
                  .andExpect(queryParam("sort_by", "popularity.desc")) 
                  .andRespond(withSuccess(jsonResposta, MediaType.APPLICATION_JSON));

        String resultado = tmdbService.descobrirConteudo("movie", 1, "28", "categoria_qualquer");

        assertEquals(jsonResposta, resultado);
        mockServer.verify();
    }

    @Test
    @DisplayName("Should get specific content details with credits and videos")
    void shouldGetSpecificContentWithAppend() {
        
        String jsonResponse = "{\"title\": \"Filme Detalhado\", \"id\": 550}";
        Long filmeId = 550L;

        mockServer.expect(requestTo(containsString("/movie/" + filmeId))) 
                  .andExpect(queryParam("append_to_response", "credits,videos"))
                  .andExpect(queryParam("api_key", "minha-key-teste-123"))
                  .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        String resultado = tmdbService.conteudoEspecificoService("movie", filmeId);

        assertEquals(jsonResponse, resultado);
        mockServer.verify();
    }

    @Test
    @DisplayName("Should get genres list correctly")
    void shouldGetGenresList() {
        
        String jsonResponse = "{\"genres\": [{\"id\": 28, \"name\": \"Action\"}]}";

        mockServer.expect(requestTo(containsString("/genre/movie/list")))
                  .andExpect(queryParam("api_key", "minha-key-teste-123"))
                  .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        String resultado = tmdbService.generosService("movie");

        assertEquals(jsonResponse, resultado);
        mockServer.verify();
    }

    @Test
    @DisplayName("Should NOT apply any sorting for upcoming or now_playing categories")
    void shouldNotSortForUpcomingContent() {
        
        String jsonResponse = "{\"results\": []}";

        mockServer.expect(requestTo(containsString("/discover/movie")))
                  .andRespond(withSuccess(jsonResponse, MediaType.APPLICATION_JSON));

        String resultado = tmdbService.descobrirConteudo("movie", 1, "28", "upcoming");

        assertEquals(jsonResponse, resultado);
        mockServer.verify();
    }
}