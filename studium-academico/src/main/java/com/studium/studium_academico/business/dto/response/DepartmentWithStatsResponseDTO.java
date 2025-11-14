package com.studium.studium_academico.business.dto.response;

import java.util.UUID;

public record DepartmentWithStatsResponseDTO(
        UUID id,
        String name,
        String description,
        int teacherCount,
        int courseCount
) {}