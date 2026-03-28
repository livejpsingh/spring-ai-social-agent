package com.socialmediaagent.domain.model;

import com.socialmediaagent.domain.enums.EngagementStatus;
import com.socialmediaagent.domain.enums.SocialPlatform;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "comments_engagement", indexes = {
    @Index(name = "idx_post_status", columnList = "postId, status"),
    @Index(name = "idx_platform_user", columnList = "platformUserId, platform")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentEngagement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255)
    private String platformCommentId;

    @Column(length = 255)
    private String platformUserId;

    @Column(length = 255)
    private String userHandle;

    private Long postId;

    @Column(columnDefinition = "TEXT")
    private String commentText;

    private LocalDateTime timestamp;

    private Double sentimentScore;

    @Column(length = 50)
    private String emotion;

    @Column(length = 50)
    private String intent;

    @Column(length = 10)
    private String language;

    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isSpam;

    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean flagged;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private SocialPlatform platform;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private EngagementStatus status;

    private Long assignedUserId;

    @Column(columnDefinition = "TEXT")
    private String responseText;

    private LocalDateTime responseTime;

    @Column(length = 255)
    private String autoReplyId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> conversationContext;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = EngagementStatus.PENDING;
        if (isSpam == null) isSpam = false;
        if (flagged == null) flagged = false;
    }
}
