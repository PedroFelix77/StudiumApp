package com.studium.studium_academico.business.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record ClassroomResponseDTO(
        UUID id,
        String name,
        String location,
        Integer capacity,
        LocalDateTime startTime,
        LocalDateTime endTime,
        UUID classId
) {}