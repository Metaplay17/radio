package org.example.controllers;

import org.example.controllers.requests.LoginRequest;
import org.example.controllers.responses.LoginResponse;
import org.example.entities.User;
import org.example.security.JwtService;
import org.example.services.LoggingService;
import org.example.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
public class AuthController {
    
    private UserService userService;
    private JwtService jwtService;
    private AuthenticationManager authenticationManager;
    private final LoggingService loggingService;

    public AuthController(UserService userService, JwtService jwtService, AuthenticationManager authenticationManager, LoggingService loggingService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.loggingService = loggingService;
    }

    @PostMapping("/api/public/auth/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        authenticationManager.authenticate(token);

        User user = userService.getUserByUsername(request.getUsername());
        String jwtToken = jwtService.generateToken(user);
        loggingService.info("Пользователь " + request.getUsername() + " авторизовался", request.getUsername());
        return ResponseEntity.ok(new LoginResponse(jwtToken, user.getStringPrivilegeLevel()));
    }
}
