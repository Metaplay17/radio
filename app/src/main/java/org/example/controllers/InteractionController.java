package org.example.controllers;

import org.example.aspects.CheckRole;
import org.example.requests.CreateInteractionRequest;
import org.example.services.InteractionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
public class InteractionController {
    private final InteractionService interactionService;

    public InteractionController(InteractionService interactionService) {
        this.interactionService = interactionService;
    }

    @PostMapping("/api/interactions")
    @CheckRole(roles = {"ROLE_ANALYST"})
    public ResponseEntity<String> createInteraction(@RequestBody @Valid CreateInteractionRequest request) {
        interactionService.addInteraction(request);
        return ResponseEntity.status(201).body("Взаимодействия успешно добавлены");
    }
}
