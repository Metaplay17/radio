package org.example.services;

import java.util.List;

import org.example.aspects.NotNullArg;
import org.example.controllers.requests.CreateAudioFeatureRequest;
import org.example.entities.AudioFeature;
import org.example.entities.AudioFeatureType;
import org.example.entities.Track;
import org.example.entities.dto.AudioFeatureDto;
import org.example.exceptions.ConflictException;
import org.example.exceptions.NotFoundException;
import org.example.repositories.AudioFeatureRepository;
import org.example.repositories.AudioFeatureTypeRepository;
import org.example.repositories.TrackRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MissingServletRequestParameterException;

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
    public void addAudioFeature(CreateAudioFeatureRequest request) throws MissingServletRequestParameterException {
        String trackTitle = request.getTrackTitle();
        String artistName = request.getArtistName();
        int featureTypeId = request.getFeatureTypeId();
        double value = request.getValue();

        if (trackTitle == null || artistName == null || trackTitle.isEmpty() || artistName.isEmpty()) {
            throw new MissingServletRequestParameterException("Не указаны обязательные параметры: название трека и исполнитель", "String");
        }
        Track track = trackRepository.findByTitleAndArtistName(trackTitle, artistName).orElseThrow(() -> new NotFoundException("Трека с названием " + trackTitle + " и исполнителем " + artistName + " нет в базе"));

        if (audioFeatureRepository.existsByIdTrackIdAndIdFeatureTypeId(track.getId(), featureTypeId)) {
            throw new ConflictException("Признак такого типа уже задан для этого трека");
        }

        AudioFeatureType audioFeatureType = audioFeatureTypeRepository.findById(featureTypeId).orElseThrow(() -> new NotFoundException("Признака с id = " + featureTypeId + " нет в базе"));

        AudioFeature audioFeature = new AudioFeature(track, audioFeatureType, value);
        audioFeatureRepository.save(audioFeature);
    }

    public List<AudioFeatureDto> getAudioFeatures(String trackTitle, String artistName, Long lastId) throws MissingServletRequestParameterException {
        if (trackTitle == null || artistName == null || trackTitle.isEmpty() || artistName.isEmpty()) {
            throw new MissingServletRequestParameterException("Не указаны обязательные параметры: название трека и исполнитель", "String");
        }

        Track track = trackRepository.findByTitleAndArtistName(trackTitle, artistName).orElseThrow(() -> new NotFoundException("Трека с названием " + trackTitle + " и исполнителем " + artistName + " нет в базе"));
        return audioFeatureRepository.findByTrackIdAndLastId(track.getId(), lastId).stream().map((AudioFeature f) -> f.toDto()).toList();
    }
}
