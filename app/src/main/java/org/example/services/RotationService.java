package org.example.services;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.example.aspects.NotNullArg;
import org.example.entities.Track;
import org.example.repositories.PlaylistRepository;
import org.springframework.stereotype.Service;

@Service
public class RotationService {
    private final PlaylistRepository playlistRepository;

    private final Integer MAX_COUNT_PER_DAY = 1;
    private final Integer MAX_COUNT_PER_WEEK = 5;

    public RotationService(PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
    }

    @NotNullArg
    public List<Track> getAvailableTracks(List<Track> tracks, LocalDate date) {
        return tracks.stream().filter(track -> playlistRepository.findTrackCountPerDay(track.getId(), date) <= MAX_COUNT_PER_DAY && playlistRepository.findTrackCountPerWeek(track.getId(), date) <= MAX_COUNT_PER_WEEK).collect(Collectors.toList());
    }

    @NotNullArg
    public boolean isAvailable(Track track, LocalDate date) {
        return playlistRepository.findTrackCountPerDay(track.getId(), date) <= MAX_COUNT_PER_DAY && playlistRepository.findTrackCountPerWeek(track.getId(), date) <= MAX_COUNT_PER_WEEK;
    }
}
