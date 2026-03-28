package com.socialmediaagent.service;

import com.socialmediaagent.domain.dto.SchedulingRequest;
import com.socialmediaagent.domain.enums.JobStatus;
import com.socialmediaagent.domain.enums.PostStatus;
import com.socialmediaagent.domain.enums.SocialPlatform;
import com.socialmediaagent.domain.model.ContentPost;
import com.socialmediaagent.domain.model.ScheduledJob;
import com.socialmediaagent.exception.ResourceNotFoundException;
import com.socialmediaagent.repository.ContentPostRepository;
import com.socialmediaagent.repository.ScheduledJobRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SchedulingService {

    @Autowired
    private ScheduledJobRepository jobRepository;

    @Autowired
    private ContentPostRepository postRepository;

    @Autowired
    private TaskScheduler taskScheduler;

    @Autowired
    private SocialMediaPublishingService publishingService;

    /**
     * Smart scheduling with AI-suggested optimal times.
     * Creates a ScheduledJob per platform and registers each with the TaskScheduler.
     */
    @Transactional
    public void scheduleContent(Long postId, SchedulingRequest request) {
        ContentPost post = postRepository.findById(postId)
            .orElseThrow(() -> new ResourceNotFoundException("Post not found: " + postId));

        LocalDateTime scheduledTime = request.isUseAIOptimization()
            ? findOptimalPostingTime(post.getUserId(), request.getPlatforms())
            : request.getScheduledTime();

        post.setScheduledTime(scheduledTime);
        post.setStatus(PostStatus.SCHEDULED);
        postRepository.save(post);

        // Create a job for each target platform
        for (SocialPlatform platform : request.getPlatforms()) {
            ScheduledJob job = ScheduledJob.builder()
                .postId(postId)
                .platform(platform)
                .scheduledTime(scheduledTime)
                .status(JobStatus.PENDING)
                .retryCount(0)
                .build();

            jobRepository.save(job);

            // Schedule the task with Spring TaskScheduler
            scheduleJobExecution(job);
        }

        log.info("Post {} scheduled for {} across {} platforms",
            postId, scheduledTime, request.getPlatforms().size());
    }

    /**
     * Predict optimal posting time based on historical engagement data.
     * Analyzes the last 90 days of performance to find peak engagement hours.
     */
    public LocalDateTime findOptimalPostingTime(Long userId, Set<SocialPlatform> platforms) {
        List<ContentPost> recentPosts = postRepository.findTopPerformingLast90Days(userId);

        // Calculate engagement by hour of day
        Map<Integer, Double> engagementByHour = new HashMap<>();
        for (ContentPost post : recentPosts) {
            if (post.getPublishedTime() != null && post.getPerformanceMetrics() != null) {
                int hour = post.getPublishedTime().getHour();
                Double engagement = post.getPerformanceMetrics()
                    .getOrDefault("engagement_rate", 0.0);
                engagementByHour.merge(hour, engagement, Double::sum);
            }
        }

        // Find the peak engagement hour
        int peakHour = engagementByHour.entrySet().stream()
            .max(Comparator.comparingDouble(Map.Entry::getValue))
            .map(Map.Entry::getKey)
            .orElse(9); // Default to 9 AM if no data

        // Return next occurrence of peak hour
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime optimal = now.withHour(peakHour).withMinute(0).withSecond(0).withNano(0);

        if (optimal.isBefore(now)) {
            optimal = optimal.plusDays(1);
        }

        return optimal;
    }

    /**
     * Schedule a job for execution at its scheduled time.
     */
    private void scheduleJobExecution(ScheduledJob job) {
        Instant executionTime = job.getScheduledTime()
            .atZone(ZoneId.systemDefault())
            .toInstant();

        taskScheduler.schedule(
            () -> executeJobWithRetry(job),
            executionTime
        );
    }

    /**
     * Execute a scheduled job with exponential backoff retry (max 3 retries).
     */
    @Transactional
    public void executeJobWithRetry(ScheduledJob job) {
        try {
            job.setStatus(JobStatus.PROCESSING);
            jobRepository.save(job);

            // Publish the post to the platform
            publishingService.publishPost(job.getPostId(), job.getPlatform());

            job.setStatus(JobStatus.COMPLETED);
            job.setExecutedTime(LocalDateTime.now());

        } catch (Exception e) {
            log.error("Job execution failed for job {}: {}", job.getId(), e.getMessage(), e);

            if (job.getRetryCount() < 3) {
                job.setRetryCount(job.getRetryCount() + 1);
                job.setStatus(JobStatus.RETRY);
                job.setLastRetryTime(LocalDateTime.now());
                // Exponential backoff: 2^retryCount * 60 seconds
                job.setNextRetryTime(
                    LocalDateTime.now().plusSeconds((long) Math.pow(2, job.getRetryCount()) * 60)
                );
            } else {
                job.setStatus(JobStatus.FAILED);
                job.setErrorLog(e.getMessage());
            }
        }

        jobRepository.save(job);
    }

    /**
     * Periodic job processor: picks up pending jobs that are past their scheduled time,
     * and retries jobs that are past their next retry time.
     * Runs every 30 seconds.
     */
    @Scheduled(fixedDelay = 30000)
    @Transactional
    public void processPendingJobs() {
        LocalDateTime now = LocalDateTime.now();

        // Process PENDING jobs
        List<ScheduledJob> pendingJobs = jobRepository.findByStatusAndScheduledTimeLessThanEqual(
            JobStatus.PENDING, now
        );

        for (ScheduledJob job : pendingJobs) {
            executeJobWithRetry(job);
        }

        // Process RETRY jobs
        List<ScheduledJob> retryJobs = jobRepository.findByStatusIn(List.of(JobStatus.RETRY));
        for (ScheduledJob job : retryJobs) {
            if (job.getNextRetryTime() != null && job.getNextRetryTime().isBefore(now)) {
                executeJobWithRetry(job);
            }
        }

        if (!pendingJobs.isEmpty() || !retryJobs.isEmpty()) {
            log.info("Processed {} pending and {} retry jobs",
                pendingJobs.size(), retryJobs.size());
        }
    }
}
