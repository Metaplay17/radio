package org.example.controllers.requests.playlist;

import java.time.LocalDate;
import java.util.Optional;

import org.example.controllers.requests.Daytime;
import org.example.controllers.requests.track.TrackSignature;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FormPlaylistRequest {
    @Positive(message = "Длительность плейлиста - положительное число")
    @NotNull(message = "Длительность плейлиста не задана")
    private Integer duration;

    @NotNull(message = "Время суток не задано")
    private Daytime daytime;

    @NotNull(message = "Дата не задана")
    @FutureOrPresent(message = "Дата не может быть в прошлом")
    private LocalDate date;

    @NotNull(message = "День недели не задан")
    @Min(value = 1, message = "День недели должен быть от 1 до 7")
    @Max(value = 7, message = "День недели должен быть от 1 до 7")
    private Integer weekday;
    private Optional<String> anchorTrack;
}
