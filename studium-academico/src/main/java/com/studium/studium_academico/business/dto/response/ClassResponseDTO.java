package com.studium.studium_academico.business.dto.response;

import java.util.UUID;

public record ClassResponseDTO(
        UUID id,
        String name,
        String code_class,
        String academicYear,
        UUID courseId,
        String courseName
) {}