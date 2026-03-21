package org.example.entities;

import java.util.List;

import org.example.entities.dto.InteractionTypeDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "interaction_types")
@Data
public class InteractionType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    public InteractionTypeDto toDto() {
        return new InteractionTypeDto(id, name);
    }

    @OneToMany(mappedBy = "interactionType")
    private List<Interaction> interactions;
}
