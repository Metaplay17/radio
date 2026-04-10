package org.example.controllers;

import org.example.aspects.CheckRole;
import org.example.controllers.requests.CreateArtistRequest;
import org.example.controllers.responses.OkResponse;
import org.example.services.ArtistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
public class ArtistController {
    private ArtistService artistService;

    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    @PostMapping("/api/artists")
    @CheckRole(roles = {"ROLE_CONTENT_MANAGER"})
    public ResponseEntity<OkResponse> createArtist(@RequestBody @Valid CreateArtistRequest request) {
        artistService.addArtist(request);
        return ResponseEntity.status(201).body(new OkResponse("Исполнитель успешно добавлен"));
    }
}

