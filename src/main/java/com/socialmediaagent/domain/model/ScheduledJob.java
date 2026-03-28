package com.socialmediaagent.domain.model;

import com.socialmediaagent.domain.enums.JobStatus;
import com.socialmediaagent.domain.enums.SocialPlatform;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "scheduled_jobs", indexes = {
    @Index(name = "idx_status_time", columnList = "status, scheduledTime"),
    @Index(name = "idx_platform", columnList = "platform")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduledJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long postId;

    @Enumerated(EnumType.STRING)
    @Column(length = 50, nullable = false)
    private SocialPlatform platform;

    @Column(nullable = false)
    private LocalDateTime scheduledTime;

    private LocalDateTime executedTime;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private JobStatus status;

    @Column(columnDefinition = "INT DEFAULT 0")
    private int retryCount;

    private LocalDateTime lastRetryTime;
    private LocalDateTime nextRetryTime;

    @Column(columnDefinition = "TEXT")
    private String errorLog;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = JobStatus.PENDING;
    }
}
