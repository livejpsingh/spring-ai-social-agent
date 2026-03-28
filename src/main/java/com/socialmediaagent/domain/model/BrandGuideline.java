package com.socialmediaagent.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Entity
@Table(name = "brand_guidelines")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BrandGuideline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    @Column(columnDefinition = "TEXT")
    private String guidelineText;

    @ElementCollection
    @CollectionTable(name = "brand_tone_samples", joinColumns = @JoinColumn(name = "guideline_id"))
    @Column(name = "tone_sample", columnDefinition = "TEXT")
    private List<String> toneSamples;

    @ElementCollection
    @CollectionTable(name = "brand_vocabulary", joinColumns = @JoinColumn(name = "guideline_id"))
    @Column(name = "vocabulary_term")
    private Set<String> vocabularyPreferences;

    @ElementCollection
    @CollectionTable(name = "brand_prohibited_terms", joinColumns = @JoinColumn(name = "guideline_id"))
    @Column(name = "prohibited_term")
    private Set<String> prohibitedTerms;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> visualGuidelines;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> complianceRules;

    @Column(columnDefinition = "INT DEFAULT 1")
    private int version;

    private LocalDateTime lastUpdated;

    @PrePersist
    protected void onCreate() {
        lastUpdated = LocalDateTime.now();
        if (version == 0) version = 1;
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }
}
