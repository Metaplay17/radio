package org.example.entities.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InteractionAnalyticDto {
    private TrackDto track;
    private String context;
    private String interactionType;
    private LocalDateTime datetime;
}
