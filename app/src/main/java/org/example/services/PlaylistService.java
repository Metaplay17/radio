package org.example.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.PriorityQueue;

import org.example.aspects.NotNullArg;
import org.example.controllers.requests.playlist.ConfirmPlaylistRequest;
import org.example.controllers.requests.playlist.FormPlaylistRequest;
import org.example.controllers.requests.track.TrackSignature;
import org.example.controllers.responses.PlaylistInfoResponse;
import org.example.controllers.responses.TrackScoreDto;
import org.example.entities.Playlist;
import org.example.entities.Track;
import org.example.entities.User;
import org.example.entities.dto.PlaylistDto;
import org.example.exceptions.LicenceExpiredException;
import org.example.exceptions.NotFoundException;
import org.example.exceptions.RotationOverusedException;
import org.example.exceptions.UserNotFoundException;
import org.example.repositories.LicenceRepository;
import org.example.repositories.PlaylistRepository;
import org.example.repositories.TrackRepository;
import org.example.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class PlaylistService {
    private final PlaylistRepository playlistRepository;
    private final TrackRepository trackRepository;
    private final RecomendationService recomendationService;
    private final RotationService rotationService;
    private final UserRepository userRepository;
    private final LicenceRepository licenceRepository;

    public PlaylistService(PlaylistRepository playlistRepository, TrackRepository trackRepository, RecomendationService recomendationService, UserRepository userRepository, RotationService rotationService, LicenceRepository licenceRepository) {
        this.playlistRepository = playlistRepository;
        this.trackRepository = trackRepository;
        this.recomendationService = recomendationService;
        this.userRepository = userRepository;
        this.rotationService = rotationService;
        this.licenceRepository = licenceRepository;
    }

    @NotNullArg
    public List<TrackScoreDto> formPlaylist(FormPlaylistRequest request) {
        if (request.getAnchorTrack().isPresent()) {
            Optional<TrackSignature> anchorTrackSignature = new TrackSignature(request.getAnchorTrack().get().split(" - ")[0], request.getAnchorTrack().get().split(" - ")[1], 60);
        }
        else {
            Optional<TrackSignature> anchorTrackSignature = Optional.empty();
        }
        
        LocalDate date = request.getDate();
        List<Track> tracks = trackRepository.findAllWithActiveLicense(date);
        tracks = rotationService.getAvailableTracks(tracks, date);

        Map<Double, Track> trackScores = new HashMap<Double, Track>();
        PriorityQueue<Double> queue = new PriorityQueue<Double>();
        List<TrackScoreDto> result = new ArrayList<TrackScoreDto>();

        if (anchorTrackSignature.isPresent()) {
            Track anchorTrack = trackRepository.findByTitleAndArtistName(anchorTrackSignature.get().getTitle(), anchorTrackSignature.get().getArtistName()).orElseThrow(
                () -> new NotFoundException("Трека с названием = " + anchorTrackSignature.get().getTitle() + " и исполнителем " + anchorTrackSignature.get().getArtistName() + " нет в базе"));

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

    @NotNullArg
    public void confirmPlaylist(ConfirmPlaylistRequest request, String creatorUsername) {
        User creator = userRepository.findByUsername(creatorUsername).orElseThrow(() -> new UserNotFoundException("Пользователя с username = " + creatorUsername + " нет в базе"));
        Playlist playlist = new Playlist(request.getName(), creator, request.getDescription(), request.getDatetime(), 0);
        Integer duration = 0;
        LocalDate date = request.getDatetime().toLocalDate();
        for (TrackSignature trackSignature : request.getTracks()) {
            Track track = trackRepository.findByTitleAndArtistName(trackSignature.getTitle(), trackSignature.getArtistName()).orElseThrow(() -> new NotFoundException("Трека с названием = " + trackSignature.getTitle() + " и исполнителем = " + trackSignature.getArtistName() + " нет в базе"));
            if (!isLicenceTrackAvailable(track, date)) {
                throw new LicenceExpiredException("У трека " + track.getTitle() + " закончилась лицензия");
            }
            if (!rotationService.isAvailable(track, date)) {
                throw new RotationOverusedException("У трека " + track.getTitle() + " закончилась ротация");
            }
            duration += track.getDuration();
            playlist.addTrack(track);
        }
        playlist.setDuration(duration);
        playlistRepository.save(playlist);
    }

    private boolean isLicenceTrackAvailable(Track track, LocalDate date) {
        return licenceRepository.checkLicenceByTrackId(track.getId(), date);
    }

    public List<PlaylistDto> getPlaylists(LocalDate date) {
        return playlistRepository.findAllByDate(date).stream().map((Playlist p) -> p.toDto()).toList();
    }

    public PlaylistInfoResponse getPlaylistInfo(Integer id) {
        Playlist playlist = playlistRepository.findById(id).orElseThrow(() -> new NotFoundException("Плейлиста с id = " + id + " нет в базе"));
        return new PlaylistInfoResponse(playlist.getId(), playlist.getName(), playlist.getDescription(), playlist.getDuration(), playlist.getDatetime(), playlist.getTracks().stream().map((Track t) -> t.toDto()).toList());
    }
}
