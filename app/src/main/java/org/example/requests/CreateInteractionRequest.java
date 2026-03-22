package org.example.requests;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateInteractionRequest {
    @Positive(message = "Идентификатор трека должен быть положительным числом")
    private long trackId;

    @Positive(message = "Идентификатор типа взаимодействия должен быть положительным числом")
    private int interactionTypeId;
    private String context;
}
