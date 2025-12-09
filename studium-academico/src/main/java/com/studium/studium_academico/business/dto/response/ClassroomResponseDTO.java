package com.studium.studium_academico.business.dto.response;

import java.time.LocalDate;
import java.util.UUID;

public record ClassroomResponseDTO(
        UUID id,
        LocalDate date,
        String content,
        ClassResponseDTO classEntity,
        DisciplineResponseDTO discipline
) {}
