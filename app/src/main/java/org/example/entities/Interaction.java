package org.example.entities;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.example.entities.dto.InteractionDto;

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
@Table(name = "interactions")
@Data
@NoArgsConstructor
public class Interaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "context", nullable = true)
    private String context;

    @Column(name = "datetime", nullable = false)
    private LocalDateTime datetime = LocalDateTime.now();

    public InteractionDto toDto() {
        return new InteractionDto(id, track.toDto(), context, interactionType.getId(), datetime);
    }

    @ManyToOne
    @JoinColumn(name = "track_id")
    private Track track;

    @ManyToOne
    @JoinColumn(name = "interaction_type_id")
    private InteractionType interactionType;

    public Interaction(InteractionType interactionType, Track track, String context, Long msec) {
        this.interactionType = interactionType;
        this.track = track;
        this.context = context;
        this.datetime = LocalDateTime.ofInstant(Instant.ofEpochMilli(msec), ZoneId.systemDefault());
    }
}
