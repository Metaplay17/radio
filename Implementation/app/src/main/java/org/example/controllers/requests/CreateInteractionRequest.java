package org.example.controllers.requests;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateInteractionRequest {
    @Size(min = 1, message = "Необходимо добавить хотя бы одно взаимодействие")
    @Valid
    List<InteractionDto> interactions;
}
