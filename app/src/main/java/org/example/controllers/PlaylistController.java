package org.example.controllers;

import java.time.LocalDate;
import java.util.List;

import org.example.aspects.CheckRole;
import org.example.controllers.requests.playlist.ConfirmPlaylistRequest;
import org.example.controllers.requests.playlist.FormPlaylistRequest;
import org.example.controllers.responses.OkResponse;
import org.example.controllers.responses.PlaylistInfoResponse;
import org.example.controllers.responses.PlaylistResponse;
import org.example.controllers.responses.TrackScoreDto;
import org.example.entities.dto.PlaylistDto;
import org.example.services.PlaylistService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
public class PlaylistController {
    private PlaylistService playlistService;

    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @PostMapping("/api/playlists/form")
    @CheckRole(roles = {"ROLE_REDACTOR"})
    public ResponseEntity<PlaylistResponse> formPlaylist(@RequestBody @Valid FormPlaylistRequest request) {
        List<TrackScoreDto> tracks = playlistService.formPlaylist(request);
        return ResponseEntity.ok(new PlaylistResponse(tracks));
    }

    @PostMapping("/api/playlists")
    @CheckRole(roles = {"ROLE_REDACTOR"})
    public ResponseEntity<OkResponse> confirmPlaylist(@RequestBody @Valid ConfirmPlaylistRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = (String)authentication.getPrincipal();
        playlistService.confirmPlaylist(request, username);
        return ResponseEntity.status(201).body(new OkResponse("Плейлист сформирован"));
    }

    @GetMapping("/api/playlists")
    @CheckRole(roles = {"ROLE_REDACTOR"})
    public ResponseEntity<List<PlaylistDto>> getPlaylists(@RequestParam(name = "date", required = false) LocalDate date, @RequestParam(name = "lastId", required = true) Long lastId) {
        return ResponseEntity.ok().body(playlistService.getPlaylists(date, lastId));
    }

    @GetMapping("/api/playlists/{id}")
    @CheckRole(roles = {"ROLE_REDACTOR"})
    public ResponseEntity<PlaylistInfoResponse> getPlaylistInfo(@PathVariable("id") Integer id) {
        return ResponseEntity.ok().body(playlistService.getPlaylistInfo(id));
    }
}
