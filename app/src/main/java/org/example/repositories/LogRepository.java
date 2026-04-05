package org.example.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.example.entities.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

public interface LogRepository extends JpaRepository<Log, Long> {
    @NativeQuery("SELECT * " + 
    "FROM logs AS l " + 
    "WHERE (initiator_username = :initiatorUsername OR :initiatorUsername IS NULL) AND " + 
    "(datetime >= :from OR CAST(:from AS TIMESTAMP) IS NULL) AND (datetime <= :to OR CAST(:to AS TIMESTAMP) IS NULL) AND " + 
    "(type = :type OR :type IS NULL) " + 
    "LIMIT :count")
    List<Log> findByInitiatorUsernameDatetimeTypeAndCount(@Param("initiatorUsername") String initiatorUsername, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to, @Param("type") String type, @Param("count") Integer count);
}
