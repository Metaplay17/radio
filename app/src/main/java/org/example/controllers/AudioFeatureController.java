package org.example.controllers;

import org.example.aspects.CheckRole;
import org.example.controllers.requests.CreateAudioFeatureRequest;
import org.example.services.AudioFeatureService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
public class AudioFeatureController {
    private final AudioFeatureService audioFeatureService;

    public AudioFeatureController(AudioFeatureService audioFeatureService) {
        this.audioFeatureService = audioFeatureService;
    }

    @PostMapping("/api/audio-features")
    @CheckRole(roles = {"ROLE_CONTENT_MANAGER"})
    public ResponseEntity<String> createAudioFeature(@RequestBody @Valid CreateAudioFeatureRequest request) {
        audioFeatureService.addAudioFeature(request);
        return ResponseEntity.status(201).body("Признак успешно добавлен");
    }
}
