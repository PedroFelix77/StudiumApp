package com.studium.studium_academico.controller;

import com.studium.studium_academico.business.dto.response.DashboardResponseDTO;
import com.studium.studium_academico.business.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/admin")
    public ResponseEntity<DashboardResponseDTO> admin() {
        return ResponseEntity.ok(dashboardService.getAdminDashboard());
    }

    @GetMapping("/director")
    public ResponseEntity<DashboardResponseDTO> director() {
        return ResponseEntity.ok(dashboardService.getDirectorDashboard());
    }

    @GetMapping("/teacher/{id}")
    public ResponseEntity<DashboardResponseDTO> teacher(@PathVariable UUID id) {
        return ResponseEntity.ok(dashboardService.getTeacherDashboard(id));
    }

    @GetMapping("/student/{id}")
    public ResponseEntity<DashboardResponseDTO> student(@PathVariable UUID id) {
        return ResponseEntity.ok(dashboardService.getStudentDashboard(id));
    }
}
