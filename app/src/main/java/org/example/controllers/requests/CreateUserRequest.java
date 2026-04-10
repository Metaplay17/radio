package org.example.controllers.requests;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateUserRequest {
    @NotBlank(message = "Поле username не может быть пустым")
    @Size(min = 3, max = 30, message = "Длина username от 3 до 30 символов")
    private String username;

    @Email(message = "Поле email не соответствует адресу эхлектронной почты")
    @NotBlank(message = "Поле email не может быть пустым")
    private String email;

    @NotNull(message = "Поле privilegeLevel не может быть пустым")
    @Min(value = 1, message = "Поле privilegeLevel не может быть меньше 1")
    @Max(value = 100, message = "Поле privilegeLevel не может быть больше 100")
    private Integer privilegeLevel;

    @NotBlank(message = "Поле password не может быть пустым")
    @Size(min = 8, message = "Длина password не может быть меньше 8 символов")
    private String password;
}
