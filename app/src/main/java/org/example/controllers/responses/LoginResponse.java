package org.example.controllers.responses;

import org.example.security.Privileges;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private Privileges privilege;
}
