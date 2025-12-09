package com.studium.studium_academico.business.dto.request;

import com.studium.studium_academico.infrastructure.entity.TypeGrade;

import java.math.BigDecimal;
import java.util.UUID;

public record GradeRequestDTO(
        BigDecimal grade,
        TypeGrade typeGrade,
        UUID registrationId,
        UUID teacherId,
        UUID disciplineId,
        UUID classId
) {
}
