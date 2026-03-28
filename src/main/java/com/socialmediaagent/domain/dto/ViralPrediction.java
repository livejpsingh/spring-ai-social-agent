package com.socialmediaagent.domain.dto;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViralPrediction {

    private Double viralScore;       // 0 to 1
    private Long estimatedReach;
    private LocalDateTime recommendedPostingTime;
    private String explanation;
}
