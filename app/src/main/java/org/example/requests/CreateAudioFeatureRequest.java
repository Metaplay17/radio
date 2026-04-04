package org.example.requests;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateAudioFeatureRequest {
    @NotNull(message = "Идентификатор трека не может быть пустым")
    @Positive(message = "Идентификатор трека должен быть положительным числом")
    private long trackId;

    @NotNull(message = "Идентификатор типа признака не может быть пустым")
    @Positive(message = "Идентификатор признака должен быть положительным числом")
    private int featureTypeId;

    @Positive(message = "Значение признака должно быть положительным числом")
    private double value;
}
