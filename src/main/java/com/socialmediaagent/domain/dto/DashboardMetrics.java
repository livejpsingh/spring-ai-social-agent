package com.socialmediaagent.domain.dto;

import com.socialmediaagent.domain.model.ContentPost;
import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardMetrics {

    private Long totalReach;
    private Double engagementRate;
    private Double audienceGrowth;
    private Long totalPosts;
    private Long scheduledPosts;
    private Long pendingApprovals;
    private Map<String, Double> platformBreakdown;
    private List<ContentPost> topPosts;
    private Map<String, List<Double>> trendsData;
}
