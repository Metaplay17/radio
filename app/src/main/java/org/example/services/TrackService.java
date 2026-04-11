package org.example.services;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.example.aspects.NotNullArg;
import org.example.controllers.requests.track.CreateTrackRequest;
import org.example.entities.Artist;
import org.example.entities.Genre;
import org.example.entities.Licence;
import org.example.entities.Track;
import org.example.entities.dto.TrackDto;
import org.example.exceptions.ConflictException;
import org.example.exceptions.NotFoundException;
import org.example.repositories.ArtistRepository;
import org.example.repositories.GenreRepository;
import org.example.repositories.TrackRepository;
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
        String artistName = request.getArtist();
        String genreName = request.getGenre();
        String title = request.getTitle();
        int duration = request.getDuration();

        Artist artist = artistRepository.findByName(artistName).orElseThrow(() -> new ConflictException("Исполнителя с названием = " + artistName + " нет в базе"));
        Genre genre = genreRepository.findByName(genreName).orElseThrow(() -> new ConflictException("Жанра с названием = " + genreName + " нет в базе"));

        if (trackRepository.existsByTitle(title)) {
            throw new ConflictException("Трек с названием = " + title + " уже существует");
        }

        Track track = new Track(title, genre, artist, duration);
        trackRepository.save(track);
    }

    public List<TrackDto> getTracks(String titlePattern, String artistName, String genre, Long lastId, Boolean isLicensedOnly) {
        titlePattern = titlePattern == null ? "%%" : "%" + titlePattern + "%";
        Integer artistId = null;
        Integer genreId = null;
        if (artistName != null && !artistName.isEmpty()) {
            artistId = artistRepository.findByName(artistName).orElseThrow(() -> new NotFoundException("Исполнителя с названием " + artistName + " нет в базе")).getId();
        }
        if (genre != null && !genre.isEmpty()) {
            genreId = genreRepository.findByName(genre).orElseThrow(() -> new NotFoundException("Жанра с названием " + genre + " нет в базе")).getId();
        }
        if (isLicensedOnly == true) {
            return trackRepository.findByTitlePatternAndArtistAndGenre(titlePattern, artistId, genreId, lastId).stream().filter((Track t) -> t.getLicences().stream().anyMatch((Licence l) -> l.getRegistered().plusDays(l.getDuration()).isAfter(LocalDate.now()))).map((Track t) -> t.toDto()).collect(Collectors.toList());
        }
        else {
            return trackRepository.findByTitlePatternAndArtistAndGenre(titlePattern, artistId, genreId, lastId).stream().map((Track t) -> t.toDto()).collect(Collectors.toList());
        }
    }
}
