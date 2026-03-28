package com.socialmediaagent.repository;

import com.socialmediaagent.domain.model.AnalyticsMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnalyticsMetricRepository extends JpaRepository<AnalyticsMetric, Long> {

    @Query("SELECT am FROM AnalyticsMetric am WHERE am.postId = :postId " +
           "ORDER BY am.timestamp DESC")
    List<AnalyticsMetric> findByPostIdOrdered(@Param("postId") Long postId);

    @Query(value = "SELECT DATE(timestamp) as date, AVG(value) as avg_value " +
           "FROM analytics_metrics WHERE account_id = :accountId " +
           "AND metric_type = :metricType " +
           "AND timestamp > now() - interval '30 days' " +
           "GROUP BY DATE(timestamp) ORDER BY date", nativeQuery = true)
    List<Object[]> getDailyMetricTrend(
        @Param("accountId") Long accountId,
        @Param("metricType") String metricType
    );

    List<AnalyticsMetric> findByAccountIdAndMetricType(Long accountId, String metricType);
}
