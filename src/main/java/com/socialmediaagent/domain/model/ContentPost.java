package com.socialmediaagent.domain.model;

import com.socialmediaagent.domain.enums.ApprovalStatus;
import com.socialmediaagent.domain.enums.PostStatus;
import com.socialmediaagent.domain.enums.Tone;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "content_posts", indexes = {
    @Index(name = "idx_user_status", columnList = "userId, status"),
    @Index(name = "idx_scheduled_time", columnList = "scheduledTime"),
    @Index(name = "idx_approval_status", columnList = "approvalStatus")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(columnDefinition = "TEXT")
    private String contentText;

    @ElementCollection
    @CollectionTable(name = "content_post_media_urls", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "media_url")
    private List<String> mediaUrls;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private PostStatus status;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private Tone tone;

    @Column(length = 10)
    private String language;

    private LocalDateTime scheduledTime;
    private LocalDateTime publishedTime;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, String> platformPostIds;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private ApprovalStatus approvalStatus;

    private Long approverId;
    private LocalDateTime approvalTime;

    @Column(columnDefinition = "TEXT")
    private String approvalFeedback;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Double> performanceMetrics;

    private Double sentimentScore;
    private Double brandVoiceScore;

    @ElementCollection
    @CollectionTable(name = "content_post_tags", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "tag")
    private Set<String> tags;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> revisionHistory;

    @Column(length = 64)
    private String contentFingerprint;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) status = PostStatus.DRAFT;
        if (approvalStatus == null) approvalStatus = ApprovalStatus.PENDING;
        if (language == null) language = "en";
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
