package org.example.requests;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateAudioFeatureRequest {
    @Positive(message = "Идентификатор трека должен быть положительным числом")
    private long trackId;

    @Positive(message = "Идентификатор признака должен быть положительным числом")
    private int featureTypeId;

    @Positive(message = "Значение признака должно быть положительным числом")
    private double value;
}
