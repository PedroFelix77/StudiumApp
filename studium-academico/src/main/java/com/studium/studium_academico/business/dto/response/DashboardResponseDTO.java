package com.studium.studium_academico.business.dto.response;

import com.studium.studium_academico.business.dto.response.dashboard.DashboardActivityDTO;
import com.studium.studium_academico.business.dto.response.dashboard.DashboardCoursePerformanceDTO;
import com.studium.studium_academico.business.dto.response.dashboard.DashboardStatDTO;

import java.util.List;

public record DashboardResponseDTO(
        List<DashboardStatDTO> stats,
        List<DashboardCoursePerformanceDTO> coursePerformance,
        List<DashboardActivityDTO> recentActivities
) {
}
