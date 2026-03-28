package com.socialmediaagent.domain.model;

import com.socialmediaagent.domain.enums.SocialPlatform;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "analytics_metrics", indexes = {
    @Index(name = "idx_post_metric", columnList = "postId, metricType"),
    @Index(name = "idx_timestamp", columnList = "timestamp")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long postId;
    private Long accountId;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private SocialPlatform platform;

    @Column(length = 100)
    private String metricType;

    private LocalDateTime timestamp;
    private Double value;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> breakdownBySegment;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> platformSpecificData;

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) timestamp = LocalDateTime.now();
    }
}
