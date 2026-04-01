package org.example.controllers;

import org.example.aspects.CheckRole;
import org.example.requests.CreateTrackRequest;
import org.example.services.TrackService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
public class TrackController {
    private TrackService trackService;

    public TrackController(TrackService trackService) {
        this.trackService = trackService;
    }

    @PostMapping("/api/tracks")
    @CheckRole(roles = {"ROLE_CONTENT_MANAGER"})
    public ResponseEntity<String> createTrack(@RequestBody @Valid CreateTrackRequest request) {
        trackService.addTrack(request);
        return ResponseEntity.status(201).body("Трек добавлен");
    }
}
