package com.socialmediaagent.controller;

import com.socialmediaagent.domain.dto.ContentGenerationRequest;
import com.socialmediaagent.domain.dto.GeneratedContentResponse;
import com.socialmediaagent.domain.dto.SchedulingRequest;
import com.socialmediaagent.domain.enums.PostStatus;
import com.socialmediaagent.domain.model.ContentPost;
import com.socialmediaagent.repository.ContentPostRepository;
import com.socialmediaagent.service.ApprovalWorkflowService;
import com.socialmediaagent.service.ContentGenerationService;
import com.socialmediaagent.service.SchedulingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/content")
public class ContentController {

    @Autowired
    private ContentGenerationService generationService;

    @Autowired
    private SchedulingService schedulingService;

    @Autowired
    private ContentPostRepository postRepository;

    @Autowired
    private ApprovalWorkflowService approvalService;

    /**
     * Generate AI-powered content variants.
     */
    @PostMapping("/generate")
    public ResponseEntity<GeneratedContentResponse> generateContent(
        @Valid @RequestBody ContentGenerationRequest request) {
        GeneratedContentResponse response = generationService.generateContent(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Save a content post as draft.
     */
    @PostMapping("/draft")
    public ResponseEntity<ContentPost> saveDraft(@RequestBody ContentPost post) {
        post.setStatus(PostStatus.DRAFT);
        ContentPost saved = postRepository.save(post);
        return ResponseEntity.ok(saved);
    }

    /**
     * Schedule a content post for publishing.
     */
    @PostMapping("/{postId}/schedule")
    public ResponseEntity<Void> scheduleContent(
        @PathVariable Long postId,
        @RequestBody SchedulingRequest request) {
        schedulingService.scheduleContent(postId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * Submit a content post for approval.
     */
    @PostMapping("/{postId}/submit-for-approval")
    public ResponseEntity<Void> submitForApproval(@PathVariable Long postId) {
        approvalService.submitForApproval(postId);
        return ResponseEntity.ok().build();
    }

    /**
     * Get all posts by user and status.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ContentPost>> getPostsByUser(
        @PathVariable Long userId,
        @RequestParam(required = false) PostStatus status) {
        List<ContentPost> posts;
        if (status != null) {
            posts = postRepository.findByUserIdAndStatus(userId, status);
        } else {
            posts = postRepository.findByUserIdAndStatus(userId, PostStatus.DRAFT);
        }
        return ResponseEntity.ok(posts);
    }

    /**
     * Get a single post by ID.
     */
    @GetMapping("/{postId}")
    public ResponseEntity<ContentPost> getPost(@PathVariable Long postId) {
        return postRepository.findById(postId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete a post.
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        postRepository.deleteById(postId);
        return ResponseEntity.noContent().build();
    }
}
