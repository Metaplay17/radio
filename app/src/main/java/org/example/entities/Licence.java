package org.example.entities;

import java.time.LocalDate;

import org.example.entities.dto.LicenceDto;

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
@Table(name = "licences")
@Data
@NoArgsConstructor
public class Licence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "registered", nullable = false)
    private LocalDate registered;

    @Column(name = "duration", nullable = true)
    private int duration;

    @ManyToOne
    @JoinColumn(name = "track_id")
    private Track track;

    public Licence(Track track, LocalDate registered, int duration) {
        this.track = track;
        this.registered = registered;
        this.duration = duration;
    }

    public LicenceDto toDto() {
        return new LicenceDto(id, track.toDto(), registered, duration);
    }
}

