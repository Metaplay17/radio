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
import lombok.NoArgsConstructor;

@Entity
@Table(name = "audio_features")
@Data
@NoArgsConstructor
public class AudioFeature {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "value", nullable = false)
    private double value;

    public AudioFeatureDto toDto() {
        return new AudioFeatureDto(id, track.getId(), audioFeatureType.getId(), value);
    }

    @ManyToOne
    @JoinColumn(name = "track_id")
    private Track track;

    @ManyToOne
    @JoinColumn(name = "feature_type_id")
    private AudioFeatureType audioFeatureType;

    public AudioFeature(Track track, AudioFeatureType audioFeatureType, double value) {
        this.track = track;
        this.audioFeatureType = audioFeatureType;
        this.value = value;
    }
}
