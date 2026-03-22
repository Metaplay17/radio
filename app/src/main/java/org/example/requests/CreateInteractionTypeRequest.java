package org.example.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateInteractionTypeRequest {
    @NotNull(message = "Название не может быть null или пустой строкой")
    @NotBlank(message = "Название не может быть null или пустой строкой")
    @NotEmpty(message = "Название не может быть null или пустой строкой")
    private String name;
}
