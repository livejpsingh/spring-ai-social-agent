// ============================================================================
// SOCIAL MEDIA AGENT - SPRING BOOT IMPLEMENTATION
// ============================================================================

// ============================================================================
// 1. DOMAIN MODELS
// ============================================================================

package com.socialmediaagent.domain.model;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "content_posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(columnDefinition = "TEXT")
    private String contentText;
    
    @ElementCollection
    private List<String> mediaUrls;
    
    @Enumerated(EnumType.STRING)
    private PostStatus status; // DRAFT, SCHEDULED, PUBLISHED, ARCHIVED
    
    @Enumerated(EnumType.STRING)
    private Tone tone; // PROFESSIONAL, CASUAL, WITTY, INSPIRATIONAL
    
    private String language; // en, es, fr, etc.
    
    private LocalDateTime scheduledTime;
    private LocalDateTime publishedTime;
    
    @Column(columnDefinition = "jsonb")
    private Map<String, String> platformPostIds; // {twitter: "123", linkedin: "456"}
    
    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus; // PENDING, APPROVED, REJECTED, REVISION_REQUESTED
    
    private Long approverId;
    private LocalDateTime approvalTime;
    
    @Column(columnDefinition = "TEXT")
    private String approvalFeedback;
    
    @Column(columnDefinition = "jsonb")
    private Map<String, Double> performanceMetrics; // likes, shares, engagement_rate, etc.
    
    private Double sentimentScore; // -1 to 1
    private Double brandVoiceScore; // 0 to 1
    
    @ElementCollection
    private Set<String> tags;
    
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> revisionHistory;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Composite key for multi-platform posts
    private String contentFingerprint; // SHA-256 hash for deduplication
}

@Entity
@Table(name = "scheduled_jobs")
@Data
@NoArgsConstructor
public class ScheduledJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long postId;
    
    @Enumerated(EnumType.STRING)
    private SocialPlatform platform;
    
    private LocalDateTime scheduledTime;
    private LocalDateTime executedTime;
    
    @Enumerated(EnumType.STRING)
    private JobStatus status; // PENDING, PROCESSING, COMPLETED, FAILED
    
    private int retryCount;
    private LocalDateTime lastRetryTime;
    private LocalDateTime nextRetryTime;
    
    @Column(columnDefinition = "TEXT")
    private String errorLog;
    
    private LocalDateTime createdAt;
}

@Entity
@Table(name = "comments_engagement")
@Data
@NoArgsConstructor
public class CommentEngagement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String platformCommentId;
    private String platformUserId;
    private String userHandle;
    
    private Long postId;
    
    @Column(columnDefinition = "TEXT")
    private String commentText;
    
    private LocalDateTime timestamp;
    
    private Double sentimentScore;
    private String emotion; // joy, anger, sadness, neutral
    private String intent; // question, complaint, praise, spam
    
    private String language;
    private Boolean isSpam;
    private Boolean flagged;
    
    @Enumerated(EnumType.STRING)
    private SocialPlatform platform;
    
    @Enumerated(EnumType.STRING)
    private EngagementStatus status; // PENDING, AUTO_REPLIED, HUMAN_REVIEWED, ESCALATED
    
    private Long assignedUserId;
    
    @Column(columnDefinition = "TEXT")
    private String responseText;
    
    private LocalDateTime responseTime;
    private String autoReplyId;
    
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> conversationContext;
    
    private LocalDateTime createdAt;
}

@Entity
@Table(name = "analytics_metrics")
@Data
@NoArgsConstructor
public class AnalyticsMetric {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long postId;
    private Long accountId;
    
    @Enumerated(EnumType.STRING)
    private SocialPlatform platform;
    
    private String metricType; // likes, comments, shares, impressions, reach, engagement_rate
    
    private LocalDateTime timestamp;
    private Double value;
    
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> breakdownBySegment; // age, gender, location, interests
    
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> platformSpecificData;
}

@Entity
@Table(name = "approval_workflows")
@Data
@NoArgsConstructor
public class ApprovalWorkflow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long postId;
    
    @Enumerated(EnumType.STRING)
    private WorkflowStatus status; // PENDING, IN_REVIEW, REVISION_REQUESTED, APPROVED, REJECTED
    
    private Long createdBy;
    private Long assignedTo;
    
    private LocalDateTime createdAt;
    private LocalDateTime dueDate;
    private LocalDateTime completedAt;
    
    @Column(columnDefinition = "TEXT")
    private String feedbackText;
    
    private int revisionsCount;
    
    @Column(columnDefinition = "jsonb")
    private List<AuditLog> auditTrail;
    
    private Double riskScore;
}

@Entity
@Table(name = "brand_guidelines")
@Data
@NoArgsConstructor
public class BrandGuideline {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long userId;
    
    @Column(columnDefinition = "TEXT")
    private String guidelineText;
    
    @Column(columnDefinition = "jsonb")
    private List<String> toneSamples; // Examples of good content
    
    @Column(columnDefinition = "jsonb")
    private Set<String> vocabularyPreferences;
    
    @Column(columnDefinition = "jsonb")
    private Set<String> prohibitedTerms;
    
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> visualGuidelines;
    
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> complianceRules;
    
    private int version;
    private LocalDateTime lastUpdated;
}

// ============================================================================
// ENUMS & DTOs
// ============================================================================

public enum PostStatus { DRAFT, SCHEDULED, PUBLISHED, ARCHIVED, DELETED }
public enum ApprovalStatus { PENDING, APPROVED, REJECTED, REVISION_REQUESTED }
public enum SocialPlatform { TWITTER, LINKEDIN, INSTAGRAM, FACEBOOK, TIKTOK }
public enum Tone { PROFESSIONAL, CASUAL, WITTY, INSPIRATIONAL, EDUCATIONAL }
public enum JobStatus { PENDING, PROCESSING, COMPLETED, FAILED, RETRY }
public enum EngagementStatus { PENDING, AUTO_REPLIED, HUMAN_REVIEWED, ESCALATED }
public enum WorkflowStatus { PENDING, IN_REVIEW, REVISION_REQUESTED, APPROVED, REJECTED }

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentGenerationRequest {
    private String prompt;
    private String contentType; // news, announcement, thought_leadership, casual
    private Tone tone;
    private String language;
    private Set<SocialPlatform> targetPlatforms;
    private boolean includeHashtags;
    private boolean includeImageSuggestions;
    private int variantsToGenerate; // default 3
    private String brandVoiceGuideId;
}

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneratedContentResponse {
    private List<String> variants;
    private List<Double> brandVoiceScores;
    private List<Double> sentimentScores;
    private List<String> suggestedHashtags;
    private List<String> imageDescriptions;
    private Map<String, String> platformAdaptations;
}

@Data
public class SchedulingRequest {
    private Long postId;
    private LocalDateTime scheduledTime;
    private boolean useAIOptimization;
    private Set<SocialPlatform> platforms;
}

@Data
public class AuditLog {
    private Long userId;
    private String action;
    private String details;
    private LocalDateTime timestamp;
}

// ============================================================================
// 2. REPOSITORY LAYER
// ============================================================================

package com.socialmediaagent.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ContentPostRepository extends JpaRepository<ContentPost, Long> {
    
    List<ContentPost> findByUserIdAndStatus(Long userId, PostStatus status);
    
    @Query("SELECT cp FROM ContentPost cp WHERE cp.userId = :userId " +
           "AND cp.scheduledTime BETWEEN :start AND :end " +
           "ORDER BY cp.scheduledTime ASC")
    List<ContentPost> findScheduledBetween(
        @Param("userId") Long userId,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );
    
    @Query("SELECT cp FROM ContentPost cp WHERE cp.approvalStatus = 'PENDING' " +
           "AND cp.userId = :userId ORDER BY cp.createdAt ASC")
    List<ContentPost> findPendingApproval(@Param("userId") Long userId);
    
    @Query(value = "SELECT cp.* FROM content_posts cp " +
           "WHERE cp.user_id = :userId AND cp.published_time > now() - interval '90 days' " +
           "ORDER BY cp.performance_metrics->'engagement_rate' DESC", nativeQuery = true)
    List<ContentPost> findTopPerformingLast90Days(@Param("userId") Long userId);
}

public interface ScheduledJobRepository extends JpaRepository<ScheduledJob, Long> {
    
    List<ScheduledJob> findByStatusAndScheduledTimeLessThanEqual(
        JobStatus status, LocalDateTime now
    );
    
    List<ScheduledJob> findByPostId(Long postId);
    
    @Query("SELECT COUNT(sj) FROM ScheduledJob sj " +
           "WHERE sj.platform = :platform AND sj.status = 'PROCESSING' " +
           "AND sj.scheduledTime > :windowStart AND sj.scheduledTime < :windowEnd")
    int countJobsInWindow(
        @Param("platform") SocialPlatform platform,
        @Param("windowStart") LocalDateTime windowStart,
        @Param("windowEnd") LocalDateTime windowEnd
    );
}

public interface CommentEngagementRepository extends JpaRepository<CommentEngagement, Long> {
    
    List<CommentEngagement> findByPostIdAndStatus(Long postId, EngagementStatus status);
    
    @Query("SELECT ce FROM CommentEngagement ce WHERE ce.userId = :userId " +
           "AND ce.status IN ('PENDING', 'ESCALATED') " +
           "ORDER BY ce.timestamp DESC")
    List<CommentEngagement> findPendingReview(@Param("userId") Long userId);
    
    List<CommentEngagement> findByPlatformUserIdAndPlatform(
        String platformUserId, SocialPlatform platform
    );
}

public interface AnalyticsMetricRepository extends JpaRepository<AnalyticsMetric, Long> {
    
    @Query("SELECT am FROM AnalyticsMetric am WHERE am.postId = :postId " +
           "ORDER BY am.timestamp DESC")
    List<AnalyticsMetric> findByPostIdOrdered(@Param("postId") Long postId);
    
    @Query(value = "SELECT DATE(timestamp) as date, AVG(value) as avg_value " +
           "FROM analytics_metrics WHERE account_id = :accountId " +
           "AND metric_type = :metricType " +
           "AND timestamp > now() - interval '30 days' " +
           "GROUP BY DATE(timestamp) ORDER BY date", nativeQuery = true)
    List<Object> getDailyMetricTrend(
        @Param("accountId") Long accountId,
        @Param("metricType") String metricType
    );
}

// ============================================================================
// 3. SERVICE LAYER - CONTENT GENERATION
// ============================================================================

package com.socialmediaagent.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Service
public class ContentGenerationService {
    
    @Autowired
    private ChatClient chatClient;
    
    @Autowired
    private BrandGuidelineService brandGuidelineService;
    
    @Autowired
    private ContentPostRepository contentRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * Generate content with AI considering brand guidelines
     */
    public GeneratedContentResponse generateContent(ContentGenerationRequest request) {
        log.info("Generating content with tone: {}, language: {}", request.getTone(), request.getLanguage());
        
        // 1. Build comprehensive prompt
        String prompt = buildPromptWithContext(request);
        
        // 2. Call Spring AI ChatClient
        ChatResponse response = chatClient.call(
            new Prompt(prompt)
        );
        
        String aiResponse = response.getResult().getOutput().getContent();
        
        // 3. Parse AI response
        List<String> variants = parseVariants(aiResponse);
        
        // 4. Validate against brand guidelines
        List<Double> brandScores = new ArrayList<>();
        for (String variant : variants) {
            Double score = brandGuidelineService.validateContentAgainstBrandGuide(
                variant, request.getBrandVoiceGuideId()
            );
            brandScores.add(score);
        }
        
        // 5. Analyze sentiment
        List<Double> sentimentScores = variants.stream()
            .map(this::analyzeSentiment)
            .collect(Collectors.toList());
        
        // 6. Extract/Generate hashtags
        List<String> hashtags = request.isIncludeHashtags() 
            ? generateHashtags(variants.get(0), request.getTargetPlatforms())
            : new ArrayList<>();
        
        // 7. Adapt for each platform
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
     * Build sophisticated prompt with brand context
     */
    private String buildPromptWithContext(ContentGenerationRequest request) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("You are a professional social media content creator.\n\n");
        
        prompt.append(String.format("Generate %d content variants with the following specifications:\n", 
            request.getVariantsToGenerate()));
        
        prompt.append(String.format("- Topic/Prompt: %s\n", request.getPrompt()));
        prompt.append(String.format("- Tone: %s\n", request.getTone()));
        prompt.append(String.format("- Language: %s\n", request.getLanguage()));
        prompt.append(String.format("- Content Type: %s\n", request.getContentType()));
        
        if (request.getBrandVoiceGuideId() != null) {
            BrandGuideline guide = brandGuidelineService.getGuideline(request.getBrandVoiceGuideId());
            prompt.append(String.format("\nBrand Guidelines:\n%s\n\n", guide.getGuidelineText()));
            
            if (guide.getVocabularyPreferences() != null) {
                prompt.append(String.format("Preferred Vocabulary: %s\n", 
                    String.join(", ", guide.getVocabularyPreferences())));
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
     * Parse multiple content variants from AI response
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
        
        return variants.isEmpty() ? List.of(response) : variants;
    }
    
    /**
     * Analyze sentiment using Spring AI
     */
    private Double analyzeSentiment(String content) {
        String sentimentPrompt = String.format(
            "Analyze the sentiment of this text and respond with ONLY a number between -1 and 1:\n%s",
            content
        );
        
        ChatResponse response = chatClient.call(new Prompt(sentimentPrompt));
        try {
            return Double.parseDouble(response.getResult().getOutput().getContent().trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    /**
     * Generate trending hashtags
     */
    private List<String> generateHashtags(String content, Set<SocialPlatform> platforms) {
        String hashtagPrompt = String.format(
            "Generate 10 relevant hashtags for this content (Twitter/X style, no # symbol):\n%s",
            content
        );
        
        ChatResponse response = chatClient.call(new Prompt(hashtagPrompt));
        return Arrays.stream(response.getResult().getOutput().getContent().split(","))
            .map(String::trim)
            .limit(10)
            .collect(Collectors.toList());
    }
    
    /**
     * Adapt content for specific platform constraints
     */
    private String adaptContentForPlatform(String content, SocialPlatform platform) {
        int maxLength = getMaxLengthForPlatform(platform);
        String platformPrompt = String.format(
            "Rewrite this content for %s (max %d characters), maintaining tone and message:\n%s",
            platform.name(), maxLength, content
        );
        
        ChatResponse response = chatClient.call(new Prompt(platformPrompt));
        String adapted = response.getResult().getOutput().getContent();
        
        // Ensure it doesn't exceed platform limit
        return adapted.length() > maxLength 
            ? adapted.substring(0, maxLength - 3) + "..."
            : adapted;
    }
    
    private int getMaxLengthForPlatform(SocialPlatform platform) {
        return switch (platform) {
            case TWITTER -> 280;
            case LINKEDIN -> 3000;
            case INSTAGRAM -> 2200;
            case FACEBOOK -> 63206;
            case TIKTOK -> 2500; // caption
            default -> 5000;
        };
    }
}

// ============================================================================
// 4. SERVICE LAYER - SCHEDULING & QUEUE MANAGEMENT
// ============================================================================

@Slf4j
@Service
public class SchedulingService {
    
    @Autowired
    private ScheduledJobRepository jobRepository;
    
    @Autowired
    private ContentPostRepository postRepository;
    
    @Autowired
    private AnalyticsMetricRepository metricsRepository;
    
    @Autowired
    private TaskScheduler taskScheduler;
    
    @Autowired
    private SocialMediaPublishingService publishingService;
    
    /**
     * Smart scheduling with AI-suggested optimal times
     */
    public void scheduleContent(Long postId, SchedulingRequest request) {
        ContentPost post = postRepository.findById(postId)
            .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        
        LocalDateTime scheduledTime = request.isUseAIOptimization()
            ? findOptimalPostingTime(post.getUserId(), request.getPlatforms())
            : request.getScheduledTime();
        
        post.setScheduledTime(scheduledTime);
        post.setStatus(PostStatus.SCHEDULED);
        postRepository.save(post);
        
        // Create jobs for each platform
        for (SocialPlatform platform : request.getPlatforms()) {
            ScheduledJob job = new ScheduledJob();
            job.setPostId(postId);
            job.setPlatform(platform);
            job.setScheduledTime(scheduledTime);
            job.setStatus(JobStatus.PENDING);
            job.setRetryCount(0);
            job.setCreatedAt(LocalDateTime.now());
            
            jobRepository.save(job);
            
            // Schedule the task
            scheduleJobExecution(job);
        }
        
        log.info("Post {} scheduled for {}", postId, scheduledTime);
    }
    
    /**
     * Predict optimal posting time based on historical data
     */
    public LocalDateTime findOptimalPostingTime(Long userId, Set<SocialPlatform> platforms) {
        // Fetch last 90 days of metrics
        List<ContentPost> recentPosts = postRepository.findTopPerformingLast90Days(userId);
        
        // Calculate engagement by hour
        Map<Integer, Double> engagementByHour = new HashMap<>();
        for (ContentPost post : recentPosts) {
            if (post.getPublishedTime() != null && post.getPerformanceMetrics() != null) {
                int hour = post.getPublishedTime().getHour();
                Double engagement = (Double) post.getPerformanceMetrics()
                    .getOrDefault("engagement_rate", 0.0);
                
                engagementByHour.merge(hour, engagement, Double::sum);
            }
        }
        
        // Find peak hour
        int peakHour = engagementByHour.entrySet().stream()
            .max(Comparator.comparingDouble(Map.Entry::getValue))
            .map(Map.Entry::getKey)
            .orElse(9); // Default 9 AM
        
        // Return next occurrence of peak hour
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime optimal = now.withHour(peakHour).withMinute(0).withSecond(0);
        
        if (optimal.isBefore(now)) {
            optimal = optimal.plusDays(1);
        }
        
        return optimal;
    }
    
    /**
     * Schedule job execution with Spring TaskScheduler
     */
    private void scheduleJobExecution(ScheduledJob job) {
        Instant executionTime = job.getScheduledTime().atZone(
            ZoneId.systemDefault()
        ).toInstant();
        
        taskScheduler.schedule(
            () -> executeJobWithRetry(job),
            executionTime
        );
    }
    
    /**
     * Execute scheduled job with exponential backoff retry
     */
    public void executeJobWithRetry(ScheduledJob job) {
        try {
            job.setStatus(JobStatus.PROCESSING);
            jobRepository.save(job);
            
            // Publish post
            publishingService.publishPost(job.getPostId(), job.getPlatform());
            
            job.setStatus(JobStatus.COMPLETED);
            job.setExecutedTime(LocalDateTime.now());
            
        } catch (Exception e) {
            log.error("Job execution failed: {}", job.getId(), e);
            
            if (job.getRetryCount() < 3) {
                job.setRetryCount(job.getRetryCount() + 1);
                job.setStatus(JobStatus.RETRY);
                job.setNextRetryTime(
                    LocalDateTime.now().plusSeconds((long) Math.pow(2, job.getRetryCount()) * 60)
                );
            } else {
                job.setStatus(JobStatus.FAILED);
                job.setErrorLog(e.getMessage());
                // Alert user of failure
            }
        }
        
        jobRepository.save(job);
    }
    
    /**
     * Monitor pending jobs and execute them
     */
    @Scheduled(fixedDelay = 30000) // Every 30 seconds
    public void processPendingJobs() {
        LocalDateTime now = LocalDateTime.now();
        List<ScheduledJob> pendingJobs = jobRepository.findByStatusAndScheduledTimeLessThanEqual(
            JobStatus.PENDING, now
        );
        
        for (ScheduledJob job : pendingJobs) {
            executeJobWithRetry(job);
        }
        
        log.info("Processed {} pending jobs", pendingJobs.size());
    }
}

// ============================================================================
// 5. SERVICE LAYER - ENGAGEMENT HANDLING
// ============================================================================

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
    
    /**
     * Process incoming comment/DM with sentiment analysis
     */
    public void processComment(CommentEngagement comment) {
        log.info("Processing comment from user: {}", comment.getUserHandle());
        
        // 1. Fetch full conversation history
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
            // Notify human moderator
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
     * Sentiment analysis using Spring AI Vision
     */
    private SentimentAnalysis analyzeSentiment(CommentEngagement comment) {
        String analysisPrompt = String.format(
            "Analyze this social media comment for sentiment and intent. " +
            "Respond in JSON format with: sentiment_score (-1 to 1), emotion (joy/anger/sadness/neutral), " +
            "intent (question/complaint/praise/spam), and is_spam (true/false).\n\nComment: %s",
            comment.getCommentText()
        );
        
        ChatResponse response = chatClient.call(new Prompt(analysisPrompt));
        String responseText = response.getResult().getOutput().getContent();
        
        // Parse JSON response
        return parseAnalysisResponse(responseText);
    }
    
    /**
     * Determine if comment should be escalated to human
     */
    private boolean shouldEscalate(CommentEngagement comment) {
        // Escalation rules
        if (comment.isSpam()) return false; // Auto-filter spam
        if (comment.getSentimentScore() < -0.8) return true; // Very negative
        if ("complaint".equalsIgnoreCase(comment.getIntent())) return true;
        if (comment.getEmotion().contains("anger")) return true;
        
        return false;
    }
    
    /**
     * Determine if auto-reply is appropriate
     */
    private boolean shouldAutoReply(CommentEngagement comment) {
        // Only auto-reply to questions and praise
        String intent = comment.getIntent();
        return ("question".equalsIgnoreCase(intent) || "praise".equalsIgnoreCase(intent))
            && !comment.isSpam()
            && comment.getSentimentScore() > -0.5;
    }
    
    /**
     * Generate contextual response using conversation history
     */
    private String generateContextualResponse(CommentEngagement comment) {
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
        
        ChatResponse response = chatClient.call(new Prompt(contextPrompt.toString()));
        String reply = response.getResult().getOutput().getContent();
        
        // Validate against brand guidelines
        Double brandScore = brandService.validateContentAgainstBrandGuide(reply, null);
        if (brandScore < 0.7) {
            return "Thank you for your comment! We appreciate your feedback. " +
                   "Please reach out to our team for more specific assistance.";
        }
        
        return reply;
    }
    
    /**
     * Build conversation context from history
     */
    private Map<String, Object> buildConversationContext(List<CommentEngagement> history) {
        Map<String, Object> context = new HashMap<>();
        
        StringBuilder conversationHistory = new StringBuilder();
        for (CommentEngagement comment : history.stream()
            .sorted(Comparator.comparing(CommentEngagement::getTimestamp))
            .collect(Collectors.toList())) {
            conversationHistory.append(String.format(
                "User: %s\n", comment.getCommentText()
            ));
            if (comment.getResponseText() != null) {
                conversationHistory.append(String.format(
                    "Brand: %s\n\n", comment.getResponseText()
                ));
            }
        }
        
        context.put("history", conversationHistory.toString());
        context.put("messageCount", history.size());
        context.put("lastInteractionDate", history.isEmpty() ? null : 
            history.get(history.size() - 1).getTimestamp());
        
        return context;
    }
    
    private void replyToComment(CommentEngagement comment, String response) {
        // Use platform API to post reply
        apiService.replyToComment(
            comment.getPlatform(),
            comment.getPlatformCommentId(),
            response
        );
    }
    
    private SentimentAnalysis parseAnalysisResponse(String jsonResponse) {
        // Implementation to parse JSON and create SentimentAnalysis object
        // (Using Jackson or similar)
        return new SentimentAnalysis();
    }
}

// ============================================================================
// 6. SERVICE LAYER - ANALYTICS
// ============================================================================

@Slf4j
@Service
public class AnalyticsService {
    
    @Autowired
    private AnalyticsMetricRepository metricsRepository;
    
    @Autowired
    private ContentPostRepository postRepository;
    
    @Autowired
    private SocialMediaApiService apiService;
    
    @Autowired
    private RestTemplate restTemplate; // For calling ML models
    
    /**
     * Aggregate metrics across platforms
     */
    public DashboardMetrics getDashboardMetrics(Long userId, LocalDate startDate, LocalDate endDate) {
        DashboardMetrics metrics = new DashboardMetrics();
        
        // Total reach and impressions
        List<AnalyticsMetric> metrics_list = metricsRepository.findAll();
        Double totalReach = metrics_list.stream()
            .filter(m -> m.getMetricType().equals("reach"))
            .mapToDouble(AnalyticsMetric::getValue)
            .sum();
        
        // Engagement rate
        Double totalLikes = metrics_list.stream()
            .filter(m -> m.getMetricType().equals("likes"))
            .mapToDouble(AnalyticsMetric::getValue)
            .sum();
        
        Double engagementRate = totalReach > 0 
            ? totalLikes / totalReach 
            : 0.0;
        
        // Audience growth
        Double audienceGrowth = calculateAudienceGrowth(userId, startDate, endDate);
        
        // Top performing content
        List<ContentPost> topPosts = postRepository.findTopPerformingLast90Days(userId)
            .stream().limit(5).collect(Collectors.toList());
        
        metrics.setTotalReach(totalReach.longValue());
        metrics.setEngagementRate(engagementRate);
        metrics.setAudienceGrowth(audienceGrowth);
        metrics.setTopPosts(topPosts);
        
        return metrics;
    }
    
    /**
     * Predict viral potential before posting
     */
    public ViralPrediction predictViralPotential(String content, SocialPlatform platform) {
        // Call ML model endpoint
        String mlEndpoint = "http://ml-service:8000/predict-viral";
        
        Map<String, Object> payload = new HashMap<>();
        payload.put("content", content);
        payload.put("platform", platform.name());
        
        try {
            ViralPredictionResponse mlResponse = restTemplate.postForObject(
                mlEndpoint, payload, ViralPredictionResponse.class
            );
            
            return ViralPrediction.builder()
                .viralScore(mlResponse.getScore())
                .estimatedReach(mlResponse.getEstimatedReach())
                .recommendedPostingTime(mlResponse.getOptimalTime())
                .build();
        } catch (Exception e) {
            log.error("Failed to predict viral potential", e);
            return ViralPrediction.builder().viralScore(0.5).build();
        }
    }
    
    /**
     * A/B testing framework
     */
    public void createABTest(Long userId, String contentA, String contentB, LocalDate endDate) {
        // Implementation for A/B testing
        // Split audience, track engagement separately
    }
    
    private Double calculateAudienceGrowth(Long userId, LocalDate startDate, LocalDate endDate) {
        // Calculate growth rate over period
        return 0.0;
    }
}

// ============================================================================
// 7. SERVICE LAYER - APPROVAL WORKFLOW
// ============================================================================

@Slf4j
@Service
public class ApprovalWorkflowService {
    
    @Autowired
    private ApprovalWorkflowRepository workflowRepository;
    
    @Autowired
    private ContentPostRepository postRepository;
    
    @Autowired
    private AnalyticsService analyticsService;
    
    @Autowired
    private EmailService emailService;
    
    /**
     * Calculate risk score for content
     */
    private Double calculateRiskScore(ContentPost post) {
        Double riskScore = 0.0;
        
        // High negative sentiment = higher risk
        if (post.getSentimentScore() < -0.5) {
            riskScore += 0.3;
        }
        
        // Low brand voice score = higher risk
        if (post.getBrandVoiceScore() < 0.7) {
            riskScore += 0.3;
        }
        
        // Contains potentially sensitive keywords
        if (containsSensitiveKeywords(post.getContentText())) {
            riskScore += 0.2;
        }
        
        // Will reach large audience
        ViralPrediction prediction = analyticsService.predictViralPotential(
            post.getContentText(),
            post.getPlatforms().iterator().next()
        );
        
        if (prediction.getViralScore() > 0.8) {
            riskScore += 0.2;
        }
        
        return Math.min(riskScore, 1.0);
    }
    
    /**
     * Submit for approval workflow
     */
    public void submitForApproval(Long postId) {
        ContentPost post = postRepository.findById(postId)
            .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        
        Double riskScore = calculateRiskScore(post);
        
        // Create approval workflow
        ApprovalWorkflow workflow = new ApprovalWorkflow();
        workflow.setPostId(postId);
        workflow.setStatus(WorkflowStatus.PENDING);
        workflow.setCreatedAt(LocalDateTime.now());
        workflow.setRiskScore(riskScore);
        
        // Assign to appropriate approver based on risk
        Long approverId = assignApprover(post.getUserId(), riskScore);
        workflow.setAssignedTo(approverId);
        
        workflowRepository.save(workflow);
        
        // Notify approver
        emailService.sendApprovalNotification(approverId, postId, riskScore);
        
        log.info("Post {} submitted for approval with risk score {}", postId, riskScore);
    }
    
    /**
     * Approve content
     */
    public void approveContent(Long workflowId, Long approverId) {
        ApprovalWorkflow workflow = workflowRepository.findById(workflowId)
            .orElseThrow();
        
        ContentPost post = postRepository.findById(workflow.getPostId())
            .orElseThrow();
        
        post.setApprovalStatus(ApprovalStatus.APPROVED);
        post.setApproverId(approverId);
        post.setApprovalTime(LocalDateTime.now());
        
        workflow.setStatus(WorkflowStatus.APPROVED);
        workflow.setCompletedAt(LocalDateTime.now());
        
        // Add audit log
        AuditLog log = new AuditLog();
        log.setUserId(approverId);
        log.setAction("APPROVED");
        log.setTimestamp(LocalDateTime.now());
        
        if (workflow.getAuditTrail() == null) {
            workflow.setAuditTrail(new ArrayList<>());
        }
        workflow.getAuditTrail().add(log);
        
        postRepository.save(post);
        workflowRepository.save(workflow);
    }
    
    /**
     * Request revision
     */
    public void requestRevision(Long workflowId, String feedback) {
        ApprovalWorkflow workflow = workflowRepository.findById(workflowId)
            .orElseThrow();
        
        ContentPost post = postRepository.findById(workflow.getPostId())
            .orElseThrow();
        
        post.setApprovalStatus(ApprovalStatus.REVISION_REQUESTED);
        post.setApprovalFeedback(feedback);
        
        workflow.setStatus(WorkflowStatus.REVISION_REQUESTED);
        workflow.setFeedbackText(feedback);
        
        postRepository.save(post);
        workflowRepository.save(workflow);
        
        // Notify user
        emailService.sendRevisionRequestNotification(post.getUserId(), post.getId(), feedback);
    }
    
    private Long assignApprover(Long userId, Double riskScore) {
        // Assign high-risk content to senior approvers
        // Medium-risk to regular approvers
        // Can use round-robin or load-balancing
        return 1L; // Placeholder
    }
    
    private boolean containsSensitiveKeywords(String content) {
        // Check against prohibited terms
        return false;
    }
}

// ============================================================================
// 8. CONTROLLER LAYER
// ============================================================================

package com.socialmediaagent.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/content")
public class ContentController {
    
    @Autowired
    private ContentGenerationService generationService;
    
    @Autowired
    private SchedulingService schedulingService;
    
    @PostMapping("/generate")
    public ResponseEntity<GeneratedContentResponse> generateContent(
        @RequestBody ContentGenerationRequest request) {
        GeneratedContentResponse response = generationService.generateContent(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{postId}/schedule")
    public ResponseEntity<Void> scheduleContent(
        @PathVariable Long postId,
        @RequestBody SchedulingRequest request) {
        schedulingService.scheduleContent(postId, request);
        return ResponseEntity.ok().build();
    }
}

@RestController
@RequestMapping("/api/v1/engagement")
public class EngagementController {
    
    @Autowired
    private EngagementHandlingService engagementService;
    
    @PostMapping("/comments/process")
    public ResponseEntity<Void> processComment(@RequestBody CommentEngagement comment) {
        engagementService.processComment(comment);
        return ResponseEntity.ok().build();
    }
}

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {
    
    @Autowired
    private AnalyticsService analyticsService;
    
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardMetrics> getDashboard(
        @RequestParam Long userId,
        @RequestParam LocalDate startDate,
        @RequestParam LocalDate endDate) {
        DashboardMetrics metrics = analyticsService.getDashboardMetrics(userId, startDate, endDate);
        return ResponseEntity.ok(metrics);
    }
}

// ============================================================================
// 9. CONFIGURATION & APPLICATION SETUP
// ============================================================================

package com.socialmediaagent.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableScheduling
public class SpringAiConfig {
    
    @Bean
    public ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel)
            .defaultSystem("You are an expert social media content creator and community manager. " +
                          "Create engaging, authentic content that resonates with audiences.")
            .build();
    }
    
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        scheduler.setThreadNamePrefix("scheduling-");
        scheduler.setAwaitTerminationSeconds(60);
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.initialize();
        return scheduler;
    }
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

@SpringBootApplication
public class SocialMediaAgentApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(SocialMediaAgentApplication.class, args);
    }
}
