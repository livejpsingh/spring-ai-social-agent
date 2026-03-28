package com.socialmediaagent.domain.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GeneratedContentResponse {

    private List<String> variants;
    private List<Double> brandVoiceScores;
    private List<Double> sentimentScores;
    private List<String> suggestedHashtags;
    private List<String> imageDescriptions;
    private Map<String, String> platformAdaptations;
}
