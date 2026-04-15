package org.example.controllers.responses;

import java.time.LocalDateTime;
import java.util.List;

import org.example.entities.dto.InteractionAnalyticDto;
import org.example.entities.dto.TrackDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InteractionAnalyticResponse {
    LocalDateTime from;
    LocalDateTime to;
    TrackDto track;
    List<InteractionAnalyticDto> interactions;
}
