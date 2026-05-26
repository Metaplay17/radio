package org.example.entities.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LogDto {
    private String message;
    private LocalDateTime datetime;
    private String initiatorUsername;
}
