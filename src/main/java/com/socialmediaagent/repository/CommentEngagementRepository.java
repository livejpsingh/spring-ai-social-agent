package com.socialmediaagent.repository;

import com.socialmediaagent.domain.enums.EngagementStatus;
import com.socialmediaagent.domain.enums.SocialPlatform;
import com.socialmediaagent.domain.model.CommentEngagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentEngagementRepository extends JpaRepository<CommentEngagement, Long> {

    List<CommentEngagement> findByPostIdAndStatus(Long postId, EngagementStatus status);

    @Query("SELECT ce FROM CommentEngagement ce WHERE ce.assignedUserId = :userId " +
           "AND ce.status IN ('PENDING', 'ESCALATED') " +
           "ORDER BY ce.timestamp DESC")
    List<CommentEngagement> findPendingReview(@Param("userId") Long userId);

    List<CommentEngagement> findByPlatformUserIdAndPlatform(
        String platformUserId, SocialPlatform platform
    );

    Long countByPostIdAndStatus(Long postId, EngagementStatus status);
}
