package org.example.services;

import org.example.aspects.NotNullArg;
import org.example.entities.Track;
import org.example.exceptions.ConflictException;
import org.example.repositories.ArtistRepository;
import org.example.repositories.GenreRepository;
import org.example.repositories.TrackRepository;
import org.example.requests.CreateTrackRequest;
import org.springframework.stereotype.Service;

@Service
public class TrackService {
    private final TrackRepository trackRepository;
    private final ArtistRepository artistRepository;
    private final GenreRepository genreRepository;

    public TrackService(TrackRepository trackRepository, ArtistRepository artistRepository, GenreRepository genreRepository) {
        this.trackRepository = trackRepository;
        this.artistRepository = artistRepository;
        this.genreRepository = genreRepository;
    }

    @NotNullArg
    public void addTrack(CreateTrackRequest request) {
        int artistId = request.getArtistId();
        int genreId = request.getGenreId();
        String title = request.getTitle();
        int duration = request.getDuration();

        if (!artistRepository.existsById(artistId)) {
            throw new ConflictException("Исполнителя с id = " + artistId + " нет в базе");
        }

        if (!genreRepository.existsById(genreId)) {
            throw new ConflictException("Жанра с id = " + genreId + " нет в базе");
        }

        if (trackRepository.existsByTitle(title)) {
            throw new ConflictException("Трек с названием = " + title + " уже существует");
        }

        Track track = new Track(title, genreId, artistId, duration);
        trackRepository.save(track);
    }
}
