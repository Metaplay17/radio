package org.example.services;

import java.util.List;

import org.example.aspects.NotNullArg;
import org.example.controllers.requests.CreateGenreRequest;
import org.example.entities.Genre;
import org.example.entities.dto.GenreDto;
import org.example.exceptions.ConflictException;
import org.example.repositories.GenreRepository;
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

    public List<GenreDto> getAllGenres() {
        return genreRepository.findAll().stream().map((Genre g) -> g.toDto()).toList();
    }
}
