package org.example.entities.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AudioFeatureDto {
    private long id;
    private long trackId;
    private int featureTypeId;
    private double value;
}
