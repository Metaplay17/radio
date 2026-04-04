package org.example.services;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.example.aspects.NotNullArg;
import org.example.entities.Interaction;
import org.example.entities.InteractionType;
import org.example.entities.Track;
import org.example.entities.dto.InteractionAnalyticDto;
import org.example.exceptions.NotFoundException;
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
            String interactionTypeName = i.getName();
            String context = i.getContext();
            Long msec = i.getDatetimeMsec();

            Track track = trackRepository.findById(trackId).orElseThrow(() -> new NotFoundException("Трека с id = " + trackId + " нет в базе"));
            InteractionType interactionType = interactionTypeRepository.findByName(interactionTypeName).orElseThrow(() -> new NotFoundException("Типа взаимодействия с названием = " + interactionTypeName + " нет в базе"));

            Interaction interaction = new Interaction(interactionType, track, context, msec);
            interactions.add(interaction);
        });
        interactionRepository.saveAll(interactions);
    }

    public List<InteractionAnalyticDto> getInteractionsAnalytics(String interactionTypeName, Long msecFrom, Long msecTo, Integer count, Long trackId) {
        InteractionType interactionType = interactionTypeRepository.findByName(interactionTypeName).orElseThrow(() -> new NotFoundException("Типа взаимодействия с именем " + interactionTypeName + " нет в базе"));
        List<Interaction> interactions = interactionRepository.findAnalyticInteraction(interactionType.getId(), LocalDateTime.ofInstant(Instant.ofEpochMilli(msecFrom), ZoneId.systemDefault()), LocalDateTime.ofInstant(Instant.ofEpochMilli(msecTo), ZoneId.systemDefault()), count, trackId);
        Map<Object, Object> interactionsTracks = interactions.stream().collect(Collectors.toMap(i -> i, i -> i.getTrack()));
        List<InteractionAnalyticDto> result = interactionsTracks.keySet().stream().map(o -> ((Track)o)).collect(Collectors.toList()).stream().map(it -> new InteractionAnalyticDto(it.toDto(), ((Interaction)interactionsTracks.get(it)).getContext(), interactionTypeName, ((Interaction)interactionsTracks.get(it)).getDatetime())).collect(Collectors.toList());
        return result;
    }
}
