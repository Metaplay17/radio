package org.example.repositories;

import java.time.LocalDate;

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
}
