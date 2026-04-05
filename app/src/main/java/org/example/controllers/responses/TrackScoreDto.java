package org.example.controllers.responses;

import org.example.entities.dto.TrackDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TrackScoreDto {
    private TrackDto track;
    private Double score;
}
