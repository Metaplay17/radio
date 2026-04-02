package org.example.requests.playlist;

import java.util.Map;
import java.util.Optional;

import org.example.requests.Daytime;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FormPlaylistRequest {
    @Positive(message = "Длительность плейлиста - положительное чичсло")
    @NotNull(message = "Длительность плейлиста не задана")
    private Integer duration;

    @NotNull(message = "Время суток не задано")
    private Daytime daytime;

    @NotNull(message = "День недели не задан")
    @Min(value = 1, message = "День недели должен быть от 1 до 7")
    @Max(value = 7, message = "День недели должен быть от 1 до 7")
    private Integer weekday;
    private Optional<Map<Integer, Double>> features;
    private Optional<Long> anchorTrackId;
}
