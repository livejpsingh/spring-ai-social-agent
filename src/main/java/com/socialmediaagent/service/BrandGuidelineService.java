package com.socialmediaagent.service;

import com.socialmediaagent.domain.model.BrandGuideline;
import com.socialmediaagent.repository.BrandGuidelineRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
public class BrandGuidelineService {

    @Autowired
    private BrandGuidelineRepository guidelineRepository;

    @Autowired
    private ChatClient chatClient;

    /**
     * Retrieve brand guideline by ID
     */
    public BrandGuideline getGuideline(String guidelineId) {
        return guidelineRepository.findById(Long.parseLong(guidelineId))
            .orElse(null);
    }

    /**
     * Retrieve brand guideline by user ID
     */
    public BrandGuideline getGuidelineByUserId(Long userId) {
        return guidelineRepository.findByUserId(userId)
            .orElse(null);
    }

    /**
     * Validate content against brand guidelines using AI
     * Returns a score from 0.0 to 1.0
     */
    public Double validateContentAgainstBrandGuide(String content, String guidelineId) {
        if (guidelineId == null) {
            return 0.85; // Default score when no guidelines configured
        }

        BrandGuideline guideline = getGuideline(guidelineId);
        if (guideline == null) {
            return 0.85;
        }

        StringBuilder validationPrompt = new StringBuilder();
        validationPrompt.append("Score how well this content adheres to the brand guidelines. ");
        validationPrompt.append("Respond with ONLY a number between 0.0 and 1.0.\n\n");
        validationPrompt.append("Brand Guidelines:\n").append(guideline.getGuidelineText()).append("\n\n");

        if (guideline.getVocabularyPreferences() != null && !guideline.getVocabularyPreferences().isEmpty()) {
            validationPrompt.append("Preferred vocabulary: ")
                .append(String.join(", ", guideline.getVocabularyPreferences())).append("\n");
        }

        Set<String> prohibited = guideline.getProhibitedTerms();
        if (prohibited != null && !prohibited.isEmpty()) {
            validationPrompt.append("Prohibited terms: ")
                .append(String.join(", ", prohibited)).append("\n");
        }

        validationPrompt.append("\nContent to validate:\n").append(content);

        try {
            var response = chatClient.prompt()
                .user(validationPrompt.toString())
                .call()
                .content();
            return Double.parseDouble(response.trim());
        } catch (Exception e) {
            log.warn("Failed to validate against brand guidelines: {}", e.getMessage());
            return 0.75; // Moderate score on failure
        }
    }

    /**
     * Save or update brand guidelines
     */
    public BrandGuideline saveGuideline(BrandGuideline guideline) {
        return guidelineRepository.save(guideline);
    }
}
