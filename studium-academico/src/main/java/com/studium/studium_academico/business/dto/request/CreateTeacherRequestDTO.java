package com.studium.studium_academico.business.dto.request;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CreateTeacherRequestDTO(
        UserRequestDTO user,
        AddressRequestDTO address,
        UUID departmentId,
        List<UUID> courseIds,
        LocalDate hireDate,
        String specialty
) {
}
