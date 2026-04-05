package org.example.controllers.responses;

import java.util.List;

import org.example.entities.dto.LogDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LogsResponse {
    private List<LogDto> logs;
}
