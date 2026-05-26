package org.example.controllers.responses;

import java.util.List;

import org.example.repositories.InteractionTypeStats;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RaoReportResponse {
    List<InteractionTypeStats> interactions;
}
