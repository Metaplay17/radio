package org.example.controllers;

import org.example.aspects.CheckRole;
import org.example.requests.CreateAudioFeatureTypeRequest;
import org.example.services.AudioFeatureTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
public class AudioFeatureTypeController {
    private final AudioFeatureTypeService audioFeatureTypeService;

    public AudioFeatureTypeController(AudioFeatureTypeService audioFeatureTypeService) {
        this.audioFeatureTypeService = audioFeatureTypeService;
    }

    @PostMapping("/api/audio-feature-types")
    @CheckRole(roles = {"ROLE_CONTENT_MANAGER"})
    public ResponseEntity<String> createAudioFeatureType(@RequestBody @Valid CreateAudioFeatureTypeRequest request) {
        audioFeatureTypeService.addAudioFeatureType(request);
        return ResponseEntity.status(201).body("Тип признака успешно добавлен");
    }
}
