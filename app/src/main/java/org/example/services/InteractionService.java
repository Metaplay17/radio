package org.example.services;

import java.util.ArrayList;
import java.util.List;

import org.example.aspects.NotNullArg;
import org.example.entities.Interaction;
import org.example.entities.InteractionType;
import org.example.entities.Track;
import org.example.exceptions.ConflictException;
import org.example.repositories.InteractionRepository;
import org.example.repositories.InteractionTypeRepository;
import org.example.repositories.TrackRepository;
import org.example.requests.CreateInteractionRequest;
import org.example.requests.InteractionDto;
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
        List<Interaction> interactions = new ArrayList<Interaction>();

        request.getInteractions().forEach((InteractionDto i) -> {
            long trackId = i.getTrackId();
            int interactionTypeId = i.getInteractionTypeId();
            String context = i.getContext();

            Track track = trackRepository.findById(trackId).orElseThrow(() -> new ConflictException("Трека с id = " + trackId + " нет в базе"));
            InteractionType interactionType = interactionTypeRepository.findById(interactionTypeId).orElseThrow(() -> new ConflictException("Типа взаимодействия с id = " + interactionTypeId + " нет в базе"));

            Interaction interaction = new Interaction(interactionType, track, context);
            interactions.add(interaction);
        });
        interactionRepository.saveAll(interactions);
    }
}
