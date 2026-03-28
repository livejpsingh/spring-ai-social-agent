package com.socialmediaagent.service;

import com.socialmediaagent.domain.dto.ContentGenerationRequest;
import com.socialmediaagent.domain.dto.GeneratedContentResponse;
import com.socialmediaagent.domain.enums.SocialPlatform;
import com.socialmediaagent.domain.model.BrandGuideline;
import com.socialmediaagent.repository.ContentPostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ContentGenerationService {

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private BrandGuidelineService brandGuidelineService;

    @Autowired
    private ContentPostRepository contentRepository;

    /**
     * Generate content with AI considering brand guidelines.
     * Produces multiple variants, validates against brand voice, analyzes sentiment,
     * and adapts for each target platform.
     */
    public GeneratedContentResponse generateContent(ContentGenerationRequest request) {
        log.info("Generating content with tone: {}, language: {}", request.getTone(), request.getLanguage());

        // 1. Build comprehensive prompt
        String prompt = buildPromptWithContext(request);

        // 2. Call Spring AI ChatClient
        String aiResponse = chatClient.prompt()
            .user(prompt)
            .call()
            .content();

        // 3. Parse AI response into variants
        List<String> variants = parseVariants(aiResponse);

        // 4. Validate each variant against brand guidelines
        List<Double> brandScores = new ArrayList<>();
        for (String variant : variants) {
            Double score = brandGuidelineService.validateContentAgainstBrandGuide(
                variant, request.getBrandVoiceGuideId()
            );
            brandScores.add(score);
        }

        // 5. Analyze sentiment of each variant
        List<Double> sentimentScores = variants.stream()
            .map(this::analyzeSentiment)
            .collect(Collectors.toList());

        // 6. Generate hashtags if requested
        List<String> hashtags = request.isIncludeHashtags()
            ? generateHashtags(variants.get(0), request.getTargetPlatforms())
            : new ArrayList<>();

        // 7. Adapt content for each target platform
        Map<String, String> platformAdaptations = new HashMap<>();
        if (request.getTargetPlatforms() != null) {
            for (SocialPlatform platform : request.getTargetPlatforms()) {
                platformAdaptations.put(
                    platform.name(),
                    adaptContentForPlatform(variants.get(0), platform)
                );
            }
        }

        return GeneratedContentResponse.builder()
            .variants(variants)
            .brandVoiceScores(brandScores)
            .sentimentScores(sentimentScores)
            .suggestedHashtags(hashtags)
            .platformAdaptations(platformAdaptations)
            .build();
    }

    /**
     * Build a sophisticated prompt with brand context, tone, and platform constraints.
     */
    private String buildPromptWithContext(ContentGenerationRequest request) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("You are a professional social media content creator.\n\n");

        prompt.append(String.format("Generate %d content variants with the following specifications:\n",
            request.getVariantsToGenerate()));

        prompt.append(String.format("- Topic/Prompt: %s\n", request.getPrompt()));
        prompt.append(String.format("- Tone: %s\n", request.getTone()));
        prompt.append(String.format("- Language: %s\n",
            request.getLanguage() != null ? request.getLanguage() : "en"));
        prompt.append(String.format("- Content Type: %s\n",
            request.getContentType() != null ? request.getContentType() : "general"));

        // Add brand guidelines context if available
        if (request.getBrandVoiceGuideId() != null) {
            BrandGuideline guide = brandGuidelineService.getGuideline(request.getBrandVoiceGuideId());
            if (guide != null) {
                prompt.append(String.format("\nBrand Guidelines:\n%s\n\n", guide.getGuidelineText()));

                if (guide.getVocabularyPreferences() != null && !guide.getVocabularyPreferences().isEmpty()) {
                    prompt.append(String.format("Preferred Vocabulary: %s\n",
                        String.join(", ", guide.getVocabularyPreferences())));
                }
            }
        }

        prompt.append("\nFor each variant:\n");
        prompt.append("1. Ensure it's engaging and platform-appropriate\n");
        prompt.append("2. Include a call-to-action\n");
        prompt.append("3. Keep sentiment positive (unless crisis communication)\n");
        prompt.append("4. Format as: VARIANT_1: [content] ... VARIANT_2: [content]\n");

        return prompt.toString();
    }

    /**
     * Parse multiple content variants from AI response.
     */
    private List<String> parseVariants(String response) {
        List<String> variants = new ArrayList<>();
        String[] lines = response.split("\n");

        for (String line : lines) {
            if (line.matches("^VARIANT_\\d+:.*")) {
                String content = line.replaceFirst("^VARIANT_\\d+:\\s*", "").trim();
                if (!content.isEmpty()) {
                    variants.add(content);
                }
            }
        }

        // Fallback: if no variants parsed, return the whole response as a single variant
        return variants.isEmpty() ? List.of(response) : variants;
    }

    /**
     * Analyze sentiment of content using Spring AI.
     * Returns a score between -1 (very negative) and 1 (very positive).
     */
    public Double analyzeSentiment(String content) {
        String sentimentPrompt = String.format(
            "Analyze the sentiment of this text and respond with ONLY a number between -1 and 1:\n%s",
            content
        );

        try {
            String result = chatClient.prompt()
                .user(sentimentPrompt)
                .call()
                .content();
            return Double.parseDouble(result.trim());
        } catch (NumberFormatException e) {
            log.warn("Failed to parse sentiment score, defaulting to 0.0");
            return 0.0;
        }
    }

    /**
     * Generate trending hashtags for content.
     */
    public List<String> generateHashtags(String content, Set<SocialPlatform> platforms) {
        String hashtagPrompt = String.format(
            "Generate 10 relevant hashtags for this content (comma-separated, no # symbol):\n%s",
            content
        );

        try {
            String result = chatClient.prompt()
                .user(hashtagPrompt)
                .call()
                .content();
            return Arrays.stream(result.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .limit(10)
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("Failed to generate hashtags: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Adapt content for specific platform constraints (character limits, format).
     */
    public String adaptContentForPlatform(String content, SocialPlatform platform) {
        int maxLength = getMaxLengthForPlatform(platform);
        String platformPrompt = String.format(
            "Rewrite this content for %s (max %d characters), maintaining tone and message:\n%s",
            platform.name(), maxLength, content
        );

        try {
            String adapted = chatClient.prompt()
                .user(platformPrompt)
                .call()
                .content();

            // Ensure it doesn't exceed platform limit
            return adapted.length() > maxLength
                ? adapted.substring(0, maxLength - 3) + "..."
                : adapted;
        } catch (Exception e) {
            log.warn("Failed to adapt content for {}: {}", platform, e.getMessage());
            return content.length() > maxLength
                ? content.substring(0, maxLength - 3) + "..."
                : content;
        }
    }

    private int getMaxLengthForPlatform(SocialPlatform platform) {
        return switch (platform) {
            case TWITTER -> 280;
            case LINKEDIN -> 3000;
            case INSTAGRAM -> 2200;
            case FACEBOOK -> 63206;
            case TIKTOK -> 2500;
        };
    }
}
