package com.studium.studium_academico.business.dto.response;

import com.studium.studium_academico.infrastructure.entity.StudentStatus;

import java.math.BigDecimal;

public record AverageResult(
        BigDecimal initialAverage,
        BigDecimal finalAverage,
        StudentStatus status,
        String message
) {
}
