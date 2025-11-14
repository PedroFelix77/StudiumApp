package com.studium.studium_academico.business.dto.response;

import java.util.UUID;

public record DepartmentResponseDTO(
        UUID id,
        String name,
        String description
) {
}
