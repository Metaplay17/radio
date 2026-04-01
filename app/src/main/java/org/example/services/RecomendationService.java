package org.example.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.example.entities.AudioFeature;
import org.example.entities.AudioFeatureType;
import org.example.entities.Track;
import org.example.exceptions.IncorrectArgumentGivenException;
import org.example.repositories.AudioFeatureRepository;
import org.example.repositories.AudioFeatureTypeRepository;
import org.example.requests.Daytime;
import org.springframework.stereotype.Service;

@Service
public class RecomendationService {

    private final AudioFeatureTypeRepository audioFeatureTypeRepository;
    private final AudioFeatureRepository audioFeatureRepository;

    private final Integer DAYTIME_CORRESPOND_WEIGHT = 100;
    private final Integer WEEKDAY_CORRESPOND_WEIGHT = 50;

    public RecomendationService(AudioFeatureTypeRepository audioFeatureTypeRepository, AudioFeatureRepository audioFeatureRepository) {
        this.audioFeatureTypeRepository = audioFeatureTypeRepository;
        this.audioFeatureRepository = audioFeatureRepository;
    }
    
    public double calcAnchorTrackScore(Track anchorTrack, Track assessedTrack) {
        int score = 50;
        List<AudioFeature> assessedAudioFeatures = assessedTrack.getAudioFeatures();
        Map<Object, Object> assessedFeatureMap = assessedAudioFeatures.stream().collect(Collectors.toMap(f -> f.getAudioFeatureType().getId(), f -> f.getValue()));

        List<AudioFeature> anchorAudioFeatures =anchorTrack.getAudioFeatures();
        Map<Object, Object> anchorFeatureMap = anchorAudioFeatures.stream().collect(Collectors.toMap(f -> f.getAudioFeatureType().getId(), f -> f.getValue()));

        for (Object featureTypeId : anchorFeatureMap.keySet()) {
            if (assessedFeatureMap.containsKey(featureTypeId)) {
                Double delta = -1 * ((((AudioFeature)assessedFeatureMap.get(featureTypeId)).getValue() - ((AudioFeature)anchorFeatureMap.get(featureTypeId)).getValue()) - 0.4);
                score += delta * 100;
            }
        }

        return score;
    }
    public double calcTrackScore(Track track, Daytime daytime, boolean isWeekend) {
        double score = 0;
        Optional<AudioFeatureType> daytimeFeature = null;
        if (daytime == Daytime.MORNING) {
            daytimeFeature = audioFeatureTypeRepository.findByName("morning-correspond");
        }
        else if (daytime == Daytime.DAY) {
            daytimeFeature = audioFeatureTypeRepository.findByName("day-correspond");
        }
        else if (daytime == Daytime.EVENING) {
            daytimeFeature = audioFeatureTypeRepository.findByName("evening-correspond");
        }
        else if (daytime == Daytime.NIGHT) {
            daytimeFeature = audioFeatureTypeRepository.findByName("night-correspond");
        }
        else {
            throw new IncorrectArgumentGivenException("calcTrackScore", "daytime", "Передано неопознанное значение daytime: " + daytime.name());
        }

        Optional<AudioFeature> trackDaytimeFeature = audioFeatureRepository.findByIdTrackIdAndIdFeatureTypeId(track.getId(), daytimeFeature.get().getId());
        if (trackDaytimeFeature.isPresent()) {
            score += trackDaytimeFeature.get().getValue() * DAYTIME_CORRESPOND_WEIGHT;
        }
        System.out.println("score: " + score + "trackId: " + track.getId());

        Optional<AudioFeatureType> fullListeningRateFeature = audioFeatureTypeRepository.findByName("full-listening-rate");
        if (fullListeningRateFeature.isPresent()) {
            Optional<AudioFeature> trackFullListeningRateFeature = audioFeatureRepository.findByIdTrackIdAndIdFeatureTypeId(track.getId(), fullListeningRateFeature.get().getId());
            if (trackFullListeningRateFeature.isPresent()) {
                score += trackFullListeningRateFeature.get().getValue() * DAYTIME_CORRESPOND_WEIGHT;
            }
        }

        if (isWeekend) {
            Optional<AudioFeatureType> weekendCorrespondFeature = audioFeatureTypeRepository.findByName("weekend-correspond");
            if (weekendCorrespondFeature.isPresent()) {
                Optional<AudioFeature> trackWeekendCorrespondFeature = audioFeatureRepository.findByIdTrackIdAndIdFeatureTypeId(track.getId(), weekendCorrespondFeature.get().getId());
                if (trackWeekendCorrespondFeature.isPresent()) {
                    score += trackWeekendCorrespondFeature.get().getValue() * WEEKDAY_CORRESPOND_WEIGHT;
                }
            }
        }
        else {
            Optional<AudioFeatureType> workdayCorrespondFeature = audioFeatureTypeRepository.findByName("workday-correspond");
            if (workdayCorrespondFeature.isPresent()) {
                Optional<AudioFeature> trackWeekendCorrespondFeature = audioFeatureRepository.findByIdTrackIdAndIdFeatureTypeId(track.getId(), workdayCorrespondFeature.get().getId());
                if (trackWeekendCorrespondFeature.isPresent()) {
                    score += trackWeekendCorrespondFeature.get().getValue() * WEEKDAY_CORRESPOND_WEIGHT;
                }
            }
        }

        return score;
    }
}
