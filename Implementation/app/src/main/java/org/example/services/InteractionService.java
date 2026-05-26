package org.example.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.example.aspects.NotNullArg;
import org.example.controllers.requests.CreateInteractionRequest;
import org.example.controllers.requests.InteractionDto;
import org.example.controllers.responses.InteractionAnalyticResponse;
import org.example.entities.Interaction;
import org.example.entities.InteractionType;
import org.example.entities.Track;
import org.example.entities.dto.InteractionAnalyticDto;
import org.example.exceptions.NotFoundException;
import org.example.repositories.InteractionRepository;
import org.example.repositories.InteractionTypeRepository;
import org.example.repositories.InteractionTypeStats;
import org.example.repositories.TrackRepository;
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

    public InteractionAnalyticResponse getInteractionsAnalytics(String interactionTypeName, LocalDateTime from, LocalDateTime to, Integer count, String trackTitle, String artistName) {
        Integer interactionTypeId = null;
        if (interactionTypeName != null && !interactionTypeName.isEmpty()) {
            interactionTypeId = interactionTypeRepository.findByName(interactionTypeName).orElseThrow(() -> new NotFoundException("Типа взаимодействия с именем " + interactionTypeName + " нет в базе")).getId();
        }
        Long trackId = null;
        Track track = null;
        if (trackTitle != null && artistName != null && !trackTitle.isEmpty() && !artistName.isEmpty()) {
            track = trackRepository.findByTitleAndArtistName(trackTitle, artistName).orElseThrow(() -> new NotFoundException("Трека с названием " + trackTitle + " и исполнителем " + artistName + " нет в базе"));
            trackId = track.getId();
        }
        List<Interaction> interactions = interactionRepository.findAnalyticInteraction(interactionTypeId, from, to, count, trackId);
        Map<Object, Object> interactionsTracks = interactions.stream().collect(Collectors.toMap(i -> i, i -> i.getTrack()));
        List<InteractionAnalyticDto> result = interactionsTracks.keySet().stream().collect(Collectors.toList()).stream().map(it -> new InteractionAnalyticDto(((Interaction)it).getTrack().toDto(), ((Interaction)it).getContext(), ((Interaction)it).getInteractionType().getName(), ((Interaction)it).getDatetime())).collect(Collectors.toList());
        return new InteractionAnalyticResponse(from, to, track.toDto(), result);
    }

    public List<InteractionTypeStats> formRaoReport(LocalDateTime from, LocalDateTime to) {
        Integer interactionTypeId = interactionTypeRepository.findByName("FULL_LISTENING").orElseThrow(() -> new NotFoundException("Типа взаимодействия с именем " + "FULL_LISTENING" + " нет в базе")).getId();
        List<InteractionTypeStats> stats = interactionRepository.findTracksGroupedInteractions(interactionTypeId, from, to);
        return stats;
    }
}
