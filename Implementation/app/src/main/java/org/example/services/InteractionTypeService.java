package org.example.services;

import java.util.List;

import org.example.aspects.NotNullArg;
import org.example.controllers.requests.CreateInteractionTypeRequest;
import org.example.entities.InteractionType;
import org.example.entities.dto.InteractionTypeDto;
import org.example.exceptions.ConflictException;
import org.example.repositories.InteractionTypeRepository;
import org.springframework.stereotype.Service;

@Service
public class InteractionTypeService {
    private final InteractionTypeRepository interactionTypeRepository;

    public InteractionTypeService(InteractionTypeRepository interactionTypeRepository) {
        this.interactionTypeRepository = interactionTypeRepository;
    }

    @NotNullArg
    public void addInteractionType(CreateInteractionTypeRequest request) {
        String interactionTypeName = request.getName();
        if (interactionTypeRepository.existsByName(request.getName())) {
            throw new ConflictException("Тип взаимодействия с названием " + interactionTypeName + " уже существует");
        }

        InteractionType interactionType = new InteractionType(interactionTypeName);
        interactionTypeRepository.save(interactionType);
    }

    public List<InteractionTypeDto> getAllInteractionTypes() {
        return interactionTypeRepository.findAll().stream().map((InteractionType t) -> t.toDto()).toList();
    }
}
