package org.example.controllers.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateInteractionTypeRequest {
    @NotBlank(message = "Название не может быть null или пустой строкой")
    private String name;
}
