package org.example.controllers.requests.playlist;

import java.time.LocalDateTime;
import java.util.List;

import org.example.controllers.requests.track.TrackSignature;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConfirmPlaylistRequest {
    @NotEmpty(message = "Название плейлиста не может быть пустым")
    private String name;
    private String description;
    
    @NotEmpty(message = "Содержание плейлиста не может быть пустым")
    private List<TrackSignature> tracks;

    @FutureOrPresent
    private LocalDateTime datetime;
}
