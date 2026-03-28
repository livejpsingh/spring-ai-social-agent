package com.socialmediaagent.repository;

import com.socialmediaagent.domain.enums.JobStatus;
import com.socialmediaagent.domain.enums.SocialPlatform;
import com.socialmediaagent.domain.model.ScheduledJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduledJobRepository extends JpaRepository<ScheduledJob, Long> {

    List<ScheduledJob> findByStatusAndScheduledTimeLessThanEqual(
        JobStatus status, LocalDateTime now
    );

    List<ScheduledJob> findByPostId(Long postId);

    @Query("SELECT COUNT(sj) FROM ScheduledJob sj " +
           "WHERE sj.platform = :platform AND sj.status = 'PROCESSING' " +
           "AND sj.scheduledTime > :windowStart AND sj.scheduledTime < :windowEnd")
    int countJobsInWindow(
        @Param("platform") SocialPlatform platform,
        @Param("windowStart") LocalDateTime windowStart,
        @Param("windowEnd") LocalDateTime windowEnd
    );

    List<ScheduledJob> findByStatusIn(List<JobStatus> statuses);
}
