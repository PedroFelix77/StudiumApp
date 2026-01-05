package com.studium.studium_academico.business.dto.response;

import com.studium.studium_academico.infrastructure.entity.StudentStatus;
import java.math.BigDecimal;
import java.util.List;

public record StudentTranscript(
        String studentName,
        String registrationNumber,
        String className,
        List<DisciplineResult> disciplineResults
) {
    public record DisciplineResult(
            String disciplineName,
            String disciplineCode,
            BigDecimal initialAverage,
            BigDecimal finalAverage,
            StudentStatus status
    ) {}
}