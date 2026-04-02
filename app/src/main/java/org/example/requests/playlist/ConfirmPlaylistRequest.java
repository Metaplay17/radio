package org.example.requests.playlist;

import java.util.List;

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
    private List<Long> tracks;
}
