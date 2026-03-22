package org.example.services;

import org.example.aspects.NotNullArg;
import org.example.entities.Interaction;
import org.example.exceptions.ConflictException;
import org.example.repositories.InteractionRepository;
import org.example.repositories.InteractionTypeRepository;
import org.example.repositories.TrackRepository;
import org.example.requests.CreateInteractionRequest;
import org.springframework.stereotype.Service;

@Service
public class InteractionService {
    private final InteractionRepository interactionRepository;
    private final TrackRepository trackRepository;
    private final InteractionTypeRepository interactionTypeRepository;

    public InteractionService(InteractionRepository interactionRepository, TrackRepository trackRepository, InteractionTypeRepository interactionTypeRepository) {
        this.interactionRepository = interactionRepository;
        this.trackRepository = trackRepository;
        this.interactionTypeRepository = interactionTypeRepository;
    }

    @NotNullArg
    public void addInteraction(CreateInteractionRequest request) {
        long trackId = request.getTrackId();
        int interactionTypeId = request.getInteractionTypeId();
        String context = request.getContext();

        if (!trackRepository.existsById(trackId)) {
            throw new ConflictException("Трека с id = " + trackId + " нет в базе");
        }

        if (!interactionTypeRepository.existsById(interactionTypeId)) {
            throw new ConflictException("Типа взаимодействия с id = " + interactionTypeId + " нет в базе");
        }

        Interaction interaction = new Interaction(interactionTypeId, trackId, context);
        interactionRepository.save(interaction);
    }
}
