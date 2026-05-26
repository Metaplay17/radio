package org.example.repositories;

import java.time.LocalDate;
import java.util.List;

import org.example.entities.Licence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LicenceRepository extends JpaRepository<Licence, Long> {
    @NativeQuery("SELECT COALESCE(MAX(l.registered + INTERVAL '1 day' * l.duration) > CAST(:date AS TIMESTAMP) + INTERVAL '1 day', false) " + 
    "FROM licences AS l " + 
    "WHERE track_id = :trackId")
    boolean checkLicenceByTrackId(@Param("trackId") Long trackId, @Param("date") LocalDate date);

    @NativeQuery("SELECT l.* " + 
    "FROM licences AS l " + 
    "WHERE (l.track_id = :trackId OR :trackId IS NULL) AND l.registered + l.duration * INTERVAL '1 day' > CURRENT_DATE AND l.id > :lastId " +
    "ORDER BY l.id " + 
    "LIMIT 10")
    List<Licence> findActiveByTrackId(@Param("trackId") Long trackId, @Param("lastId") Long lastId);

    @NativeQuery("SELECT l.* " + 
    "FROM licences AS l " + 
    "WHERE (l.track_id = :trackId OR :trackId IS NULL) AND l.id > :lastId " +
    "ORDER BY l.id " + 
    "LIMIT 10")
    List<Licence> findAllByTrackId(@Param("trackId") Long trackId, @Param("lastId") Long lastId);

    @NativeQuery("SELECT l.* " + 
    "FROM licences AS l " + 
    "WHERE (l.track_id = :trackId OR :trackId IS NULL) AND l.registered + l.duration * INTERVAL '1 day' <= CURRENT_DATE + INTERVAL '1 day' * 30 " +
    "AND l.registered + l.duration * INTERVAL '1 day' > CURRENT_DATE AND l.id > :lastId " +
    "ORDER BY l.id " + 
    "LIMIT 10")
    List<Licence> findWarningByTrackId(@Param("trackId") Long trackId, @Param("lastId") Long lastId);

    @NativeQuery("SELECT l.* " + 
    "FROM licences AS l " + 
    "WHERE (l.track_id = :trackId OR :trackId IS NULL) AND l.registered + l.duration * INTERVAL '1 day' <= CURRENT_DATE AND l.id > :lastId " +
    "ORDER BY l.id " + 
    "LIMIT 10")
    List<Licence> findExpiredByTrackId(@Param("trackId") Long trackId, @Param("lastId") Long lastId);
}
