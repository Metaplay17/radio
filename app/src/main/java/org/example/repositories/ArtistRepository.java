package org.example.repositories;

import java.util.List;
import java.util.Optional;

import org.example.entities.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Integer> {
    boolean existsByName(String name);
    @NativeQuery("SELECT id " + 
    "FROM artists " + 
    "WHERE name IN :names")
    List<Integer> findIdsByName(@Param("names") List<String> names);
    Optional<Artist> findByName(String name);

    @NativeQuery("SELECT * " + 
    "FROM artists AS a " + 
    "WHERE a.name LIKE %:namePattern%")
    List<Artist> findByNameLike(@Param("namePattern") String namePattern);
}
