package org.example.controllers;

import org.example.aspects.CheckRole;
import org.example.controllers.requests.CreateUserRequest;
import org.example.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
public class AdminController {
    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/api/admin/users")
    @CheckRole(roles = {"ROLE_ADMIN", "ROLE_SYSTEM"})
    public ResponseEntity<String> createUser(@RequestBody @Valid CreateUserRequest request) {
        userService.createUser(request);
        return ResponseEntity.status(201).body("Пользователь создан");
    }
}
