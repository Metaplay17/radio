package org.example.entities;

import java.util.ArrayList;
import java.util.List;

import org.example.entities.dto.ArtistDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "artists")
@Data
public class Artist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    public ArtistDto toDto() {
        return new ArtistDto(id, name);
    }

    @OneToMany(mappedBy = "artist")
    private List<Track> tracks = new ArrayList<Track>();

    public Artist(String name) {
        this.name = name;
    }
}
