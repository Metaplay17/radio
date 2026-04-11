package org.example.services;

import java.util.List;

import org.example.aspects.NotNullArg;
import org.example.controllers.requests.CreateArtistRequest;
import org.example.entities.Artist;
import org.example.entities.dto.ArtistDto;
import org.example.exceptions.ConflictException;
import org.example.repositories.ArtistRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MissingServletRequestParameterException;

@Service
public class ArtistService {
    private final ArtistRepository artistRepository;

    public ArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }
    
    @NotNullArg
    public void addArtist(CreateArtistRequest request) {
        String artistName = request.getName();
        if (artistRepository.existsByName(artistName)) {
            throw new ConflictException("Исполнитель с именем " + artistName + " уже существует");
        }

        Artist artist = new Artist(artistName);
        artistRepository.save(artist);
    }

    public List<ArtistDto> getArtistByNamePattern(String namePattern) throws MissingServletRequestParameterException {
        if (namePattern == null || namePattern.isEmpty()) {
            throw new MissingServletRequestParameterException("Поисковый запрос по имени исполнителя", "String");
        }

        return artistRepository.findByNameLike(namePattern).stream().map((Artist a) -> a.toDto()).toList();
    }
}
