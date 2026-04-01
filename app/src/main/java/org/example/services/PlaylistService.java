package org.example.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;

import org.example.entities.Track;
import org.example.exceptions.ConflictException;
import org.example.repositories.PlaylistRepository;
import org.example.repositories.TrackRepository;
import org.example.requests.FormPlaylistRequest;
import org.example.responses.TrackScoreDto;
import org.springframework.stereotype.Service;

@Service
public class PlaylistService {
    private final PlaylistRepository playlistRepository;
    private final TrackRepository trackRepository;
    private final RecomendationService recomendationService;

    public PlaylistService(PlaylistRepository playlistRepository, TrackRepository trackRepository, RecomendationService recomendationService) {
        this.playlistRepository = playlistRepository;
        this.trackRepository = trackRepository;
        this.recomendationService = recomendationService;
    }

    public List<TrackScoreDto> formPlaylist(FormPlaylistRequest request) {
        Optional<Long> anchorTrackId = request.getAnchorTrackId();
        List<Track> tracks = trackRepository.findAllWithActiveLicense();

        Map<Double, Track> trackScores = new HashMap<Double, Track>();
        PriorityQueue<Double> queue = new PriorityQueue<Double>();
        List<TrackScoreDto> result = new ArrayList<TrackScoreDto>();

        if (anchorTrackId.isPresent()) {
            Track anchorTrack = trackRepository.findById(anchorTrackId.get()).orElseThrow(() -> new ConflictException("Трека с ID = " + anchorTrackId + " нет в базе"));

            for (Track track : tracks) {
                double score = recomendationService.calcAnchorTrackScore(anchorTrack, track);
                while (trackScores.containsKey(score)) {
                    score -= 0.01;
                }
                trackScores.put(score, track);
                queue.add(score);
            }
        }
        else {
            for (Track track : tracks) {
                double score = recomendationService.calcTrackScore(track, request.getDaytime(), request.getWeekday() >= 6);
                while (trackScores.containsKey(score)) {
                    score -= 0.01;
                }
                trackScores.put(score, track);
                queue.add(score);
            }
        }

        int duration = 0;
        for (Double sc : queue) {
            if (duration + trackScores.get(sc).getDuration() >= request.getDuration()) {
                break;
            }
            result.add(new TrackScoreDto(trackScores.get(sc).toDto(), sc));
        }
        return result.reversed();
    }
}
