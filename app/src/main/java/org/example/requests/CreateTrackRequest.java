package org.example.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateTrackRequest {
    @NotNull(message = "Название не может быть null или пустой строкой")
    @NotBlank(message = "Название не может быть null или пустой строкой")
    @NotEmpty(message = "Название не может быть null или пустой строкой")
    private String title;

    @Positive(message = "Идентификатор жанра должен быть положительным числом")
    private int genreId;

    @Positive(message = "Идентификатор исполнителя должен быть положительным числом")
    private int artistId;

    @Positive(message = "Длительность должна быть положительным числом")
    private int duration;
}
