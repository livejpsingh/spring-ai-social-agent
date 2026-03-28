package com.socialmediaagent.domain.dto;

import com.socialmediaagent.domain.enums.SocialPlatform;
import com.socialmediaagent.domain.enums.Tone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentGenerationRequest {

    @NotBlank(message = "Prompt is required")
    private String prompt;

    private String contentType; // news, announcement, thought_leadership, casual

    @NotNull(message = "Tone is required")
    private Tone tone;

    private String language;

    private Set<SocialPlatform> targetPlatforms;

    private boolean includeHashtags;
    private boolean includeImageSuggestions;

    @Builder.Default
    private int variantsToGenerate = 3;

    private String brandVoiceGuideId;
}
