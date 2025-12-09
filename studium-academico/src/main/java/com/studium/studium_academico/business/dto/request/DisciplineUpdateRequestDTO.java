package com.studium.studium_academico.business.dto.request;

import java.util.UUID;

public record DisciplineUpdateRequestDTO(
        String name,
        Integer workload,
        String code,
        UUID teacherId
) {
}
