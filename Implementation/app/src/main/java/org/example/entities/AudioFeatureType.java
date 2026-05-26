package org.example.entities;

import java.util.ArrayList;
import java.util.List;

import org.example.entities.dto.AudioFeatureTypeDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "audio_features_types")
@NoArgsConstructor
@Data
public class AudioFeatureType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    public AudioFeatureTypeDto toDto() {
        return new AudioFeatureTypeDto(id, name);
    }

    @OneToMany(mappedBy = "audioFeatureType")
    private List<AudioFeature> audioFeatures = new ArrayList<AudioFeature>();

    public AudioFeatureType(String name) {
        this.name = name;
    }
}
