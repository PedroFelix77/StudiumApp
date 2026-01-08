package com.studium.studium_academico.business.dto.request;

import java.time.LocalDate;
import java.util.UUID;

public record ClassroomRequestDTO(
        String name,
        String code,
        Integer weeklyWorkLoad,
        UUID classId,
        UUID disciplineId,
        UUID teacherId
) {
}
