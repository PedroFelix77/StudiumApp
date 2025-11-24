package com.studium.studium_academico.business.dto.response;

import java.util.List;
import java.util.UUID;

public record InstitutionResponseDTO(
        UUID id,
        String name,
        String CNPJ,
        AddressResponseDTO addressResponseDTO,
        List<CourseResponseDTO> courses,
        List<DepartmentResponseDTO> departments
) {
}
