package org.example.requests;

import java.time.LocalDate;

import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateLicenceRequest {
    @Positive(message = "Идентификатор трека должен быть положительным числом")
    private long trackId;

    @Positive(message = "Длительность лицензии должна быть положительным числом дней")
    private int duration;

    @PastOrPresent(message = "Дата регистрации лицензии не может быть в будущем")
    private LocalDate registered;
}
