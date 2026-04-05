package org.example.controllers.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginRequest {
    @NotBlank(message = "Поле username не может быть пустым")
    @Size(min = 3, max = 30, message = "Длина username от 3 до 30 символов")
    private String username;

    @NotBlank(message = "Поле password не может быть пустым")
    @Size(min = 8)
    private String password;
}
