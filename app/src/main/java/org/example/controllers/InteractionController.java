package org.example.controllers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.example.aspects.CheckRole;
import org.example.controllers.requests.CreateInteractionRequest;
import org.example.controllers.responses.InteractionAnalyticResponse;
import org.example.controllers.responses.OkResponse;
import org.example.controllers.responses.RaoReportResponse;
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
    public ResponseEntity<OkResponse> createInteraction(@RequestBody @Valid CreateInteractionRequest request) {
        interactionService.addInteraction(request);
        return ResponseEntity.status(201).body(new OkResponse("Взаимодействия успешно добавлены"));
    }

    @GetMapping("/api/interactions")
    @CheckRole(roles = {"ROLE_ANALYST"})
    public ResponseEntity<InteractionAnalyticResponse> getInteractionsAnalytics(@RequestParam(required = false) String interactionType, @RequestParam(required = true) Long msecFrom, 
    @RequestParam(name = "msecTo", required = true) Long msecTo, @RequestParam(name = "count", required = true) Integer count, @RequestParam(name = "trackTitle", required = false) String trackTitle, @RequestParam(name = "artistName", required = false) String artistName) {
        LocalDateTime from = LocalDateTime.ofInstant(Instant.ofEpochMilli(msecFrom), ZoneId.systemDefault());
        LocalDateTime to = LocalDateTime.ofInstant(Instant.ofEpochMilli(msecTo), ZoneId.systemDefault());
        return ResponseEntity.ok().body(interactionService.getInteractionsAnalytics(interactionType, from, to, count, trackTitle, artistName));
    }

    @GetMapping("/api/interactions/rao-report")
    @CheckRole(roles = {"ROLE_ANALYST"})
    public ResponseEntity<RaoReportResponse> getRaoReport(@RequestParam Long msecFrom, @RequestParam Long msecTo) {
        LocalDateTime from = LocalDateTime.ofInstant(Instant.ofEpochMilli(msecFrom), ZoneId.systemDefault());
        LocalDateTime to = LocalDateTime.ofInstant(Instant.ofEpochMilli(msecTo), ZoneId.systemDefault());
        return ResponseEntity.ok().body(new RaoReportResponse(interactionService.formRaoReport(from, to)));
    }
}
