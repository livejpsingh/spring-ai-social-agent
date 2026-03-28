package com.socialmediaagent.service;

import com.socialmediaagent.domain.dto.AuditLog;
import com.socialmediaagent.domain.dto.ViralPrediction;
import com.socialmediaagent.domain.enums.ApprovalStatus;
import com.socialmediaagent.domain.enums.WorkflowStatus;
import com.socialmediaagent.domain.model.ApprovalWorkflow;
import com.socialmediaagent.domain.model.ContentPost;
import com.socialmediaagent.exception.ResourceNotFoundException;
import com.socialmediaagent.repository.ApprovalWorkflowRepository;
import com.socialmediaagent.repository.ContentPostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
     * Submit a content post for human approval.
     * Calculates a risk score and assigns an appropriate approver.
     */
    @Transactional
    public void submitForApproval(Long postId) {
        ContentPost post = postRepository.findById(postId)
            .orElseThrow(() -> new ResourceNotFoundException("Post not found: " + postId));

        Double riskScore = calculateRiskScore(post);

        ApprovalWorkflow workflow = ApprovalWorkflow.builder()
            .postId(postId)
            .status(WorkflowStatus.PENDING)
            .riskScore(riskScore)
            .createdBy(post.getUserId())
            .build();

        // Assign to appropriate approver based on risk
        Long approverId = assignApprover(post.getUserId(), riskScore);
        workflow.setAssignedTo(approverId);

        workflowRepository.save(workflow);

        // Notify the approver
        emailService.sendApprovalNotification(approverId, postId, riskScore);

        log.info("Post {} submitted for approval with risk score {}", postId, riskScore);
    }

    /**
     * Approve content and move it to the scheduling queue.
     */
    @Transactional
    public void approveContent(Long workflowId, Long approverId) {
        ApprovalWorkflow workflow = workflowRepository.findById(workflowId)
            .orElseThrow(() -> new ResourceNotFoundException("Workflow not found: " + workflowId));

        ContentPost post = postRepository.findById(workflow.getPostId())
            .orElseThrow(() -> new ResourceNotFoundException("Post not found: " + workflow.getPostId()));

        post.setApprovalStatus(ApprovalStatus.APPROVED);
        post.setApproverId(approverId);
        post.setApprovalTime(LocalDateTime.now());

        workflow.setStatus(WorkflowStatus.APPROVED);
        workflow.setCompletedAt(LocalDateTime.now());

        // Add audit log entry
        addAuditEntry(workflow, approverId, "APPROVED", "Content approved for publishing");

        postRepository.save(post);
        workflowRepository.save(workflow);

        log.info("Post {} approved by user {}", workflow.getPostId(), approverId);
    }

    /**
     * Reject content with feedback.
     */
    @Transactional
    public void rejectContent(Long workflowId, Long approverId, String reason) {
        ApprovalWorkflow workflow = workflowRepository.findById(workflowId)
            .orElseThrow(() -> new ResourceNotFoundException("Workflow not found: " + workflowId));

        ContentPost post = postRepository.findById(workflow.getPostId())
            .orElseThrow(() -> new ResourceNotFoundException("Post not found: " + workflow.getPostId()));

        post.setApprovalStatus(ApprovalStatus.REJECTED);
        post.setApprovalFeedback(reason);

        workflow.setStatus(WorkflowStatus.REJECTED);
        workflow.setFeedbackText(reason);
        workflow.setCompletedAt(LocalDateTime.now());

        addAuditEntry(workflow, approverId, "REJECTED", reason);

        postRepository.save(post);
        workflowRepository.save(workflow);

        log.info("Post {} rejected by user {}: {}", workflow.getPostId(), approverId, reason);
    }

    /**
     * Request revision with feedback for the content creator.
     */
    @Transactional
    public void requestRevision(Long workflowId, String feedback) {
        ApprovalWorkflow workflow = workflowRepository.findById(workflowId)
            .orElseThrow(() -> new ResourceNotFoundException("Workflow not found: " + workflowId));

        ContentPost post = postRepository.findById(workflow.getPostId())
            .orElseThrow(() -> new ResourceNotFoundException("Post not found: " + workflow.getPostId()));

        post.setApprovalStatus(ApprovalStatus.REVISION_REQUESTED);
        post.setApprovalFeedback(feedback);

        workflow.setStatus(WorkflowStatus.REVISION_REQUESTED);
        workflow.setFeedbackText(feedback);
        workflow.setRevisionsCount(workflow.getRevisionsCount() + 1);

        addAuditEntry(workflow, workflow.getAssignedTo(), "REVISION_REQUESTED", feedback);

        postRepository.save(post);
        workflowRepository.save(workflow);

        // Notify the content creator
        emailService.sendRevisionRequestNotification(post.getUserId(), post.getId(), feedback);

        log.info("Revision requested for post {}: {}", workflow.getPostId(), feedback);
    }

    /**
     * Get all pending approvals for an approver.
     */
    public List<ApprovalWorkflow> getPendingApprovals(Long approverId) {
        return workflowRepository.findByAssignedToAndStatus(approverId, WorkflowStatus.PENDING);
    }

    /**
     * Calculate risk score based on sentiment, brand voice score, and viral potential.
     */
    private Double calculateRiskScore(ContentPost post) {
        double riskScore = 0.0;

        // High negative sentiment increases risk
        if (post.getSentimentScore() != null && post.getSentimentScore() < -0.5) {
            riskScore += 0.3;
        }

        // Low brand voice score increases risk
        if (post.getBrandVoiceScore() != null && post.getBrandVoiceScore() < 0.7) {
            riskScore += 0.3;
        }

        // Sensitive keywords check
        if (containsSensitiveKeywords(post.getContentText())) {
            riskScore += 0.2;
        }

        // High viral potential = more eyeballs = higher risk
        try {
            ViralPrediction prediction = analyticsService.predictViralPotential(
                post.getContentText(),
                com.socialmediaagent.domain.enums.SocialPlatform.TWITTER // Default
            );
            if (prediction.getViralScore() > 0.8) {
                riskScore += 0.2;
            }
        } catch (Exception e) {
            log.warn("Could not assess viral risk: {}", e.getMessage());
        }

        return Math.min(riskScore, 1.0);
    }

    private Long assignApprover(Long userId, Double riskScore) {
        // High-risk content → senior approvers, medium → regular
        // Placeholder: return a default approver ID
        return 1L;
    }

    private boolean containsSensitiveKeywords(String content) {
        if (content == null) return false;
        String lower = content.toLowerCase();
        List<String> sensitiveTerms = List.of(
            "lawsuit", "controversy", "scandal", "legal", "complaint",
            "refund", "crisis", "urgent", "apology"
        );
        return sensitiveTerms.stream().anyMatch(lower::contains);
    }

    private void addAuditEntry(ApprovalWorkflow workflow, Long userId, String action, String details) {
        if (workflow.getAuditTrail() == null) {
            workflow.setAuditTrail(new ArrayList<>());
        }
        workflow.getAuditTrail().add(AuditLog.builder()
            .userId(userId)
            .action(action)
            .details(details)
            .timestamp(LocalDateTime.now())
            .build());
    }
}
