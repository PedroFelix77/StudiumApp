package com.studium.studium_academico.business.dto.response.dashboard;

import java.time.LocalDateTime;
import java.util.UUID;

public record DashboardActivityDTO(
        UUID id,
        String text,
        LocalDateTime createdAt
) {
}
