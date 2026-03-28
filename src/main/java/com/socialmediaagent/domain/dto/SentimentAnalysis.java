package com.socialmediaagent.domain.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SentimentAnalysis {

    private Double sentimentScore;  // -1 to 1
    private String emotion;         // joy, anger, sadness, neutral
    private String intent;          // question, complaint, praise, spam
    private boolean spam;
}
