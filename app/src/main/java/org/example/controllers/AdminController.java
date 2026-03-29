package org.example.controllers;

import org.example.exceptions.AccessDeniedException;
import org.example.requests.CreateUserRequest;
import org.example.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public ResponseEntity<String> createUser(@RequestBody @Valid CreateUserRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication.getAuthorities());
        if (!authentication.getAuthorities().stream().anyMatch((GrantedAuthority a) -> (a.getAuthority().equals("ROLE_ADMIN") | a.getAuthority().equals("ROLE_SYSTEM")))) {
            throw new AccessDeniedException("Доступно только администраторам");
        }

        userService.createUser(request);
        return ResponseEntity.status(201).body("Пользователь создан");
    }
}
