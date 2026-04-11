package org.example.controllers;

import java.util.List;

import org.example.aspects.CheckRole;
import org.example.controllers.requests.CreateGenreRequest;
import org.example.controllers.responses.OkResponse;
import org.example.entities.dto.GenreDto;
import org.example.services.GenreService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    public ResponseEntity<OkResponse> createGenre(@RequestBody @Valid CreateGenreRequest request) {

        genreService.addGenre(request);
        return ResponseEntity.status(201).body(new OkResponse("Жанр успешно добавлен"));
    }

    @GetMapping("/api/genres")
    @CheckRole(roles = {"ROLE_CONTENT_MANAGER", "ROLE_REDACTOR", "ROLE_ANALYST"})
    public ResponseEntity<List<GenreDto>> getGenres() {
        return ResponseEntity.ok(genreService.getAllGenres());
    }
}
