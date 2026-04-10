package org.example.controllers;

import org.example.aspects.CheckRole;
import org.example.controllers.requests.CreateInteractionTypeRequest;
import org.example.controllers.responses.OkResponse;
import org.example.services.InteractionTypeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
public class InteractionTypeController {
    private final InteractionTypeService interactionTypeService;

    public InteractionTypeController(InteractionTypeService interactionTypeService) {
        this.interactionTypeService = interactionTypeService;
    }

    @PostMapping("/api/interaction-types")
    @CheckRole(roles = {"ROLE_ANALYST"})
    public ResponseEntity<OkResponse> createInteractionType(@RequestBody @Valid CreateInteractionTypeRequest request) {
        interactionTypeService.addInteractionType(request);
        return ResponseEntity.status(201).body(new OkResponse("Тип взаимодействия успешно добавлен"));
    }
}
