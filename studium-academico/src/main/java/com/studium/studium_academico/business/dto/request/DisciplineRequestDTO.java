package com.studium.studium_academico.business.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DisciplineRequestDTO(
        String name,
        String code,
        Integer workload,
        UUID courseId,
        UUID teacherId
) {}
