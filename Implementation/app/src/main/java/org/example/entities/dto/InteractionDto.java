package org.example.entities.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InteractionDto {
    private long id;
    private TrackDto track;
    private String context;
    private int interactionTypeId;
    private LocalDateTime datetime;
}
