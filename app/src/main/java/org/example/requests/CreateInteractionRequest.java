package org.example.requests;

import java.util.List;

import org.hibernate.validator.constraints.Length;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateInteractionRequest {
    @Length(min = 1, message = "Список взапимодействий не может быть пустым")
    List<InteractionDto> interactions;
}
