package org.example.entities.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlaylistDto {
    private int id;
    private String name;
    private String description;
    private LocalDateTime datetime;
    private int duration;
}
