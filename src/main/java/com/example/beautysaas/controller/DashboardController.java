package com.example.beautysaas.controller;

import com.example.beautysaas.dto.dashboard.DashboardAnalyticsDto;
import com.example.beautysaas.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.UUID;

@RestController
@RequestMapping
@Tag(name = "Dashboard & Analytics", description = "APIs for retrieving dashboard analytics")
@Slf4j
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @Operation(summary = "Parlour Admin Dashboard Analytics", description = "Retrieves analytics data for a specific parlour, accessible by its Admin.")
    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DashboardAnalyticsDto> getAdminDashboardAnalytics(
            Principal principal,
            @Parameter(description = "ID of the parlour to retrieve analytics for", required = true) @RequestParam UUID parlourId)  {
        log.info("Admin {} fetching dashboard analytics for parlour {}.", principal.getName(), parlourId);
        DashboardAnalyticsDto analytics = dashboardService.getAdminDashboardAnalytics(principal.getName(), parlourId);
        return ResponseEntity.ok(analytics);
    }

    @Operation(summary = "SuperAdmin Dashboard Analytics", description = "Retrieves platform-wide analytics data, accessible by SuperAdmin.")
    @GetMapping("/superadmin/dashboard")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<DashboardAnalyticsDto> getSuperAdminDashboardAnalytics(Principal principal) {
        log.info("SuperAdmin {} fetching platform-wide dashboard analytics.", principal.getName());
        DashboardAnalyticsDto analytics = dashboardService.getSuperAdminDashboardAnalytics();
        return ResponseEntity.ok(analytics);
    }
}
