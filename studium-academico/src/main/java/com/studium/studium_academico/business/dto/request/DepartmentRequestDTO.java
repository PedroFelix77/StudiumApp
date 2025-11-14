package com.studium.studium_academico.business.dto.request;

import jakarta.validation.constraints.NotBlank;

public record DepartmentRequestDTO(
        @NotBlank
        String name,
        String description
) {
}
