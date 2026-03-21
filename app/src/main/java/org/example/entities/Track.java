package org.example.entities;

import java.util.List;

import org.example.entities.dto.TrackDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "tracks")
@Data
public class Track {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "genre_id", nullable = false)
    private int genreId;

    @Column(name = "artist_id", nullable = false)
    private int artistId;

    @Column(name = "duration", nullable = false)
    private int duration;

    public TrackDto toDto() {
        return new TrackDto(id, title, artistId, genreId, duration);
    }

    @ManyToOne
    @JoinColumn(name = "artist_id")
    private Artist artist;

    @OneToMany(mappedBy = "track")
    private List<Licence> licences;

    @ManyToOne
    @JoinColumn(name = "genre_id")
    private Genre genre;

    @OneToMany(mappedBy = "track")
    private List<Interaction> interactions;

    @OneToMany(mappedBy = "track")
    private List<AudioFeature> audioFeatures;
}
