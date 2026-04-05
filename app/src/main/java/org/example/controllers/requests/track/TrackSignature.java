package org.example.controllers.requests.track;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TrackSignature {
    @NotEmpty(message = "Название не может быть пустым")
    private String title;

    @NotEmpty(message = "Исполнитель не может быть пустым")
    private String artistName;

    @NotNull(message = "Длительность не может быть пустой")
    @Positive(message = "Длительность должна быть положительным числом")
    private Integer duration;
}
