package org.example.controllers;

import org.example.aspects.CheckRole;
import org.example.requests.CreateGenreRequest;
import org.example.services.GenreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
public class GenreController {
    
    public GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @PostMapping("/api/genres")
    @CheckRole(roles = {"ROLE_CONTENT_MANAGER"})
    public ResponseEntity<String> createGenre(@RequestBody @Valid CreateGenreRequest request) {

        genreService.addGenre(request);
        return ResponseEntity.status(201).body("Жанр успешно добавлен");
    }
}
