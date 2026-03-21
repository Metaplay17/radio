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

@Entity
@Table(name = "licences")
@Data
public class Licence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "track_id", nullable = false)
    private long trackId;

    @Column(name = "registered", nullable = false)
    private LocalDate registered;

    @Column(name = "duration", nullable = true)
    private Integer duration;

    public LicenceDto toDto() {
        return new LicenceDto(id ,trackId, registered, duration);
    }

    @ManyToOne
    @JoinColumn(name = "track_id")
    private Track track;
}

