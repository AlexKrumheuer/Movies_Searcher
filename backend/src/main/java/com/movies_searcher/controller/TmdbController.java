package com.movies_searcher.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.movies_searcher.service.TmdbService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/tmdb")
public class TmdbController {
    private final TmdbService tmdbService;
    
    public TmdbController(TmdbService tmdbService) {
        this.tmdbService = tmdbService;
    }

    @GetMapping("/{tipo}/search")
    public String buscarPorNome(
        @PathVariable String tipo,
        @RequestParam String query,
        @RequestParam(defaultValue = "1") int page
    ) {
        return tmdbService.buscarConteudoPorNome(query, tipo, page);
    }

    @GetMapping("/{tipo}/discover")
    public String descobrirConteudo(
        @PathVariable String tipo,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(required = false) String idsGenres,
        @RequestParam(defaultValue = "popular") String  category
    ) {
        return tmdbService.descobrirConteudo(tipo, page, idsGenres, category);
    }   
    
    @GetMapping("/{tipo}/{id}")
    public String conteudoEspecifico(
        @PathVariable String tipo,
        @PathVariable Long id
    ) {
        return tmdbService.conteudoEspecificoService(tipo, id);
    }

    @GetMapping("/genre/{tipo}/list")
    public String listarGeneros(@PathVariable String tipo) {
        return tmdbService.generosService(tipo);
    }
}
