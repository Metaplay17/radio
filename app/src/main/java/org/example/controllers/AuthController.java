package org.example.controllers;

import org.example.requests.LoginRequest;
import org.example.responses.LoginResponse;
import org.example.security.JwtService;
import org.example.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api")
public class AuthController {
    
    private UserService userService;
    private JwtService jwtService;
    private AuthenticationManager authenticationManager;

    public AuthController(UserService userService, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/public/auth/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        authenticationManager.authenticate(token);

        String jwtToken = jwtService.generateToken(userService.getUserByUsername(request.getUsername()));
        return ResponseEntity.ok(new LoginResponse(jwtToken));
    }
}
