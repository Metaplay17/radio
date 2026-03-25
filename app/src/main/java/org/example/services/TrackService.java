package org.example.services;

import org.example.aspects.NotNullArg;
import org.example.entities.Artist;
import org.example.entities.Genre;
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

        Artist artist = artistRepository.findById(artistId).orElseThrow(() -> new ConflictException("Исполнителя с id = " + artistId + " нет в базе"));
        Genre genre = genreRepository.findById(genreId).orElseThrow(() -> new ConflictException("Жанра с id = " + genreId + " нет в базе"));

        if (trackRepository.existsByTitle(title)) {
            throw new ConflictException("Трек с названием = " + title + " уже существует");
        }

        Track track = new Track(title, genre, artist, duration);
        trackRepository.save(track);
    }
}
