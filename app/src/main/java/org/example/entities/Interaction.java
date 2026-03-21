package org.example.entities;

import java.time.LocalDateTime;

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

@Entity
@Table(name = "interactions")
@Data
public class Interaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "interaction_type_id", nullable = false)
    private int interactionTypeId;

    @Column(name = "track_id", nullable = false)
    private long trackId;

    @Column(name = "context", nullable = true)
    private String context;

    @Column(name = "datetime", nullable = false)
    private LocalDateTime datetime;

    public InteractionDto toDto() {
        return new InteractionDto(id, trackId, context, interactionTypeId, datetime);
    }

    @ManyToOne
    @JoinColumn(name = "track_id")
    private Track track;

    @ManyToOne
    @JoinColumn(name = "interaction_type_id")
    private InteractionType interactionType;
}
