package org.example.controllers;

import java.util.List;

import org.example.aspects.CheckRole;
import org.example.requests.FormPlaylistRequest;
import org.example.responses.PlaylistResponse;
import org.example.responses.TrackScoreDto;
import org.example.services.PlaylistService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
public class PlaylistController {
    private PlaylistService playlistService;

    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @PostMapping("/api/playlists/form")
    @CheckRole(roles = {"ROLE_CONTENT_MANAGER"})
    public ResponseEntity<PlaylistResponse> formPlaylist(@RequestBody @Valid FormPlaylistRequest request) {
        List<TrackScoreDto> tracks = playlistService.formPlaylist(request);
        return ResponseEntity.ok(new PlaylistResponse(tracks));
    }
}
