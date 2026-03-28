package com.socialmediaagent.controller;

import com.socialmediaagent.domain.dto.DashboardMetrics;
import com.socialmediaagent.domain.dto.ViralPrediction;
import com.socialmediaagent.domain.enums.SocialPlatform;
import com.socialmediaagent.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    /**
     * Get aggregated dashboard metrics across all platforms.
     */
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardMetrics> getDashboard(
        @RequestParam Long userId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        DashboardMetrics metrics = analyticsService.getDashboardMetrics(userId, startDate, endDate);
        return ResponseEntity.ok(metrics);
    }

    /**
     * Predict viral potential of content before posting.
     */
    @PostMapping("/predict-viral")
    public ResponseEntity<ViralPrediction> predictViral(
        @RequestParam String content,
        @RequestParam SocialPlatform platform) {
        ViralPrediction prediction = analyticsService.predictViralPotential(content, platform);
        return ResponseEntity.ok(prediction);
    }
}
