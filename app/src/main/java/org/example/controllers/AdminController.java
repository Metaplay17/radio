package org.example.controllers;

import org.example.exceptions.AccessDeniedException;
import org.example.requests.CreateUserRequest;
import org.example.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/admin")
public class AdminController {
    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/users")
    public ResponseEntity<String> createUser(@AuthenticationPrincipal UserDetails userDetails, @RequestBody CreateUserRequest request) {
        if (!userDetails.getAuthorities().stream().anyMatch((GrantedAuthority a) -> a.getAuthority().equals("ADMIN"))) {
            throw new AccessDeniedException("Доступно только администраторам");
        }

        userService.createUser(request);
        return ResponseEntity.status(201).body("Пользователь создан");
    }
}
