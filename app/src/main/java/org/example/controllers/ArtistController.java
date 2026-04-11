package org.example.controllers;

import java.util.List;

import org.example.aspects.CheckRole;
import org.example.controllers.requests.CreateArtistRequest;
import org.example.controllers.responses.OkResponse;
import org.example.entities.dto.ArtistDto;
import org.example.services.ArtistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/api/artists")
    @CheckRole(roles = {"ROLE_CONTENT_MANAGER", "ROLE_REDACTOR", "ROLE_LICENSE_MANAGER"})
    public ResponseEntity<List<ArtistDto>> getArtists(@RequestParam(name = "namePattern", required = true) String namePattern, @RequestParam(name = "lastId", required = true) Long lastId) throws MissingServletRequestParameterException {
        return ResponseEntity.ok(artistService.getArtistByNamePattern(namePattern, lastId));
    }
}

