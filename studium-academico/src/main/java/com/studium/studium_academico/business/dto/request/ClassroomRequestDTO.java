package com.studium.studium_academico.business.dto.request;

import java.time.LocalDate;
import java.util.UUID;

public record ClassroomRequestDTO(
        LocalDate date,
        String content,
        UUID classId,
        UUID disciplineId
) {
}
