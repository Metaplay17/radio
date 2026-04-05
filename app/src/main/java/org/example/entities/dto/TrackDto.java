package org.example.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TrackDto {
    private long id;
    private String title;
    private String artistName;
    private String genreName;
    private int duration;
}
