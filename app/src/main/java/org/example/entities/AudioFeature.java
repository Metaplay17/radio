package org.example.entities;

import org.example.entities.dto.AudioFeatureDto;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
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
    @EmbeddedId
    private AudioFeatureId id;

    @Column(name = "value", nullable = false)
    private double value;

    public AudioFeatureDto toDto() {
        return new AudioFeatureDto(track.getId(), audioFeatureType.getId(), value);
    }

    @ManyToOne
    @JoinColumn(name = "track_id", insertable = false, updatable = false)
    private Track track;

    @ManyToOne
    @JoinColumn(name = "feature_type_id", insertable = false, updatable = false)
    private AudioFeatureType audioFeatureType;

    public AudioFeature(Track track, AudioFeatureType audioFeatureType, double value) {
        this.track = track;
        this.audioFeatureType = audioFeatureType;
        this.value = value;
        this.id = new AudioFeatureId(track.getId(), audioFeatureType.getId());
    }
}
