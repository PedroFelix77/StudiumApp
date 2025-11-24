package com.studium.studium_academico.business.dto.response;

import java.util.List;
import java.util.UUID;

public record StudentResponseDTO(
        UUID id,
        UUID userId,        // ← Mapeado do User
        String name,        // ← Mapeado do User
        String email,       // ← Mapeado do User
        String registrationNumber,
        List<CourseResponseDTO> courses,
        List<GradeResponseDTO> grades
) {}
