package org.example.repositories;

import java.util.List;

import org.example.entities.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackRepository extends JpaRepository<Track, Long> {
    boolean existsByTitle(String title);
    @NativeQuery("SELECT t.* " +
    "FROM tracks AS t " + 
    "JOIN licences AS l ON t.id = l.track_id " + 
    "WHERE l.registered + l.duration > CURRENT_DATE")
    List<Track> findAllWithActiveLicense();
}
