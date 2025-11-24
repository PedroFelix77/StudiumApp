package com.studium.studium_academico.business.dto.response;

import java.util.UUID;

public record TeacherClassResponseDTO(
        UUID id,
        ClassResponseDTO classEntity,
        String subject
) {
}
