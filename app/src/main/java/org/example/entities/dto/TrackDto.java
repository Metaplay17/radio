package org.example.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TrackDto {
    private long id;
    private String title;
    private int artistId;
    private int genreId;
    private int duration;
}
