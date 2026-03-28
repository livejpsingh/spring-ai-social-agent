package com.socialmediaagent.service;

import com.socialmediaagent.domain.dto.SentimentAnalysis;
import com.socialmediaagent.domain.enums.EngagementStatus;
import com.socialmediaagent.domain.model.CommentEngagement;
import com.socialmediaagent.repository.CommentEngagementRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EngagementHandlingService {

    @Autowired
    private CommentEngagementRepository commentRepository;

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private SocialMediaApiService apiService;

    @Autowired
    private BrandGuidelineService brandService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Process an incoming comment/DM: sentiment analysis → escalation check → auto-reply.
     */
    @Transactional
    public void processComment(CommentEngagement comment) {
        log.info("Processing comment from user: {}", comment.getUserHandle());

        // 1. Fetch full conversation history for context
        List<CommentEngagement> conversationHistory =
            commentRepository.findByPlatformUserIdAndPlatform(
                comment.getPlatformUserId(),
                comment.getPlatform()
            );
        comment.setConversationContext(buildConversationContext(conversationHistory));

        // 2. Analyze sentiment & intent
        SentimentAnalysis analysis = analyzeSentiment(comment);
        comment.setSentimentScore(analysis.getSentimentScore());
        comment.setEmotion(analysis.getEmotion());
        comment.setIntent(analysis.getIntent());
        comment.setIsSpam(analysis.isSpam());

        // 3. Check escalation rules
        if (shouldEscalate(comment)) {
            comment.setStatus(EngagementStatus.ESCALATED);
            commentRepository.save(comment);
            log.info("Comment escalated for human review: {}", comment.getId());
            return;
        }

        // 4. Generate auto-reply if appropriate
        if (shouldAutoReply(comment)) {
            String response = generateContextualResponse(comment);
            replyToComment(comment, response);
            comment.setStatus(EngagementStatus.AUTO_REPLIED);
            comment.setResponseText(response);
            comment.setResponseTime(LocalDateTime.now());
        } else {
            comment.setStatus(EngagementStatus.PENDING);
        }

        commentRepository.save(comment);
    }

    /**
     * Sentiment analysis using Spring AI.
     */
    private SentimentAnalysis analyzeSentiment(CommentEngagement comment) {
        String analysisPrompt = String.format(
            "Analyze this social media comment for sentiment and intent. " +
            "Respond in JSON format with: sentiment_score (-1 to 1), emotion (joy/anger/sadness/neutral), " +
            "intent (question/complaint/praise/spam), and is_spam (true/false).\n\nComment: %s",
            comment.getCommentText()
        );

        try {
            String responseText = chatClient.prompt()
                .user(analysisPrompt)
                .call()
                .content();
            return parseAnalysisResponse(responseText);
        } catch (Exception e) {
            log.warn("Failed to analyze sentiment: {}", e.getMessage());
            return SentimentAnalysis.builder()
                .sentimentScore(0.0)
                .emotion("neutral")
                .intent("question")
                .spam(false)
                .build();
        }
    }

    /**
     * Determine if a comment should be escalated to a human moderator.
     */
    private boolean shouldEscalate(CommentEngagement comment) {
        if (Boolean.TRUE.equals(comment.getIsSpam())) return false; // Auto-filter spam
        if (comment.getSentimentScore() != null && comment.getSentimentScore() < -0.8) return true;
        if ("complaint".equalsIgnoreCase(comment.getIntent())) return true;
        if (comment.getEmotion() != null && comment.getEmotion().contains("anger")) return true;
        return false;
    }

    /**
     * Determine if auto-reply is appropriate (only for questions and praise).
     */
    private boolean shouldAutoReply(CommentEngagement comment) {
        String intent = comment.getIntent();
        return ("question".equalsIgnoreCase(intent) || "praise".equalsIgnoreCase(intent))
            && !Boolean.TRUE.equals(comment.getIsSpam())
            && (comment.getSentimentScore() == null || comment.getSentimentScore() > -0.5);
    }

    /**
     * Generate a contextual, brand-appropriate response using conversation history.
     */
    public String generateContextualResponse(CommentEngagement comment) {
        StringBuilder contextPrompt = new StringBuilder();
        contextPrompt.append("You are a brand customer service representative. ");
        contextPrompt.append("Generate a friendly, helpful response.\n\n");

        // Add conversation history
        Map<String, Object> context = comment.getConversationContext();
        if (context != null && context.containsKey("history")) {
            contextPrompt.append("Previous conversation:\n");
            contextPrompt.append(context.get("history")).append("\n\n");
        }

        contextPrompt.append(String.format(
            "User sentiment: %s\nUser intent: %s\nCurrent comment: %s",
            comment.getEmotion(), comment.getIntent(), comment.getCommentText()
        ));

        try {
            String reply = chatClient.prompt()
                .user(contextPrompt.toString())
                .call()
                .content();

            // Validate against brand guidelines
            Double brandScore = brandService.validateContentAgainstBrandGuide(reply, null);
            if (brandScore < 0.7) {
                return "Thank you for your comment! We appreciate your feedback. " +
                       "Please reach out to our team for more specific assistance.";
            }

            return reply;
        } catch (Exception e) {
            log.warn("Failed to generate contextual response: {}", e.getMessage());
            return "Thank you for reaching out! We'll get back to you shortly.";
        }
    }

    /**
     * Build conversation context from historical interactions.
     */
    private Map<String, Object> buildConversationContext(List<CommentEngagement> history) {
        Map<String, Object> context = new HashMap<>();

        StringBuilder conversationHistory = new StringBuilder();
        List<CommentEngagement> sorted = history.stream()
            .filter(c -> c.getTimestamp() != null)
            .sorted(Comparator.comparing(CommentEngagement::getTimestamp))
            .collect(Collectors.toList());

        for (CommentEngagement c : sorted) {
            conversationHistory.append(String.format("User: %s\n", c.getCommentText()));
            if (c.getResponseText() != null) {
                conversationHistory.append(String.format("Brand: %s\n\n", c.getResponseText()));
            }
        }

        context.put("history", conversationHistory.toString());
        context.put("messageCount", history.size());
        context.put("lastInteractionDate", sorted.isEmpty() ? null :
            sorted.get(sorted.size() - 1).getTimestamp());

        return context;
    }

    /**
     * Post a reply to the comment via the platform API.
     */
    private void replyToComment(CommentEngagement comment, String response) {
        apiService.replyToComment(
            comment.getPlatform(),
            comment.getPlatformCommentId(),
            response
        );
    }

    /**
     * Parse the AI JSON response into a SentimentAnalysis object.
     */
    private SentimentAnalysis parseAnalysisResponse(String jsonResponse) {
        try {
            // Try to extract JSON from response (AI may wrap it in markdown code blocks)
            String json = jsonResponse;
            if (json.contains("```")) {
                json = json.substring(json.indexOf("{"), json.lastIndexOf("}") + 1);
            }

            var node = objectMapper.readTree(json);
            return SentimentAnalysis.builder()
                .sentimentScore(node.has("sentiment_score") ? node.get("sentiment_score").asDouble() : 0.0)
                .emotion(node.has("emotion") ? node.get("emotion").asText() : "neutral")
                .intent(node.has("intent") ? node.get("intent").asText() : "question")
                .spam(node.has("is_spam") && node.get("is_spam").asBoolean())
                .build();
        } catch (Exception e) {
            log.warn("Failed to parse sentiment response: {}", e.getMessage());
            return SentimentAnalysis.builder()
                .sentimentScore(0.0)
                .emotion("neutral")
                .intent("question")
                .spam(false)
                .build();
        }
    }
}
