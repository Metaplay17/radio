package org.example.repositories;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.example.entities.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackRepository extends JpaRepository<Track, Long> {
    boolean existsByTitle(String title);
    @NativeQuery("SELECT t.* " +
    "FROM tracks AS t " + 
    "JOIN licences AS l ON t.id = l.track_id " + 
    "WHERE l.registered + l.duration > :date")
    List<Track> findAllWithActiveLicense(@Param("date") LocalDate date);

    @NativeQuery("SELECT t.* " + 
    "FROM tracks AS t " + 
    "JOIN artists AS a ON t.artist_id = a.id " +
    "WHERE t.title = :title AND a.name = :artistName")
    Optional<Track> findByTitleAndArtistName(@Param("title") String title, @Param("artistName") String artistName);

    @NativeQuery("SELECT t.* " + 
    "FROM tracks AS t " + 
    "JOIN artists AS a ON t.artist_id = a.id " + 
    "WHERE t.title LIKE :titlePattern AND (t.genre_id = :genreId OR :genreId IS NULL) AND (a.id = :artistId OR :artistId IS NULL) AND t.id > :lastId " +
    "ORDER BY t.id " +
    "LIMIT 10")
    List<Track> findByTitlePatternAndArtistAndGenre(@Param("titlePattern") String titlePattern, @Param("artistId") Integer artistId, @Param("genreId") Integer genreId, @Param("lastId") Long lastId);
}
