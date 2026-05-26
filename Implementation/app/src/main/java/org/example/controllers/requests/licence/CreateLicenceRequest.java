package org.example.controllers.requests.licence;

import java.time.LocalDate;

import org.example.controllers.requests.track.TrackSignature;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateLicenceRequest {
    @NotNull(message = "Сигнатура трека не может быть пустой")
    private TrackSignature track;

    @Positive(message = "Длительность лицензии должна быть положительным числом дней")
    private int duration;

    @NotNull(message = "Дата регистрации лицензии не может быть пустой")
    private LocalDate registered;
}
