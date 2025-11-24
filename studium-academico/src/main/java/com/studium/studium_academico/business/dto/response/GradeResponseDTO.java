package com.studium.studium_academico.business.dto.response;

import com.studium.studium_academico.infrastructure.entity.TypeGrade;

import java.math.BigDecimal;
import java.util.UUID;

public record GradeResponseDTO(
        UUID id,
        BigDecimal grade,
        TypeGrade typeGrade,
        UUID registrationId,
        UUID recordedByTeacherId,
        UUID disciplineId,
        UUID classId
) {
}
