package org.example.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateTrackRequest {
    @NotBlank(message = "Название не может быть null или пустой строкой")
    private String title;

    @Positive(message = "Идентификатор жанра должен быть положительным числом")
    private int genreId;

    @Positive(message = "Идентификатор исполнителя должен быть положительным числом")
    private int artistId;

    @Positive(message = "Длительность должна быть положительным числом")
    private int duration;
}
