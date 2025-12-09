package com.studium.studium_academico.business.dto.response;

import com.studium.studium_academico.infrastructure.entity.StatusFrequency;

import java.time.LocalDate;
import java.util.UUID;

public record FrequencyResponseDTO(
        UUID id,
        LocalDate attendanceDate,
        StatusFrequency statusFrequency,
        String justification,

        RegistrationResponseDTO registration,
        ClassroomResponseDTO classroom,
        TeacherResponseDTO registeredByTeacher
) {
}
