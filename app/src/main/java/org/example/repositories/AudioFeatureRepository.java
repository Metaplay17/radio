package org.example.repositories;

import org.example.entities.AudioFeature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AudioFeatureRepository extends JpaRepository<AudioFeature, Long> {
    
}
