package org.example.entities;

import java.util.ArrayList;
import java.util.List;

import org.example.entities.dto.GenreDto;

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
@Table(name = "genres")
@Data
@NoArgsConstructor
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    public GenreDto toDto() {
        return new GenreDto(id, name);
    }

    @OneToMany(mappedBy = "genre")
    private List<Track> tracks = new ArrayList<Track>();

    public Genre(String name) {
        this.name = name;
    }
}
