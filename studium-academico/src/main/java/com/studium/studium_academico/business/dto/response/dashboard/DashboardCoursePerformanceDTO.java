package com.studium.studium_academico.business.dto.response.dashboard;

import java.util.UUID;

public record DashboardCoursePerformanceDTO(
        UUID courseId,
        String courseName,
        Long studentCount,
        Double averageGrade
) {
}
