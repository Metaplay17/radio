package org.example.repositories;

import org.example.entities.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackRepository extends JpaRepository<Track, Long> {
    boolean existsByTitle(String title);
}
