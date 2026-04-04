package org.example.controllers;

import org.example.aspects.CheckRole;
import org.example.requests.CreateInteractionRequest;
import org.example.responses.InteractionAnalyticResponse;
import org.example.services.InteractionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/api/interactions")
    @CheckRole(roles = {"ROLE_ANALYST"})
    public ResponseEntity<InteractionAnalyticResponse> getInteractionsAnalytics(@RequestParam String interactionType, @RequestParam Long msecFrom, @RequestParam Long msecTo, @RequestParam Integer count, @RequestParam Long trackId) {
        return ResponseEntity.ok().body(new InteractionAnalyticResponse(interactionService.getInteractionsAnalytics(interactionType, msecFrom, msecTo, count, trackId)));
    }
}
