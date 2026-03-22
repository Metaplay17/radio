package org.example.entities;

import org.example.entities.dto.AudioFeatureDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "audio_features")
@Data
public class AudioFeature {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "track_id", nullable = false)
    private long trackId;

    @Column(name = "feature_type_id", nullable = false)
    private int featureTypeId;

    @Column(name = "value", nullable = false)
    private double value;

    public AudioFeatureDto toDto() {
        return new AudioFeatureDto(id, trackId, featureTypeId, value);
    }

    @ManyToOne
    @JoinColumn(name = "track_id")
    private Track track;

    @ManyToOne
    @JoinColumn(name = "feature_type_id")
    private AudioFeatureType featureType;

    public AudioFeature(long trackId, int featureTypeId, double value) {
        this.trackId = trackId;
        this.featureTypeId = featureTypeId;
        this.value = value;
    }
}
