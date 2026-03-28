package com.socialmediaagent.service;

import com.socialmediaagent.domain.dto.DashboardMetrics;
import com.socialmediaagent.domain.dto.ViralPrediction;
import com.socialmediaagent.domain.enums.SocialPlatform;
import com.socialmediaagent.domain.model.AnalyticsMetric;
import com.socialmediaagent.domain.model.ContentPost;
import com.socialmediaagent.repository.AnalyticsMetricRepository;
import com.socialmediaagent.repository.ContentPostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AnalyticsService {

    @Autowired
    private AnalyticsMetricRepository metricsRepository;

    @Autowired
    private ContentPostRepository postRepository;

    @Autowired
    private ChatClient chatClient;

    /**
     * Aggregate metrics across all platforms for a dashboard view.
     */
    public DashboardMetrics getDashboardMetrics(Long userId, LocalDate startDate, LocalDate endDate) {
        List<AnalyticsMetric> allMetrics = metricsRepository.findAll();

        // Total reach
        Double totalReach = allMetrics.stream()
            .filter(m -> "reach".equals(m.getMetricType()))
            .mapToDouble(AnalyticsMetric::getValue)
            .sum();

        // Total likes
        Double totalLikes = allMetrics.stream()
            .filter(m -> "likes".equals(m.getMetricType()))
            .mapToDouble(AnalyticsMetric::getValue)
            .sum();

        // Engagement rate
        Double engagementRate = totalReach > 0 ? totalLikes / totalReach : 0.0;

        // Audience growth
        Double audienceGrowth = calculateAudienceGrowth(userId, startDate, endDate);

        // Top performing content
        List<ContentPost> topPosts = postRepository.findTopPerformingLast90Days(userId)
            .stream().limit(5).collect(Collectors.toList());

        // Platform breakdown
        Map<String, Double> platformBreakdown = allMetrics.stream()
            .filter(m -> "engagement_rate".equals(m.getMetricType()))
            .collect(Collectors.groupingBy(
                m -> m.getPlatform() != null ? m.getPlatform().name() : "UNKNOWN",
                Collectors.averagingDouble(AnalyticsMetric::getValue)
            ));

        return DashboardMetrics.builder()
            .totalReach(totalReach.longValue())
            .engagementRate(engagementRate)
            .audienceGrowth(audienceGrowth)
            .topPosts(topPosts)
            .platformBreakdown(platformBreakdown)
            .build();
    }

    /**
     * Predict viral potential of content before posting.
     * Uses AI to estimate reach and engagement probability.
     */
    public ViralPrediction predictViralPotential(String content, SocialPlatform platform) {
        String predictionPrompt = String.format(
            "Analyze this social media content for viral potential on %s. " +
            "Respond with ONLY a number between 0.0 and 1.0 representing the probability of high engagement:\n\n%s",
            platform.name(), content
        );

        try {
            String result = chatClient.prompt()
                .user(predictionPrompt)
                .call()
                .content();

            double score = Double.parseDouble(result.trim());

            return ViralPrediction.builder()
                .viralScore(Math.min(Math.max(score, 0.0), 1.0))
                .estimatedReach(estimateReach(score))
                .explanation("AI-predicted engagement probability")
                .build();

        } catch (Exception e) {
            log.warn("Failed to predict viral potential: {}", e.getMessage());
            return ViralPrediction.builder()
                .viralScore(0.5)
                .estimatedReach(1000L)
                .explanation("Default prediction (AI unavailable)")
                .build();
        }
    }

    /**
     * Create an A/B test between two content variants.
     */
    public void createABTest(Long userId, String contentA, String contentB, LocalDate endDate) {
        log.info("Creating A/B test for user {} ending {}", userId, endDate);
        // Implementation: split audience, track engagement separately
    }

    private Double calculateAudienceGrowth(Long userId, LocalDate startDate, LocalDate endDate) {
        // Calculate growth rate over the given period
        return 0.0;
    }

    private Long estimateReach(double viralScore) {
        // Rough estimation based on viral score
        if (viralScore > 0.9) return 100000L;
        if (viralScore > 0.7) return 50000L;
        if (viralScore > 0.5) return 10000L;
        if (viralScore > 0.3) return 5000L;
        return 1000L;
    }
}
