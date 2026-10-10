package com.skillbridge.controller;

import com.skillbridge.dto.DashboardStatsDto;
import com.skillbridge.model.Role;
import com.skillbridge.model.User;
import com.skillbridge.service.AnalyticsService;
import com.skillbridge.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final AuthService authService;

    public AnalyticsController(AnalyticsService analyticsService, AuthService authService) {
        this.analyticsService = analyticsService;
        this.authService = authService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardStatsDto> getDashboardStats() {
        User currentUser = authService.getCurrentAuthenticatedUser();
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }

        if (currentUser.getRole() == Role.ROLE_EMPLOYER) {
            return ResponseEntity.ok(analyticsService.getEmployerStats(currentUser));
        } else {
            return ResponseEntity.ok(analyticsService.getStudentStats(currentUser));
        }
    }
}
