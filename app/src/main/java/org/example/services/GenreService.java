package org.example.services;

import org.example.aspects.NotNullArg;
import org.example.entities.Genre;
import org.example.exceptions.ConflictException;
import org.example.repositories.GenreRepository;
import org.example.requests.CreateGenreRequest;
import org.springframework.stereotype.Service;

@Service
public class GenreService {
    private final GenreRepository genreRepository;

    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    @NotNullArg
    public void addGenre(CreateGenreRequest request) {
        String genreName = request.getName();
        if (genreRepository.existsByName(genreName)) {
            throw new ConflictException("Жанр с названием " + genreName + " уже существует");
        }

        Genre genre = new Genre(genreName);
        genreRepository.save(genre);
    }
}
