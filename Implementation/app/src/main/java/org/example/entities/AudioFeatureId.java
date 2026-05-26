package org.example.entities;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AudioFeatureId {
    @Column(name = "track_id")
    private long trackId;

    @Column(name = "feature_type_id")
    private int featureTypeId;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }

        return this.getTrackId() == ((AudioFeatureId)o).getTrackId() && this.getFeatureTypeId() == ((AudioFeatureId)o).getFeatureTypeId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(trackId, featureTypeId);
    }
}
