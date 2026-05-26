package org.example.repositories;

import java.util.Optional;

import org.example.entities.AudioFeatureType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AudioFeatureTypeRepository extends JpaRepository<AudioFeatureType, Integer> {
    boolean existsByName(String name);
    Optional<AudioFeatureType> findByName(String name);
}
