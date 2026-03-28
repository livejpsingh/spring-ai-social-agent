package com.socialmediaagent.domain.model;

import com.socialmediaagent.domain.dto.AuditLog;
import com.socialmediaagent.domain.enums.WorkflowStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "approval_workflows", indexes = {
    @Index(name = "idx_wf_status", columnList = "status"),
    @Index(name = "idx_assigned_to", columnList = "assignedTo")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalWorkflow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long postId;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private WorkflowStatus status;

    private Long createdBy;
    private Long assignedTo;

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime dueDate;
    private LocalDateTime completedAt;

    @Column(columnDefinition = "TEXT")
    private String feedbackText;

    @Column(columnDefinition = "INT DEFAULT 0")
    private int revisionsCount;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private List<AuditLog> auditTrail;

    private Double riskScore;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) status = WorkflowStatus.PENDING;
    }
}
