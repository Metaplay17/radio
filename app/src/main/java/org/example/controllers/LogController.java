package org.example.controllers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.example.aspects.CheckRole;
import org.example.controllers.responses.LogsResponse;
import org.example.services.LoggingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogController {
    private final LoggingService loggingService;

    public LogController(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    @GetMapping("/api/logs")
    @CheckRole(roles = {"ROLE_ADMIN"})
    public ResponseEntity<LogsResponse> getLogs(@RequestParam(name = "initiatorUsername", required = false) String initiatorUsername, 
    @RequestParam(name = "type", required = false) String type, @RequestParam(name = "msecFrom", required = false) Long msecFrom, 
    @RequestParam(name = "msecTo", required = false) Long msecTo, @RequestParam(name = "count", required = false) Integer count) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        String username = (String)securityContext.getAuthentication().getPrincipal();
        loggingService.info("Запрос на логи от " + username, username);
        LocalDateTime from = null, to = null;
        if (msecFrom != null) {
            from = LocalDateTime.ofInstant(Instant.ofEpochMilli(msecFrom), ZoneId.systemDefault());
        }
        if (msecTo != null) {
            to = LocalDateTime.ofInstant(Instant.ofEpochMilli(msecTo), ZoneId.systemDefault());
        }
        return ResponseEntity.ok().body(new LogsResponse(loggingService.getLogs(from, to, type, initiatorUsername, count)));
    }
}
