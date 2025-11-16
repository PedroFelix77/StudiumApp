package com.studium.studium_academico.business.dto.response;

import java.time.LocalDate;
import java.util.UUID;

public record RegistrationResponseDTO(
        UUID id,
        String registrationNumber,
        LocalDate dateRegistration,
        UUID studentId,
        String studentName,
        UUID courseId,
        String courseName,
        UUID classId,
        String className
) {
}
