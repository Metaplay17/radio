package org.example.repositories;

import java.time.LocalDate;
import java.util.List;

import org.example.entities.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Integer> {
    @NativeQuery("SELECT COUNT(*) " + 
    "FROM playlists AS p " + 
    "JOIN playlists_tracks AS pt ON p.id = pt.playlist_id AND pt.track_id = :trackId " + 
    "WHERE p.datetime >= CURRENT_DATE - INTERVAL '1 day'")
    Integer findTrackCountPerDay(@Param("trackId") Long trackId);

    @NativeQuery("SELECT COUNT(*) " + 
    "FROM playlists AS p " + 
    "JOIN playlists_tracks AS pt ON p.id = pt.playlist_id AND pt.track_id = :trackId " + 
    "WHERE p.datetime >= CURRENT_DATE - INTERVAL '1 week'")
    Integer findTrackCountPerWeek(@Param("trackId") Long trackId);

    @NativeQuery("SELECT p.* " + 
    "FROM playlists AS p " + 
    "WHERE (datetime::DATE = :date OR CAST(:date AS DATE) IS NULL) AND p.id > :lastId " + 
    "ORDER BY p.id " + 
    "LIMIT 10")
    List<Playlist> findAllByDate(@Param("date") LocalDate date, @Param("lastId") Long lastId);
}
