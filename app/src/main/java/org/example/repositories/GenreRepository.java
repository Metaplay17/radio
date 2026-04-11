package org.example.repositories;

import java.util.Optional;

import org.example.entities.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Integer> {
    boolean existsByName(String name);
    Optional<Genre> findByName(String name);
}
