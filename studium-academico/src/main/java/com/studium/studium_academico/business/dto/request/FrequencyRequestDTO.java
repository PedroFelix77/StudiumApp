package com.studium.studium_academico.business.dto.request;

import com.studium.studium_academico.infrastructure.entity.StatusFrequency;

import java.time.LocalDate;
import java.util.UUID;

public record FrequencyRequestDTO (
        UUID registrationId,
        UUID classroomId,
        LocalDate attendanceDate,
        StatusFrequency statusFrequency,
        String justification
) {}
