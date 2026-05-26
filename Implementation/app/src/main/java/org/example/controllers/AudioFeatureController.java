package org.example.controllers;

import java.util.List;

import org.example.aspects.CheckRole;
import org.example.controllers.requests.CreateAudioFeatureRequest;
import org.example.controllers.responses.OkResponse;
import org.example.entities.dto.AudioFeatureDto;
import org.example.services.AudioFeatureService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
public class AudioFeatureController {
    private final AudioFeatureService audioFeatureService;

    public AudioFeatureController(AudioFeatureService audioFeatureService) {
        this.audioFeatureService = audioFeatureService;
    }

    @PostMapping("/api/audio-features")
    @CheckRole(roles = {"ROLE_ANALYST"})
    public ResponseEntity<OkResponse> createAudioFeature(@RequestBody @Valid CreateAudioFeatureRequest request) throws MissingServletRequestParameterException {
        audioFeatureService.addAudioFeature(request);
        return ResponseEntity.status(201).body(new OkResponse("Признак успешно добавлен"));
    }

    @GetMapping("/api/audio-features")
    @CheckRole(roles = {"ROLE_ANALYST"})
    public ResponseEntity<List<AudioFeatureDto>> getAudioFeatures(@RequestParam(name = "trackTitle", required = true) String trackTitle, 
    @RequestParam(name = "artistName", required = true) String artistName, @RequestParam(name = "lastId", required = true) Long lastId) throws MissingServletRequestParameterException {
        return ResponseEntity.ok(audioFeatureService.getAudioFeatures(trackTitle, artistName, lastId));
    }
}
