package org.example.controllers;

import org.example.aspects.CheckRole;
import org.example.controllers.requests.licence.CreateLicenceRequest;
import org.example.controllers.requests.licence.RemoveLicenceRequest;
import org.example.services.LicenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LicenceController {
    private final LicenceService licenceService;

    public LicenceController(LicenceService licenceService) {
        this.licenceService = licenceService;
    }

    @PostMapping("/api/licences")
    @CheckRole(roles = {"ROLE_LICENCE_MANAGER"})
    public ResponseEntity<String> createLicence(@RequestBody CreateLicenceRequest request) {
        licenceService.addLicence(request);
        return ResponseEntity.status(201).body("Лицензия успешно добавлена");
    }

    @DeleteMapping("/api/licences")
    @CheckRole(roles = {"ROLE_LICENCE_MANAGER"})
    public ResponseEntity<String> removeLicence(@RequestBody RemoveLicenceRequest request) {
        licenceService.removeLicence(request);
        return ResponseEntity.status(200).body("Лицензия отозвана с завтрашнего дня");
    }
}
