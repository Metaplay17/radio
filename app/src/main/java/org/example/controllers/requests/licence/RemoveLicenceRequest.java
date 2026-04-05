package org.example.controllers.requests.licence;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RemoveLicenceRequest {
    @NotNull(message = "LicenceId не может быть пустым")
    @Positive(message = "LicenceId должен быть положительным числом")
    private Long licenceId;
}
