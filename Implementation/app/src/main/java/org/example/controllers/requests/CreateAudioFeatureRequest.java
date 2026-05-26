package org.example.controllers.requests;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateAudioFeatureRequest {
    @NotEmpty(message = "Название трека не может быть пустым")
    private String trackTitle;

    @NotEmpty(message = "Название исполнителя не может быть пустым")
    private String artistName;

    @NotNull(message = "Идентификатор типа признака не может быть пустым")
    @Positive(message = "Идентификатор признака должен быть положительным числом")
    private int featureTypeId;

    @Positive(message = "Значение признака должно быть положительным числом")
    private double value;
}
