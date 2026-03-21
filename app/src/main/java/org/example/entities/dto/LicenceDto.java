package org.example.entities.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LicenceDto {
    private long id;
    private long trackId;
    private LocalDate registered;
    private Integer duration;
}
