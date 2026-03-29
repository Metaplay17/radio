package org.example.controllers;

import org.example.exceptions.AccessDeniedException;
import org.example.requests.CreateTrackRequest;
import org.example.services.TrackService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public ResponseEntity<String> createTrack(@RequestBody @Valid CreateTrackRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.getAuthorities().stream().anyMatch((GrantedAuthority a) -> a.getAuthority().equals("ROLE_CONTENT_MANAGER"))) {
            throw new AccessDeniedException("Доступно только контент-менеджерам");
        }

        trackService.addTrack(request);
        return ResponseEntity.status(201).body("Трек добавлен");
    }
}
