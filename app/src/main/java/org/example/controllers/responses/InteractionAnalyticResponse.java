package org.example.controllers.responses;

import java.util.List;

import org.example.entities.dto.InteractionAnalyticDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InteractionAnalyticResponse {
    List<InteractionAnalyticDto> interactions;
}
