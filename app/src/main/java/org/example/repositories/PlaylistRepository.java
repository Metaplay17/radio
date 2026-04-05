package org.example.repositories;

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
}
