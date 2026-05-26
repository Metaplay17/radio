package org.example.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AudioFeatureDto {
    private TrackDto track;
    private int featureTypeId;
    private String name;
    private double value;
}
