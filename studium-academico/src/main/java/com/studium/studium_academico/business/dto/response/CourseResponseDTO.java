package com.studium.studium_academico.business.dto.response;

import java.util.List;
import java.util.UUID;

public record CourseResponseDTO(
        UUID id,
        String name,
        String code_course,
        UUID departmentId,
        String departmentName,
        UUID institutionId,
        String institutionName
) {
}
