package org.example.services;

import org.example.aspects.NotNullArg;
import org.example.controllers.requests.CreateAudioFeatureTypeRequest;
import org.example.entities.AudioFeatureType;
import org.example.exceptions.ConflictException;
import org.example.repositories.AudioFeatureTypeRepository;
import org.springframework.stereotype.Service;

@Service
public class AudioFeatureTypeService {
    private final AudioFeatureTypeRepository audioFeatureTypeRepository;

    public AudioFeatureTypeService(AudioFeatureTypeRepository audioFeatureTypeRepository) {
        this.audioFeatureTypeRepository = audioFeatureTypeRepository;
    }

    @NotNullArg
    public void addAudioFeatureType(CreateAudioFeatureTypeRequest request) {
        String featureName = request.getName();
        if (audioFeatureTypeRepository.existsByName(featureName)) {
            throw new ConflictException("Признак с именем " + featureName + " уже существует");
        }

        AudioFeatureType audioFeatureType = new AudioFeatureType(featureName);
        audioFeatureTypeRepository.save(audioFeatureType);
    }
}
