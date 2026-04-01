package org.example.responses;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlaylistResponse {
    private List<TrackScoreDto> tracks;
}
