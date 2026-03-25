package org.example.services;

import java.time.LocalDate;

import org.example.aspects.NotNullArg;
import org.example.entities.Licence;
import org.example.entities.Track;
import org.example.exceptions.ConflictException;
import org.example.repositories.LicenceRepository;
import org.example.repositories.TrackRepository;
import org.example.requests.CreateLicenceRequest;
import org.springframework.stereotype.Service;

@Service
public class LicenceService {
    private final LicenceRepository licenceRepository;
    private final TrackRepository trackRepository;

    public LicenceService(LicenceRepository licenceRepository, TrackRepository trackRepository) {
        this.licenceRepository = licenceRepository;
        this.trackRepository = trackRepository;
    }

    @NotNullArg
    public void addLicence(CreateLicenceRequest request) {
        long trackId = request.getTrackId();
        int duration = request.getDuration();
        LocalDate registered = request.getRegistered();

        Track track = trackRepository.findById(trackId).orElseThrow(() -> new ConflictException("Трека с id = " + trackId + " нет в базе"));

        Licence licence = new Licence(track, registered, duration);
        licenceRepository.save(licence);
    }
}
