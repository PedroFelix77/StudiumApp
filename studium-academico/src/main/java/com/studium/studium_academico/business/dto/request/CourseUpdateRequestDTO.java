package com.studium.studium_academico.business.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record CourseUpdateRequestDTO(
        @NotBlank String name,
        @NotBlank String codeCourse,
        UUID departmentId,
        UUID institutionId
) {
}
