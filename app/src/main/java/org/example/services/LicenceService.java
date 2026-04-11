package org.example.services;

import java.time.LocalDate;
import java.util.List;

import org.example.aspects.NotNullArg;
import org.example.controllers.requests.licence.CreateLicenceRequest;
import org.example.controllers.requests.licence.RemoveLicenceRequest;
import org.example.controllers.requests.track.TrackSignature;
import org.example.entities.Licence;
import org.example.entities.Track;
import org.example.entities.dto.LicenceDto;
import org.example.exceptions.NotFoundException;
import org.example.repositories.LicenceRepository;
import org.example.repositories.TrackRepository;
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
        TrackSignature trackSignature = request.getTrack();
        int duration = request.getDuration();
        LocalDate registered = request.getRegistered();

        Track track = trackRepository.findByTitleAndArtistName(trackSignature.getTitle(), trackSignature.getArtistName()).orElseThrow(() -> new NotFoundException("Трека с названием = " + trackSignature.getTitle() + " нет в базе"));

        Licence licence = new Licence(track, registered, duration);
        licenceRepository.save(licence);
    }

    @NotNullArg
    public void removeLicence(RemoveLicenceRequest request) {
        Licence licence = licenceRepository.findById(request.getLicenceId()).orElseThrow(() -> new NotFoundException("Лицензии с id = " + request.getLicenceId() + " нет в базе"));
        LocalDate registered = licence.getRegistered();
        LocalDate now = LocalDate.now();
        Long daysGone = registered.datesUntil(now).count();
        licence.setDuration(daysGone.intValue());
    }

    public List<LicenceDto> getLicences(String trackTitle, String artistName, String type, Long lastId) {
        Long trackId = null;
        if (trackTitle != null && artistName != null && !trackTitle.isEmpty() && !artistName.isEmpty()) {
            trackId = trackRepository.findByTitleAndArtistName(trackTitle, artistName).orElseThrow(() -> new NotFoundException("Трека с названием = " + trackTitle + " нет в базе")).getId();
        }
        if (type.equals("ALL")) {
            return licenceRepository.findAllByTrackId(trackId, lastId).stream().map((Licence l) -> l.toDto()).toList();
        }
        else if (type.equals("ACTIVE")) {
            return licenceRepository.findActiveByTrackId(trackId, lastId).stream().map((Licence l) -> l.toDto()).toList();
        }
        else if (type.equals("WARNING")) {
            return licenceRepository.findWarningByTrackId(trackId, lastId).stream().map((Licence l) -> l.toDto()).toList();
        }
        else if (type.equals("EXPIRED")) {
            return licenceRepository.findExpiredByTrackId(trackId, lastId).stream().map((Licence l) -> l.toDto()).toList();
        }
        throw new NotFoundException("Неизвестный тип лицензии");
    }
}
