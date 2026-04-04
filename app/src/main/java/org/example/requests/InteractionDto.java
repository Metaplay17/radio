package org.example.requests;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InteractionDto {
    @Positive(message = "Идентификатор трека должен быть положительным числом")
    private long trackId;

    @NotEmpty(message = "Название типа взаимодействия не может быть пустым")
    private String name;

    @NotNull(message = "Дата и время не могут быть пустыми")
    @Positive(message = "Дата и время не могут быть отрицательными")
    private Long datetimeMsec;
    private String context;
}
