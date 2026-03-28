package com.socialmediaagent.controller;

import com.socialmediaagent.domain.model.ApprovalWorkflow;
import com.socialmediaagent.service.ApprovalWorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/approval")
public class ApprovalController {

    @Autowired
    private ApprovalWorkflowService approvalService;

    /**
     * Get pending approvals for an approver.
     */
    @GetMapping("/pending/{approverId}")
    public ResponseEntity<List<ApprovalWorkflow>> getPendingApprovals(
        @PathVariable Long approverId) {
        List<ApprovalWorkflow> workflows = approvalService.getPendingApprovals(approverId);
        return ResponseEntity.ok(workflows);
    }

    /**
     * Approve a content workflow.
     */
    @PostMapping("/{workflowId}/approve")
    public ResponseEntity<Void> approve(
        @PathVariable Long workflowId,
        @RequestParam Long approverId) {
        approvalService.approveContent(workflowId, approverId);
        return ResponseEntity.ok().build();
    }

    /**
     * Reject a content workflow.
     */
    @PostMapping("/{workflowId}/reject")
    public ResponseEntity<Void> reject(
        @PathVariable Long workflowId,
        @RequestParam Long approverId,
        @RequestBody Map<String, String> body) {
        approvalService.rejectContent(workflowId, approverId, body.get("reason"));
        return ResponseEntity.ok().build();
    }

    /**
     * Request revision for a content workflow.
     */
    @PostMapping("/{workflowId}/revision")
    public ResponseEntity<Void> requestRevision(
        @PathVariable Long workflowId,
        @RequestBody Map<String, String> body) {
        approvalService.requestRevision(workflowId, body.get("feedback"));
        return ResponseEntity.ok().build();
    }
}
