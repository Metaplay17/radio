package org.example.controllers;

import java.util.List;

import org.example.aspects.CheckRole;
import org.example.controllers.requests.track.CreateTrackRequest;
import org.example.controllers.responses.OkResponse;
import org.example.entities.dto.TrackDto;
import org.example.services.TrackService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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
    public ResponseEntity<OkResponse> createTrack(@RequestBody @Valid CreateTrackRequest request) {
        trackService.addTrack(request);
        return ResponseEntity.status(201).body(new OkResponse("Трек добавлен"));
    }

    @GetMapping("/api/tracks")
    @CheckRole(roles = {"ROLE_CONTENT_MANAGER", "ROLE_ANALYST", "ROLE_LICENSE_MANAGER", "ROLE_REDACTOR"})
    public ResponseEntity<List<TrackDto>> getTracks(@RequestParam(name = "titlePattern", required = false) String titlePattern, 
        @RequestParam(name = "artistName", required = false) String artist, @RequestParam(name = "lastId", required = true) Long lastId, 
        @RequestParam(name = "genre", required = false) String genre, @RequestParam(name = "isLicensedOnly", required = true) Boolean isLicensedOnly) {
        return ResponseEntity.ok().body(trackService.getTracks(titlePattern, artist, genre, lastId, isLicensedOnly));
    }
}
