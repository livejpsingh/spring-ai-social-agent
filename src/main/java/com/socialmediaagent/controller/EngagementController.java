package com.socialmediaagent.controller;

import com.socialmediaagent.domain.enums.EngagementStatus;
import com.socialmediaagent.domain.model.CommentEngagement;
import com.socialmediaagent.repository.CommentEngagementRepository;
import com.socialmediaagent.service.EngagementHandlingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/engagement")
public class EngagementController {

    @Autowired
    private EngagementHandlingService engagementService;

    @Autowired
    private CommentEngagementRepository commentRepository;

    /**
     * Process an incoming comment through the AI engagement pipeline.
     */
    @PostMapping("/comments/process")
    public ResponseEntity<Void> processComment(@RequestBody CommentEngagement comment) {
        engagementService.processComment(comment);
        return ResponseEntity.ok().build();
    }

    /**
     * Get comments pending review for a user.
     */
    @GetMapping("/comments/pending/{userId}")
    public ResponseEntity<List<CommentEngagement>> getPendingComments(@PathVariable Long userId) {
        List<CommentEngagement> comments = commentRepository.findPendingReview(userId);
        return ResponseEntity.ok(comments);
    }

    /**
     * Get comments by post and status.
     */
    @GetMapping("/comments/post/{postId}")
    public ResponseEntity<List<CommentEngagement>> getCommentsByPost(
        @PathVariable Long postId,
        @RequestParam(required = false) EngagementStatus status) {
        List<CommentEngagement> comments;
        if (status != null) {
            comments = commentRepository.findByPostIdAndStatus(postId, status);
        } else {
            comments = commentRepository.findByPostIdAndStatus(postId, EngagementStatus.PENDING);
        }
        return ResponseEntity.ok(comments);
    }

    /**
     * Manually reply to a comment.
     */
    @PostMapping("/comments/{commentId}/reply")
    public ResponseEntity<CommentEngagement> replyToComment(
        @PathVariable Long commentId,
        @RequestBody String responseText) {
        CommentEngagement comment = commentRepository.findById(commentId)
            .orElseThrow(() -> new RuntimeException("Comment not found: " + commentId));

        comment.setResponseText(responseText);
        comment.setStatus(EngagementStatus.HUMAN_REVIEWED);
        commentRepository.save(comment);

        return ResponseEntity.ok(comment);
    }
}
