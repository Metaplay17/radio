package org.example.entities;

import java.time.LocalDateTime;

import org.example.entities.dto.PlaylistDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Table(name = "playlists")
@Entity
@Data
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = true)
    private String name;

    @Column(name = "description", nullable = true)
    private String description;

    @Column(name = "datetime", nullable = false)
    private LocalDateTime datetime;

    @Column(name = "duration", nullable = false)
    private int duration;

    public PlaylistDto toDto() {
        return new PlaylistDto(id, name, description, datetime, duration);
    }

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    public Playlist(String name, String description, LocalDateTime datetime, int duration) {
        this.name = name;
        this.description = description;
        this.datetime = datetime;
        this.duration = duration;
    }
}
