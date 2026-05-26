package org.example.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.example.entities.Interaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InteractionRepository extends JpaRepository<Interaction, Long> {
    @NativeQuery("SELECT * " +
    "FROM interactions " + 
    "WHERE (track_id = :trackId OR :trackId IS NULL) " +
    "AND (interaction_type_id = :interactionTypeId OR :interactionTypeId IS NULL) " + 
    "AND (datetime >= :from OR CAST(:from AS TIMESTAMP) IS NULL) AND (datetime <= :to OR CAST(:to AS TIMESTAMP) IS NULL) " +
    "ORDER BY datetime ASC " + 
    "LIMIT :count")
    List<Interaction> findAnalyticInteraction(@Param("interactionTypeId") Integer interactionTypeId, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to, @Param("count") Integer count, @Param("trackId") Long trackId);

    @NativeQuery("SELECT t.title, a.name, t.duration, COUNT(*) AS listening " +
                "FROM interactions AS i " +
                "JOIN tracks AS t ON t.id = i.track_id " +
                "JOIN artists AS a ON t.artist_id = a.id " +
                "WHERE i.interaction_type_id = :interactionTypeId AND i.datetime >= :from AND i.datetime <= :to " +
                "GROUP BY t.title, a.name, t.duration") 
    List<InteractionTypeStats> findTracksGroupedInteractions(@Param("interactionTypeId") Integer interactionTypeId, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

}
