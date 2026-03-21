package org.example.repositories;

import org.example.entities.InteractionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InteractionTypeRepository extends JpaRepository<InteractionType, Integer> {
    
}
