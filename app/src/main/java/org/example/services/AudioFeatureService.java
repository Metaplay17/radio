package org.example.services;

import org.example.aspects.NotNullArg;
import org.example.entities.AudioFeature;
import org.example.entities.AudioFeatureType;
import org.example.entities.Track;
import org.example.exceptions.ConflictException;
import org.example.repositories.AudioFeatureRepository;
import org.example.repositories.AudioFeatureTypeRepository;
import org.example.repositories.TrackRepository;
import org.example.requests.CreateAudioFeatureRequest;
import org.springframework.stereotype.Service;

@Service
public class AudioFeatureService {
    private final AudioFeatureRepository audioFeatureRepository;
    private final TrackRepository trackRepository;
    private final AudioFeatureTypeRepository audioFeatureTypeRepository;

    public AudioFeatureService(AudioFeatureRepository audioFeatureRepository, TrackRepository trackRepository, AudioFeatureTypeRepository audioFeatureTypeRepository) {
        this.audioFeatureRepository = audioFeatureRepository;
        this.trackRepository = trackRepository;
        this.audioFeatureTypeRepository = audioFeatureTypeRepository;
    }

    @NotNullArg
    public void addAudioFeature(CreateAudioFeatureRequest request) {
        long trackId = request.getTrackId();
        int featureTypeId = request.getFeatureTypeId();
        double value = request.getValue();

        if (audioFeatureRepository.existsByIdTrackIdAndIdFeatureTypeId(trackId, featureTypeId)) {
            throw new ConflictException("Признак такого типа уже задан для этого трека");
        }

        Track track = trackRepository.findById(trackId).orElseThrow(() -> new ConflictException("Трека с id = " + trackId + " нет в базе"));
        AudioFeatureType audioFeatureType = audioFeatureTypeRepository.findById(featureTypeId).orElseThrow(() -> new ConflictException("Признака с id = " + trackId + " нет в базе"));

        AudioFeature audioFeature = new AudioFeature(track, audioFeatureType, value);
        audioFeatureRepository.save(audioFeature);
    }
}
