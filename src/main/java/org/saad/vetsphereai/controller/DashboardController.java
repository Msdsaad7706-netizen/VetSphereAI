package org.saad.vetsphereai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.saad.vetsphereai.dto.DashboardStatsResponse;
import org.saad.vetsphereai.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Dashboard", description = "Dashboard KPIs and Clinic Analytics endpoints")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/stats")
    @Operation(summary = "Get clinic analytics and KPIs", description = "Retrieves aggregated counts for pets, appointments, vaccinations, prescriptions, and status distributions")
    public DashboardStatsResponse getDashboardStats() {
        return dashboardService.getDashboardStats();
    }
}
