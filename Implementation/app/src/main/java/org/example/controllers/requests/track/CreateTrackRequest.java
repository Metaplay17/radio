package org.example.controllers.requests.track;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateTrackRequest {
    @NotBlank(message = "Название не может быть null или пустой строкой")
    private String title;

    @NotBlank(message = "Жанр должен быть задан")
    private String genre;

    @NotBlank(message = "Исполнитель должен быть задан")
    private String artist;

    @Positive(message = "Длительность должна быть положительным числом")
    private Integer duration;
}
