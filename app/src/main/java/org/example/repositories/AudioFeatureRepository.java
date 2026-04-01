package org.example.repositories;

import java.util.List;
import java.util.Optional;

import org.example.entities.AudioFeature;
import org.example.entities.AudioFeatureId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AudioFeatureRepository extends JpaRepository<AudioFeature, AudioFeatureId> {
    boolean existsByIdTrackIdAndIdFeatureTypeId(Long trackId, Integer featureTypeId);
    Optional<AudioFeature> findByIdTrackIdAndIdFeatureTypeId(Long trackId, Integer featureTypeId);
    List<AudioFeature> findByTrackId(Long trackId);
    List<AudioFeature> findFeaturesByTrackId(Long trackId);
}
