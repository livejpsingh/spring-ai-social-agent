package com.socialmediaagent.repository;

import com.socialmediaagent.domain.enums.PostStatus;
import com.socialmediaagent.domain.model.ContentPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ContentPostRepository extends JpaRepository<ContentPost, Long> {

    List<ContentPost> findByUserIdAndStatus(Long userId, PostStatus status);

    @Query("SELECT cp FROM ContentPost cp WHERE cp.userId = :userId " +
           "AND cp.scheduledTime BETWEEN :start AND :end " +
           "ORDER BY cp.scheduledTime ASC")
    List<ContentPost> findScheduledBetween(
        @Param("userId") Long userId,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

    @Query("SELECT cp FROM ContentPost cp WHERE cp.approvalStatus = 'PENDING' " +
           "AND cp.userId = :userId ORDER BY cp.createdAt ASC")
    List<ContentPost> findPendingApproval(@Param("userId") Long userId);

    @Query(value = "SELECT cp.* FROM content_posts cp " +
           "WHERE cp.user_id = :userId AND cp.published_time > now() - interval '90 days' " +
           "ORDER BY cp.sentiment_score DESC NULLS LAST", nativeQuery = true)
    List<ContentPost> findTopPerformingLast90Days(@Param("userId") Long userId);

    Long countByUserIdAndStatus(Long userId, PostStatus status);
}
