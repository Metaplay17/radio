package org.example.controllers.responses;

import java.time.LocalDateTime;
import java.util.List;

import org.example.entities.dto.TrackDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlaylistInfoResponse {
    private Integer id;
    private String name;
    private String description;
    private Integer duration;
    private LocalDateTime datetime;
    private List<TrackDto> tracks;
}
