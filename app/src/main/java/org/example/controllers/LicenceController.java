package org.example.controllers;

import java.util.List;

import org.example.aspects.CheckRole;
import org.example.controllers.requests.licence.CreateLicenceRequest;
import org.example.controllers.requests.licence.RemoveLicenceRequest;
import org.example.controllers.responses.OkResponse;
import org.example.entities.dto.LicenceDto;
import org.example.services.LicenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LicenceController {
    private final LicenceService licenceService;

    public LicenceController(LicenceService licenceService) {
        this.licenceService = licenceService;
    }

    @PostMapping("/api/licences")
    @CheckRole(roles = {"ROLE_LICENCE_MANAGER"})
    public ResponseEntity<OkResponse> createLicence(@RequestBody CreateLicenceRequest request) {
        licenceService.addLicence(request);
        return ResponseEntity.status(201).body(new OkResponse("Лицензия успешно добавлена"));
    }

    @DeleteMapping("/api/licences")
    @CheckRole(roles = {"ROLE_LICENCE_MANAGER"})
    public ResponseEntity<OkResponse> removeLicence(@RequestBody RemoveLicenceRequest request) {
        licenceService.removeLicence(request);
        return ResponseEntity.status(200).body(new OkResponse("Лицензия отозвана с завтрашнего дня"));
    }

    @GetMapping("/api/licences")
    @CheckRole(roles = {"ROLE_LICENCE_MANAGER"})
    public ResponseEntity<List<LicenceDto>> getLicences(@RequestParam(name = "type", required = true) String type, 
        @RequestParam(name = "trackTitle", required = false) String trackTitle, @RequestParam(name = "artistName", required = false) String artistName,
         @RequestParam(name = "lastId", required = true) Long lastId) {
            return ResponseEntity.ok().body(licenceService.getLicences(trackTitle, artistName, type, lastId));
    }
}
