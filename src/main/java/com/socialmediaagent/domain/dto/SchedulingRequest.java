package com.socialmediaagent.domain.dto;

import com.socialmediaagent.domain.enums.SocialPlatform;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchedulingRequest {

    private Long postId;
    private LocalDateTime scheduledTime;
    private boolean useAIOptimization;
    private Set<SocialPlatform> platforms;
}
